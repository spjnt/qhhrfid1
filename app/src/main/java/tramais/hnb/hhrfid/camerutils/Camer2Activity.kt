/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tramais.hnb.hhrfid.camerutils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import androidx.core.graphics.drawable.toDrawable
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.util.*
import java.io.Closeable
import java.io.File
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max

class Camer2Activity : BaseActivity() {

    private var latitude = 0.0
    private var longitude = 0.0
    private val cameraManager: CameraManager by lazy {
        val context = this.applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics("0")
    }

    /** Readers used as buffers for camera still shots */
    private lateinit var imageReader: ImageReader

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** Performs recording animation of flashing screen */
    private val animationTask: Runnable by lazy {
        Runnable {
            // Flash white animation
            overlay.background = Color.argb(150, 255, 255, 255).toDrawable()
            // Wait for ANIMATION_FAST_MILLIS
            overlay.postDelayed({
                // Remove white flash animation
                overlay.background = null
            }, 50L)
        }
    }


    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }

    private val imageReaderHandler = Handler(imageReaderThread.looper)

    private lateinit var camera: CameraDevice
    private lateinit var session: CameraCaptureSession

    private lateinit var relativeOrientation: OrientationLiveData


    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        // Open the selected camera
        var intent = Intent()
        val pixelFormat = intent.getIntExtra("pixel_format", 256)

        camera = openCamera(cameraManager, "0", cameraHandler)

        // Initialize an image reader which will be used to capture still photos
        val size = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
                .getOutputSizes(pixelFormat).maxByOrNull { it.height * it.width }!!
        imageReader = ImageReader.newInstance(
                size.width, size.height, pixelFormat, IMAGE_BUFFER_SIZE
        )
        // Log.e("cammer", "" + args.cameraId + "  " + args.pixelFormat)
        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(view_finder.holder.surface, imageReader.surface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        val captureRequest = camera.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
        ).apply { addTarget(view_finder.holder.surface) }


        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)


    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
            manager: CameraManager,
            cameraId: String,
            handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
                this@Camer2Activity.finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Starts a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine
     */
    private suspend fun createCaptureSession(
            device: CameraDevice,
            targets: List<Surface>,
            handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Create a capture session using the predefined targets; this also involves defining the
        // session state callback to be notified of when the session is ready
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Helper function used to capture a still image using the [CameraDevice.TEMPLATE_STILL_CAPTURE]
     * template. It performs synchronization between the [CaptureResult] and the [Image] resulting
     * from the single capture, and outputs a [CombinedCaptureResult] object.
     */
    private suspend fun takePhoto():
            CombinedCaptureResult = suspendCoroutine { cont ->

        // Flush any images left in the image reader
        @Suppress("ControlFlowWithEmptyBody")
        while (imageReader.acquireNextImage() != null) {
        }

        val imageQueue = ArrayBlockingQueue<Image>(IMAGE_BUFFER_SIZE)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireNextImage()

            imageQueue.add(image)
        }, imageReaderHandler)

        val captureRequest = session.device.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE
        ).apply { addTarget(imageReader.surface) }
        session.capture(captureRequest.build(), object : CameraCaptureSession.CaptureCallback() {

            override fun onCaptureStarted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    timestamp: Long,
                    frameNumber: Long
            ) {
                super.onCaptureStarted(session, request, timestamp, frameNumber)
                view_finder.post(animationTask)
            }

            override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                val resultTimestamp = result.get(CaptureResult.SENSOR_TIMESTAMP)

                val exc = TimeoutException("Image dequeuing took too long")
                val timeoutRunnable = Runnable { cont.resumeWithException(exc) }
                imageReaderHandler.postDelayed(timeoutRunnable, IMAGE_CAPTURE_TIMEOUT_MILLIS)

                @Suppress("BlockingMethodInNonBlockingContext")
                lifecycleScope.launch(cont.context) {
                    while (true) {

                        val image = imageQueue.take()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                image.format != ImageFormat.DEPTH_JPEG &&
                                image.timestamp != resultTimestamp
                        ) continue

                        imageReaderHandler.removeCallbacks(timeoutRunnable)
                        imageReader.setOnImageAvailableListener(null, null)

                        while (imageQueue.size > 0) {
                            imageQueue.take().close()
                        }

                        // Compute EXIF orientation metadata
                        val rotation = 270
                        val mirrored = characteristics.get(CameraCharacteristics.LENS_FACING) ==
                                CameraCharacteristics.LENS_FACING_FRONT
                        val exifOrientation = computeExifOrientation(rotation, mirrored)

                        // Build the result and resume progress
                        cont.resume(
                                CombinedCaptureResult(
                                        image, result, exifOrientation, imageReader.imageFormat
                                )
                        )

                        // There is no need to break out of the loop, this coroutine will suspend
                    }
                }
            }
        }, cameraHandler)
    }

    private var bitmap: Bitmap? = null
    var cdpath: String = ""
    var path: String = ""

    /** Helper function used to save a [CombinedCaptureResult] into a [File] */
    private suspend fun saveResult(result: CombinedCaptureResult): File = suspendCoroutine { cont ->
        when (result.format) {

            // When the format is JPEG or DEPTH JPEG we can simply save the bytes as-is
            ImageFormat.JPEG, ImageFormat.DEPTH_JPEG -> {
                val buffer = result.image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
                val createFile = createFile(this, bytes)
                /*  imv_pic.bringToFront()
                  imv_pic!!.visibility = View.VISIBLE
                  Glide.with(this@Camer2Activity).load(createFile).into(imv_pic!!)*/
                cont.resume(createFile)


            }


            // No other formats are supported by this sample
            else -> {
                val exc = RuntimeException("Unknown image format: ${result.image.format}")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_camera)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
        ims?.clear()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    override fun initView() {
        /*  capture_button.setOnApplyWindowInsetsListener { v, insets ->
              v.translationX = (-insets.systemWindowInsetRight).toFloat()
              v.translationY = (-insets.systemWindowInsetBottom).toFloat()
              insets.consumeSystemWindowInsets()
          }

          imv_pic.setOnApplyWindowInsetsListener { v, insets ->
              v.translationX = (-insets.systemWindowInsetRight).toFloat()
              v.translationY = (-insets.systemWindowInsetBottom).toFloat()
              insets.consumeSystemWindowInsets()
          }*/

        view_finder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

            override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
            ) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {
                // Selects appropriate preview size and configures view finder
                val previewSize = getPreviewOutputSize(
                        view_finder.display,
                        characteristics,
                        SurfaceHolder::class.java
                )

                view_finder.setAspectRatio(
                        previewSize.width,
                        previewSize.height
                )

                // To ensure that size is set, initialize camera in the view's thread
                initializeCamera()
            }
        })

        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(this, characteristics).apply {
            observe(this@Camer2Activity, { orientation ->
                LogUtils.e("Orientation changed: $orientation")
            })
        }

    }

    var location_add: String? = "无法定位"
    var famername: String? = null
    var earTag: String? = null
    var remark: String? = null

    var mLocationClient: LocationClient? = null

    //    var come_in_time: String = ""
    var textList: MutableList<String> = ArrayList()
    var sdk_path: String? = null

    var creatTime: String? = null
    var riskReason: String? = null
    var riskQty: String? = null
    var insure_type: String? = null
    private var photo_num = 0
    override fun initData() {
        if (NetUtil.checkNet(this)) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            if (mLocationClient != null) mLocationClient!!.start()

            //声明LocationClient类
            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                LogUtils.e("add  $add $lat  $log")
                if (add.isNullOrEmpty() || add.isNullOrBlank()) {
                    location_add = "无法定位"
                } else {
                    location_add = add
                }

                latitude = lat
                longitude = log
                mLocationClient!!.stop()
            })
        }

        val fenpei = intent.getSerializableExtra("fenpei") as FenPei?

        photo_num = intent.getIntExtra("photo_num", 0)
