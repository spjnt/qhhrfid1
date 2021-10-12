package tramais.hnb.hhrfid.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.util.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class Camera2Preview @JvmOverloads constructor(
        private val mContext: Context?,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : TextureView(
        mContext!!, attrs, defStyle
), CameraView {
    private var mWindowManager: WindowManager? = null
    private var mRatioWidth = 0
    private var mRatioHeight = 0
    private var mCameraCount = 0
    private var mCurrentCameraFacing = CameraCharacteristics.LENS_FACING_BACK
    private fun init() {
        mWindowManager = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * 同生命周期 onResume
     */
    override fun onResume() {

        startBackgroundThread()
        // 当关闭并打开屏幕时，SurfaceTexture已经可用，并且不会调用“onSurfaceTextureAvailable”。
        // 在这种情况下，我们可以打开相机并从这里开始预览（否则，我们将等待，直到Surface在SurfaceTextureListener中准备好）。
        if (isAvailable) {
            openCamera(width, height)
        } else {
            surfaceTextureListener = mSurfaceTextureListener
        }
    }

    /**
     * 同生命周期 onPause
     */
    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
    }

    /**
     * 启动拍照的方法
     */
    override fun takePicture() {
        takePictureInternal()
    }

    private var mTakePictureCallback: CameraView.TakePictureCallback? = null
    override fun takePicture(takePictureCallback: CameraView.TakePictureCallback?) {

        mTakePictureCallback = takePictureCallback
        takePictureInternal()
    }

    private fun takePictureInternal() {

        if (mCurrentCameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
            lockFocus()
        } else {
            runPrecaptureSequence()
        }
    }

    /**
     * 设置图片的保存路径(文件夹)
     *
     * @param pictureSavePath
     */
    private var mPictureSaveDir: String? = null
    override fun setPictureSavePath(pictureSavePath: String?) {
        mPictureSaveDir = pictureSavePath
    }

    override fun switchCameraFacing() {
        if (mCameraCount > 1) {
            mCurrentCameraFacing =
                    if (mCurrentCameraFacing == CameraCharacteristics.LENS_FACING_BACK) CameraCharacteristics.LENS_FACING_FRONT else CameraCharacteristics.LENS_FACING_BACK
            closeCamera()
            openCamera(width, height)
        }
    }

    /**
     * 设置此视图的宽高比。
     * 视图的大小将基于从参数计算的比率来测量。
     * 请注意，参数的实际大小并不重要，也就是说，setAspectRatio（2, 3）setAspectRatio（4, 6）会得到相同的结果。
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }

    companion object {
        private const val TAG = "Camera2Preview"
        private val ORIENTATIONS: SparseIntArray = SparseIntArray()

        /**
         * 相机状态: 显示相机预览
         */
        private const val STATE_PREVIEW = 0

        /**
         * 相机状态: 等待焦点被锁定
         */
        private const val STATE_WAITING_LOCK = 1

        /**
         * 相机状态: 等待曝光前的状态。
         */
        private const val STATE_WAITING_PRECAPTURE = 2

        /**
         * 相机状态: 等待曝光状态非预先捕获的东西.
         */
        private const val STATE_WAITING_NON_PRECAPTURE = 3

        /**
         * 相机状态: 照片拍摄
         */
        private const val STATE_PICTURE_TAKEN = 4

        /**
         * 最大的预览宽度
         */
        private const val MAX_PREVIEW_WIDTH = 1920

        /**
         * 最大的预览高度
         */
        private const val MAX_PREVIEW_HEIGHT = 1080

        /**
         * 给定相机支持的`Size`的`choices`，
         * 选择至少与相应TextureView大小一样大、最多与相应最大大小一样大、且纵横比与指定值匹配的最小一个。
         * 如果不存在这样的尺寸，则选择最大尺寸与相应的最大尺寸一样大，并且其纵横比与指定值匹配的最大尺寸。
         *
         * @param choices           摄像机支持预期输出类的大小列表。
         * @param textureViewWidth  TextureView相对于传感器坐标的宽度
         * @param textureViewHeight TextureView相对于传感器坐标的高度
         * @param maxWidth          可选择的最大宽度
         * @param maxHeight         可选择的最大高度
         * @param aspectRatio       宽高比
         * @return 最佳的 `Size`, 或任意一个，如果没有足够大的话
         */
        private fun chooseOptimalSize(
                choices: Array<Size>, textureViewWidth: Int,
                textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size
        ): Size {

            // 收集至少与预览表面一样大的支持分辨率。
            val bigEnough: MutableList<Size> = ArrayList()
            // 收集小于预览表面的支持的分辨率
            val notBigEnough: MutableList<Size> = ArrayList()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
                    if (option.width >= textureViewWidth &&
                            option.height >= textureViewHeight
                    ) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // 挑那些最小中的足够大的。如果没有足够大的，选择最大的那些不够大的。
            return if (bigEnough.size > 0) {
                Collections.min(bigEnough, CompareSizesByArea())
            } else if (notBigEnough.size > 0) {
                Collections.max(notBigEnough, CompareSizesByArea())
            } else {
                Log.e(TAG, "Couldn't find any suitable preview size")
                choices[0]
            }
        }

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }

    /**
     * [SurfaceTextureListener] handles several lifecycle events on a
     * [TextureView].
     */
    private val mSurfaceTextureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

    /**
     * ID of the current [CameraDevice].
     */
    private var mCameraId: String? = null

    /**
     * 相机预览要用到的[CameraCaptureSession] .
     */
    private var mCaptureSession: CameraCaptureSession? = null

    /**
     * A reference to the opened [CameraDevice].
     */
    private var mCameraDevice: CameraDevice? = null

    /**
     * 相机预览的 [Size]
     */
    private var mPreviewSize: Size? = null

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
            // 当相机打开时调用此方法。我们在这里开始相机预览。
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            Log.e(TAG, "CameraDevice.StateCallback onError errorCode= $error")
        }
    }

    /**
     * 用于运行不应该阻止UI的任务的附加线程。
     */
    private var mBackgroundThread: HandlerThread? = null

    /**
     * 在后台运行任务的Handler。
     */
    private var mBackgroundHandler: Handler? = null

    /**
     * An [ImageReader] 用于处理图像捕获(抓拍).
     */
    private var mImageReader: ImageReader? = null

    /**
     * 图片文件
     */
    private var mPictureFile: File? = null

    /**
     * 这是[ImageReader]的回调对象. 当静态图像准备好保存时，将回调"onImageAvailable"
     */
    private val mOnImageAvailableListener: ImageReader.OnImageAvailableListener =
            object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader) {

                    //检测外部存储是否存在
                    //根据时间戳生成图片名称

                    if (FileUtils.checkSDCard()) {
                        val outputMediaFile = FileUtils.getOutputMediaFile()

                        mPictureFile = outputMediaFile

                        if (mPictureFile == null) {
                            Log.e(TAG, "error creating media file, check storage permissions")
                            if (mTakePictureCallback != null) {
                                mTakePictureCallback!!.error("error creating media file, check storage permissions")
                            } else {
                                mTakePictureCallback
                            }
                            return
                        }
                    } else {
                        mPictureFile = FileUtils.getOutputMediaFile()
                    }

                    mBackgroundHandler!!.post(ImageSaver(reader.acquireNextImage(), mPictureFile))
                }
            }

    /**
     * [CaptureRequest.Builder] for the camera1 preview
     */
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

    /**
     * [CaptureRequest] generated by [.mPreviewRequestBuilder]
     */
    private var mPreviewRequest: CaptureRequest? = null

    /**
     * 拍照相机的当前状态
     *
     * @see .mCaptureCallback
     */
    private var mState = STATE_PREVIEW

    /**
     * A [Semaphore] 防止相机关闭前退出应用程序。
     */
    private val mCameraOpenCloseLock = Semaphore(1)

    /**
     * 当前的相机设备是否支持Flash。
     */
    private var mFlashSupported = false

    /**
     * 相机传感器的方向
     */
    private var mSensorOrientation = 0

    /**
     * A [CameraCaptureSession.CaptureCallback] 处理JPEG捕获的相关事件
     */
    private val mCaptureCallback: CameraCaptureSession.CaptureCallback =
            object : CameraCaptureSession.CaptureCallback() {

                private fun process(result: CaptureResult) {

                    when (mState) {
                        STATE_PREVIEW -> {
                        }
                        STATE_WAITING_LOCK -> {
                            val afState: Int? = result.get<Int>(CaptureResult.CONTROL_AF_STATE)
                            if (afState == null) {
                                captureStillPicture()
                            } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                    CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                            ) {
                                // CONTROL_AE_STATE can be null on some devices
                                val aeState: Int? = result.get<Int>(CaptureResult.CONTROL_AF_STATE)
                                if (aeState == null ||
                                        aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED
                                ) {
                                    mState = STATE_PICTURE_TAKEN
                                    captureStillPicture()
                                } else {
                                    runPrecaptureSequence()
                                }
                            }
                        }
                        STATE_WAITING_PRECAPTURE -> {

                            // CONTROL_AE_STATE can be null on some devices
                            val aeState: Int? = result.get<Int>(CaptureResult.CONTROL_AE_STATE)
                            if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                                mState = STATE_WAITING_NON_PRECAPTURE
                            }
                        }
                        STATE_WAITING_NON_PRECAPTURE -> {

                            // CONTROL_AE_STATE can be null on some devices
                            val aeState: Int? = result.get<Int>(CaptureResult.CONTROL_AE_STATE)
                            if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                                mState = STATE_PICTURE_TAKEN
                                captureStillPicture()
                            }
                        }
                    }
                }

                override fun onCaptureProgressed(
                        @NonNull session: CameraCaptureSession,
                        @NonNull request: CaptureRequest,
                        @NonNull partialResult: CaptureResult
                ) {
                    process(partialResult)
                }

                override fun onCaptureCompleted(
                        @NonNull session: CameraCaptureSession,
                        @NonNull request: CaptureRequest,
                        @NonNull result: TotalCaptureResult
                ) {
                    process(result)
                }
            }

    /**
     * 设置与摄像机相关的成员变量。
     *
     * @param width  相机预览可用尺寸的宽度
     * @param height 相机预览可用尺寸的高度
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {

        val manager: CameraManager =
                mContext!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraIdList: Array<String> = manager.getCameraIdList()
            mCameraCount = cameraIdList.size
            for (cameraId in cameraIdList) {
                val characteristics: CameraCharacteristics =
                        manager.getCameraCharacteristics(cameraId)

                //判断当前摄像头是前置还是后置摄像头
                val facing: Int? = characteristics.get<Int>(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing != mCurrentCameraFacing) {
                    continue
                }
                val map: StreamConfigurationMap = characteristics.get<StreamConfigurationMap>(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                ) ?: continue

                // 对于静态图像捕获，我们使用最大可用的大小。
                val largest = Collections.max(
                        listOf<Size>(*map.getOutputSizes(ImageFormat.JPEG)),
                        CompareSizesByArea()
                )

                mImageReader = ImageReader.newInstance(
                        largest.width, largest.height,
                        ImageFormat.JPEG,  /*maxImages*/2
                )
                mImageReader!!.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler
                )

                // 找出是否需要交换尺寸以获得相对与传感器坐标的预览大小
                val displayRotation: Int = mWindowManager!!.getDefaultDisplay().getRotation()
                //检验条件
                mSensorOrientation =
                        characteristics.get<Int>(CameraCharacteristics.SENSOR_ORIENTATION)!!
                var swappedDimensions = false
                when (displayRotation) {
                    Surface.ROTATION_0, Surface.ROTATION_180 -> if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                        swappedDimensions = true
                    }
                    Surface.ROTATION_90, Surface.ROTATION_270 -> if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                        swappedDimensions = true
                    }
                    else -> Log.e(TAG, "Display rotation is invalid: $displayRotation")
                }
                val displaySize = Point()
                mWindowManager!!.defaultDisplay.getSize(displaySize)
                var rotatedPreviewWidth = width
                var rotatedPreviewHeight = height
                var maxPreviewWidth = displaySize.x
                var maxPreviewHeight = displaySize.y
                if (swappedDimensions) {
                    rotatedPreviewWidth = height
                    rotatedPreviewHeight = width
                    maxPreviewWidth = displaySize.y
                    maxPreviewHeight = displaySize.x
                }
                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH
                }
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT
                }

                // 危险，W.R.！尝试使用太大的预览大小可能超过相机总线的带宽限制，导致高清的预览，但存储垃圾捕获数据。
                mPreviewSize = chooseOptimalSize(
                        map.getOutputSizes<SurfaceTexture>(
                                SurfaceTexture::class.java
                        ),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest
                )
                LogUtils.e("largest  ${mPreviewSize!!.width}   ${mPreviewSize!!.height}")
                // 我们将TextureView的宽高比与我们选择的预览大小相匹配。
                val orientation: Int = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setAspectRatio(
                            maxPreviewWidth, maxPreviewHeight
                    )
                } else {
                    setAspectRatio(
                            maxPreviewHeight,maxPreviewWidth
                    )
                }

                //检验是否支持flash
                val available: Boolean =
                        characteristics.get<Boolean>(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                mFlashSupported = available ?: false
                mCameraId = cameraId
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            //抛出空指针一般代表当前设备不支持Camera2API
            Log.e(TAG, "This device doesn't support Camera2 API.")
        }
    }

    /**
     * 打开指定的相机（mCameraId）
     */
    @SuppressLint("MissingPermission")
    private fun openCamera(width: Int, height: Int) {

        setUpCameraOutputs(width, height)
        configureTransform(width, height)
        val manager: CameraManager =
                mContext!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera1 opening.")
            }
            if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.CAMERA
                    ) !== PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            manager.openCamera(mCameraId!!, mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera1 opening.", e)
        }
    }

    /**
     * 关闭相机
     */
    private fun closeCamera() {

        try {
            mCameraOpenCloseLock.acquire()
            if (null != mCaptureSession) {
                mCaptureSession!!.close()
                mCaptureSession = null
            }
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
            if (null != mImageReader) {
                mImageReader!!.close()
                mImageReader = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera1 closing.", e)
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    /**
     * 启动后台线程和Handler.
     */
    private fun startBackgroundThread() {

        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    /**
     * 停止后台线程和Handler.
     */
    private fun stopBackgroundThread() {

        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 创建一个新的 [CameraCaptureSession] 用于相机预览.
     */
    private fun createCameraPreviewSession() {

        try {
            val texture: SurfaceTexture = surfaceTexture!!
            val outSize = Point()
            mWindowManager!!.defaultDisplay.getRealSize(outSize)
            val x: Int = outSize.x
            val y: Int = outSize.y
            LogUtils.e("x = $x,y = $y")
            // 我们将默认缓冲区的大小设置为我们想要的相机预览的大小。
            texture.setDefaultBufferSize(x, y)

            // 我们需要开始预览输出Surface
            val surface: Surface = Surface(texture)

            // 我们建立了一个具有输出Surface的捕获器。
            mPreviewRequestBuilder =
                    mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)

            // 这里，我们创建了一个用于相机预览的CameraCaptureSession
            mCameraDevice!!.createCaptureSession(
                    Arrays.asList(surface, mImageReader!!.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                            // 相机已经关闭
                            if (null == mCameraDevice) {
                                return
                            }

                            // 当session准备好后，我们开始显示预览
                            mCaptureSession = cameraCaptureSession
                            try {
                                // 相机预览时应连续自动对焦
                                mPreviewRequestBuilder!!.set<Int>(
                                        CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                                )
                                // 设置闪光灯在必要时自动打开
                                setAutoFlash(mPreviewRequestBuilder)

                                // 最终,显示相机预览
                                mPreviewRequest = mPreviewRequestBuilder!!.build()
                                mCaptureSession!!.setRepeatingRequest(
                                        mPreviewRequest!!,
                                        mCaptureCallback, mBackgroundHandler
                                )
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onConfigureFailed(
                                @NonNull cameraCaptureSession: CameraCaptureSession
                        ) {
                            Log.e(TAG, "CameraCaptureSession.StateCallback onConfigureFailed")
                        }
                    }, null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 配置必要的 [Matrix] 转换为 `mTextureView`.
     *
     *
     * 该方法应该在setUpCameraOutputs中确定相机预览大小以及“mTextureView”的大小固定之后调用。
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (null == mPreviewSize || null == mContext) {
            return
        }

        val rotation: Int = mWindowManager!!.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect =
                RectF(0f, 0f, mPreviewSize!!.height.toFloat(), mPreviewSize!!.width.toFloat())
        val centerX: Float = viewRect.centerX()
        val centerY: Float = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale =
                    (viewHeight.toFloat() / mPreviewSize!!.height).coerceAtLeast(viewWidth.toFloat() / mPreviewSize!!.width)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        setTransform(matrix)
    }

    /**
     * 锁定焦点作为静态图像捕获的第一步
     */
    private fun lockFocus() {
        try {
            //  这里是让相机锁定焦点
            mPreviewRequestBuilder!!.set<Int>(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START
            )
            // 告知 #mCaptureCallback 等待锁
            mState = STATE_WAITING_LOCK
            mCaptureSession!!.capture(
                    mPreviewRequestBuilder!!.build(), mCaptureCallback,
                    mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 运行预捕获序列捕获一张静态图片。
     *
     *
     * 这个方法应该在我们从得到mCaptureCallback的响应后调用
     */
    private fun runPrecaptureSequence() {
        try {
            // 这就是如何告诉相机触发。
            mPreviewRequestBuilder!!.set<Int>(
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
            // 告知 #mCaptureCallback 等待设置预捕获序列。
            mState = STATE_WAITING_PRECAPTURE
            mCaptureSession!!.capture(
                    mPreviewRequestBuilder!!.build(), mCaptureCallback,
                    mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e("CameraAccessException", e.message.toString())
            e.printStackTrace()
        }
    }

    /**
     * 捕获一张静态图片
     * 这个方法应该在我们从得到mCaptureCallback的响应后调用
     */
    private fun captureStillPicture() {
        try {
            if (null == mCameraDevice) {
                return
            }
            // 这是 CaptureRequest.Builder ，我们用它来进行拍照
            val captureBuilder: CaptureRequest.Builder = mCameraDevice!!.createCaptureRequest(
                    CameraDevice.TEMPLATE_STILL_CAPTURE
            )
            captureBuilder.addTarget(mImageReader!!.surface)

            //  使用相同的AE和AF模式作为预览。
            captureBuilder.set<Int>(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            setAutoFlash(captureBuilder)

            // 方向
            val rotation: Int = mWindowManager!!.defaultDisplay.rotation
            captureBuilder.set<Int>(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))
            val CaptureCallback: CameraCaptureSession.CaptureCallback =
                    object : CameraCaptureSession.CaptureCallback() {
                        override fun onCaptureCompleted(
                                @NonNull session: CameraCaptureSession,
                                @NonNull request: CaptureRequest,
                                @NonNull result: TotalCaptureResult
                        ) {
                            if (mTakePictureCallback != null) {
                                if (mPictureFile != null)
                                    mTakePictureCallback!!.success(mPictureFile!!.absolutePath)
                                else
                                    mTakePictureCallback!!.error("mPictureFile is null")
                            }
                            unlockFocus()
                        }
                    }
            mCaptureSession!!.stopRepeating()
            mCaptureSession!!.abortCaptures()
            mCaptureSession!!.capture(captureBuilder.build(), CaptureCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 从指定的屏幕旋转中检索JPEG方向。
     *
     * @param rotation 图片旋转
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private fun getOrientation(rotation: Int): Int {
        // 对于大多数设备，传感器定向是90，对于某些设备（例如Nexus 5X）是270。
        //我们必须考虑到这一点，并适当的旋转JPEG。
        //对于取向为90的设备，我们只需从方向返回映射即可。
        //对于方向为270的设备，我们需要旋转JPEG 180度。
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360
    }

    /**
     * 解锁焦点.
     *
     *
     * 此方法应该在静态图片捕获序列结束后调用
     */
    private fun unlockFocus() {
        try {
            // 重置自动对焦触发
            mPreviewRequestBuilder!!.set<Int>(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
            )
            setAutoFlash(mPreviewRequestBuilder)
            mCaptureSession!!.capture(
                    mPreviewRequestBuilder!!.build(), mCaptureCallback,
                    mBackgroundHandler
            )
            // 在此之后，相机将回到正常的预览状态。
            mState = STATE_PREVIEW
            mCaptureSession!!.setRepeatingRequest(
                    mPreviewRequest!!, mCaptureCallback,
                    mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder?) {
        if (mFlashSupported) {
            requestBuilder!!.set<Int>(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
        }
    }

    /**
     * 将JPEG[Image]保存并放到指定的文件中
     */
    private class ImageSaver internal constructor(
            /**
             * The JPEG image
             */
            private val mImage: Image,
            /**
             * The file we save the image into.
             */
            private val mFile: File?
    ) : Runnable {
        override fun run() {
            val buffer = mImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer[bytes]
            var output: FileOutputStream? = null
            try {
                output = FileOutputStream(mFile)
                output.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mImage.close()
                if (null != output) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * 根据它们的区域比较两个的大小 `Size`。
     */
    internal class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(
                    lhs.width.toLong() * lhs.height -
                            rhs.width.toLong() * rhs.height
            )
        }
    }

    init {
        init()
    }
}