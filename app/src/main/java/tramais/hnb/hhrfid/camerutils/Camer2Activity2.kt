package tramais.hnb.hhrfid.camerutils

import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.camerakit.api.camera2.ext.getCameraId
import com.camerakit.type.CameraFacing
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
import tramais.hnb.hhrfid.waterimage.WaterMaskUtil
import tramais.hnb.hhrfid.waterimage.WaterMaskView
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

class Camer2Activity2 : BaseActivity() {
    private var waterMaskView: WaterMaskView? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private val cameraManager: CameraManager by lazy {
        val context = this.applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraManager.getCameraId(CameraFacing.BACK)!!)
    }
    private lateinit var imageReader: ImageReader
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)
    private val animationTask: Runnable by lazy {
        Runnable {
            overlay.background = Color.argb(150, 255, 255, 255).toDrawable()
            overlay.postDelayed({
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
        val cameraId = cameraManager.getCameraId(CameraFacing.BACK)
        if (cameraId.isNullOrEmpty()) {

        }
        camera = openCamera(cameraManager, cameraId!!, cameraHandler)
        val size = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
                .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!
        imageReader = ImageReader.newInstance(
                size.width, size.height, ImageFormat.JPEG, IMAGE_BUFFER_SIZE
        )
        val targets = listOf(view_finder.holder.surface, imageReader.surface)
        session = createCaptureSession(camera, targets, cameraHandler)

        val captureRequest = camera.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
        ).apply { addTarget(view_finder.holder.surface) }
        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
    }

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
                this@Camer2Activity2.finish()
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

    private suspend fun createCaptureSession(
            device: CameraDevice,
            targets: List<Surface>,
            handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }


    private suspend fun takePhoto():
            CombinedCaptureResult = suspendCoroutine { cont ->
        /*  @Suppress("ControlFlowWithEmptyBody")
          while (imageReader.acquireNextImage() != null) {

          }
  */

        val imageQueue = ArrayBlockingQueue<Image>(IMAGE_BUFFER_SIZE)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireNextImage()
            imageQueue.add(image)
            // image.close()
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
                        val rotation = 270
                        val mirrored = characteristics.get(CameraCharacteristics.LENS_FACING) ==
                                CameraCharacteristics.LENS_FACING_FRONT
                        val exifOrientation = computeExifOrientation(rotation, mirrored)
                        cont.resume(
                                CombinedCaptureResult(
                                        image, result, exifOrientation, imageReader.imageFormat
                                )
                        )
                    }
                }
            }
        }, cameraHandler)
    }

    private var bitmap: Bitmap? = null
    var cdpath: String = ""
    var path: String = ""
    private suspend fun saveResult(result: CombinedCaptureResult): File = suspendCoroutine { cont ->
        when (result.format) {
            ImageFormat.JPEG, ImageFormat.DEPTH_JPEG -> {
                val buffer = result.image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
                var path = createFileNew(bytes)
                //   cont.resume(File(path))


            }
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
        capture_button.setOnApplyWindowInsetsListener { v, insets ->
            v.translationX = (-insets.systemWindowInsetRight).toFloat()
            v.translationY = (-insets.systemWindowInsetBottom).toFloat()
            insets.consumeSystemWindowInsets()
        }
        waterMaskView = WaterMaskView(this)
        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )
        waterMaskView!!.layoutParams = params
        view_finder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

            override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
            ) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {
                val previewSize = getPreviewOutputSize(
                        view_finder.display,
                        characteristics,
                        SurfaceHolder::class.java
                )

                view_finder.setAspectRatio(
                        previewSize.width,
                        previewSize.height
                )
                initializeCamera()
            }
        })
        relativeOrientation = OrientationLiveData(this, characteristics).apply {
            observe(this@Camer2Activity2, { orientation ->
                //  LogUtils.e("Orientation changed: $orientation")
            })
        }

    }

    var location_add: String? = "无法定位"
    var famername: String? = null
    var earTag: String? = null
    var remark: String? = null

    var mLocationClient: LocationClient? = null
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

            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->

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
        fenpei?.let { fenpei_ ->
            famername = if (fenpei_.farmerName.isNullOrBlank())
                "未知投保人"
            else fenpei_.farmerName

            earTag = if (fenpei_.EarTag.isNullOrBlank())
                "未知耳标号"
            else fenpei_.EarTag
            creatTime = fenpei_.createTime
            riskReason = fenpei_.riskReason
            setTitleText("被保险人:$famername")
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
            if (camera != null)
                camera.close()
            if (cameraThread != null)
                cameraThread.quitSafely()
            if (imageReaderThread != null)
                imageReaderThread.quitSafely()
            ims?.clear()
            if (mLocationClient != null) mLocationClient!!.stop()
            finish()
        }
        if (imv_pic != null)
            imv_pic!!.setOnClickListener { v: View? ->
                if (ims.size == 0) return@setOnClickListener
                if (path.isEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, path)
                if (!dialogImg.isShowing && !isFinishing) dialogImg.show()
            }
        capture_button.setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO) {
                takePhoto().use { result ->

                    it.post {
                        it.isEnabled = true

                    }
                    val output = saveResult(result)
                    if (output.extension == "jpg") {
                        val exif = ExifInterface(output.absolutePath)
                        exif.setAttribute(
                                ExifInterface.TAG_ORIENTATION, result.orientation.toString()
                        )
                        exif.saveAttributes()
                    }
                }

            }
        }
    }

    var ims: ArrayList<String> = ArrayList()


    companion object {
        private val TAG = Camer2Activity2::class.java.simpleName
        private const val IMAGE_BUFFER_SIZE: Int = 50
        private const val IMAGE_CAPTURE_TIMEOUT_MILLIS: Long = 3000

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
        if (max(outHeight, outWidth) > 1024) {
            val scaleFactorX = outWidth / 1024 + 1
            val scaleFactorY = outHeight / 1024 + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true)
    }

    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(ExifInterface.ORIENTATION_ROTATE_90) }

    private val bitmaps: ArrayList<String?>? = ArrayList()
    var crators: MutableList<String> = ArrayList()
    var waterInfos: MutableList<String> = ArrayList()
    private fun createFileNew(bytes: ByteArray): String {
        waterInfos.clear()
        crators.clear()
        /*水印顺序：被保险人，标的名称，耳标号，出险原因，出险时间，查勘时间,经纬度，查勘地点，*/
        if (remark == "only_photo") {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_camer
            waterInfos.add("被保险人:$famername")
            waterInfos.add("耳标号:$earTag")
            waterInfos.add("标的名称:$riskQty")
            waterInfos.add("出险原因:$riskReason")
            waterInfos.add("出险时间:$creatTime")
            waterInfos.add("查勘时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            waterInfos.add("经度:$longitude 纬度:$latitude")

            val userName = PreferUtils.getString(this, Constants.UserName)
            var name = if (userName.isNullOrBlank()) {
                "未知"
            } else {
                userName
            }
            for (item in 1..20) {
                crators.add(name)
            }

        }

        val decodeByteArray = decodeBitmap(bytes, 0, bytes.size)
        waterMaskView!!.setBackData(crators, decodeByteArray.height.toFloat(), decodeByteArray.width.toFloat())
        waterMaskView!!.setLeftData(waterInfos)
        waterMaskView!!.setLocation(location_add!!)
        cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"


        val photo_name = System.currentTimeMillis().toString() + ".jpg"
        playSound(R.raw.camera_click)
        val path = saveWaterMask(waterMaskView, decodeByteArray, cdpath, photo_name)
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                Glide.with(this@Camer2Activity2).load(path).into(imv_pic!!)
                if (!bitmaps!!.contains(path)) bitmaps!!.add(path)
                if (remark == "only_photo") {
                    scan_total!!.text = "当前第 " + (bitmaps.size) + " 张"
                }
                LogUtils.e("path  $path")
            }
        }

        return path

    }

    private fun saveWaterMask(waterMaskView: WaterMaskView?, sourBitmap: Bitmap, path_: String, name_: String): String {
        try {
            var waterBitmap = WaterMaskUtil.loadBitmapFromView(waterMaskView)
            var watermarkBitmap = WaterMaskUtil.createWaterMaskLeftBottom(this, sourBitmap, waterBitmap, 0, 0)
            return ImageUtils.saveBitmap(this, watermarkBitmap, path_, name_)
        } catch (e: Exception) {
            LogUtils.e("e  ${e.message}")
        }
        return ""

    }
}