//        come_in_time = TimeUtil.getTime(Constants.MMddHHmmss)
        fenpei?.let { fenpei_ ->
            famername = if (fenpei_.farmerName.isNullOrBlank())
                "未知投保人"
            else fenpei_.farmerName

            earTag = if (fenpei_.EarTag.isNullOrBlank())
                "未知耳标号"
            else fenpei_.EarTag
            creatTime = fenpei_.createTime
            riskReason = fenpei_.riskReason
            setTitleText("投保人:$famername")
            remark = fenpei_.fRemark
            riskQty = fenpei_.riskQty
//            insure_type = fenpei_.fCoinsFlag //险种
//            location_add = fenpei_.riskAddress

//            latitude = fenpei_.lat
//            longitude = fenpei_.log
            if (remark != "only_photo") {

            }
        }


    }

    override fun initListner() {
        imv_gallery.setOnClickListener {

            finish()
            //数据是使用Intent返回
//            val intent = Intent()
//            //把返回数据存入Intent
//            intent.putExtra("imgs", ims)
//            //设置返回数据
//            setResult(RESULT_OK, intent)
//            finish()

        }
        if (imv_pic != null)
            imv_pic!!.setOnClickListener { v: View? ->
                if (ims == null || ims.size == 0) return@setOnClickListener
                if (path.isNullOrEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, path)
                if (dialogImg != null && !isFinishing) dialogImg.show()
            }
        capture_button.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                takePhoto().use { result ->

                    val output = saveResult(result)
                    LogUtils.e("outPut  ${output.absolutePath}")

                    if (output.extension == "jpg") {
                        val exif = ExifInterface(output.absolutePath)
                        exif.setAttribute(
                                ExifInterface.TAG_ORIENTATION, result.orientation.toString()
                        )
                        exif.saveAttributes()

                    }

                }


                it.post {
                    it.isEnabled = true

                }
            }
        }
    }

    var ims: ArrayList<String> = ArrayList()
    private fun createFile(context: Context, bytes: ByteArray): File {
        textList.clear()
        /*水印顺序：被保险人，标的名称，耳标号，出险原因，出险时间，查勘时间,经纬度，查勘地点，*/
        if (remark == "only_photo") {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_camer
            textList.add("被保险人:$famername")
            if (insure_type == "养殖险")
                textList.add("耳标号:$earTag")
            textList.add("标的名称:$riskQty")

            textList.add("出险原因:$riskReason")
            textList.add("出险时间:$creatTime")
            textList.add("查勘时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            textList.add("经度:$longitude 纬度:$latitude")
            val length = if (location_add.isNullOrEmpty()) {
                0
            } else {
                location_add!!.length
            }
            var one_length = 14
            if (length >= one_length) {
                var first = location_add!!.substring(0, one_length)
                var end = location_add!!.substring(one_length, length)
                textList.add("📍:$first")
                textList.add(end)

            } else {
                textList.add("📍:$location_add")
            }

        } else {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_middle_animal
            textList.add("被保险人:$famername")
            textList.add("时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            textList.add("经度:$longitude 纬度:$latitude")
            val length = if (location_add.isNullOrEmpty()) {
                0
            } else {
                location_add!!.length
            }

            var one_length = 14
            if (length >= one_length) {
                var first = location_add!!.substring(0, one_length)
                var end = location_add!!.substring(one_length, length)
                textList.add("📍:$first")
                textList.add(end)
            } else {
                textList.add("📍:$location_add")
            }
        }
        cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"
        val decodeByteArray = decodeBitmap(bytes, 0, bytes.size)

        playSound(R.raw.camera_click)
        lifecycleScope.launch(Dispatchers.IO) {
            val getimage = ImageUtils.getimageOnly(decodeByteArray)
            var task = WateImagsTask()
            bitmap = task.addWater(context, textList, getimage)
            var photo_name = System.currentTimeMillis().toString() + ".jpg"
            path = ImageUtils.saveBitmap(context, bitmap, cdpath, photo_name)



            withContext(Dispatchers.Main) {

                ims.add(path)

                scan_total.bringToFront()
                scan_total.text = "当前第 ${ims.size} 张"
                Glide.with(this@Camer2Activity).load(path).into(imv_pic)
                if (ims.size == 20) {
                    showStr("请先点击完成，保存数据")
                    return@withContext
                }
            }

        }

        return File(path)
    }

    companion object {
        private val TAG = Camer2Activity::class.java.simpleName

        /** Maximum number of images that will be held in the reader's buffer */
        private const val IMAGE_BUFFER_SIZE: Int = 20

        /** Maximum time allowed to wait for the result of an image capture */
        private const val IMAGE_CAPTURE_TIMEOUT_MILLIS: Long = 5000

        /** Helper data class used to hold capture metadata with their associated image */
        data class CombinedCaptureResult(
                val image: Image,
                val metadata: CaptureResult,
                val orientation: Int,
                val format: Int
        ) : Closeable {
            override fun close() = image.close()
        }


    }

    override fun onPause() {
        super.onPause()

        if (mLocationClient != null) mLocationClient!!.stop()

    }

    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        // Keep Bitmaps at less than 1 MP
        if (max(outHeight, outWidth) > 1024) {
            val scaleFactorX = outWidth / 1024 + 1
            val scaleFactorY = outHeight / 1024 + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    /** Utility function used to decode a [Bitmap] from a byte array */
    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {

        // Load bitmap from given buffer
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)

        // Transform bitmap orientation using provided metadata
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true)
    }

    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(ExifInterface.ORIENTATION_ROTATE_90) }
}
