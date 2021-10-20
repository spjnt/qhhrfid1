package tramais.hnb.hhrfid.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.hardware.Camera
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.camera.CameraSize
import tramais.hnb.hhrfid.camera.CameraSizeCalculator
import java.io.IOException
import java.util.*

@SuppressLint("ShowToast")
class CustomCameraView : FrameLayout, SurfaceHolder.Callback, Camera.AutoFocusCallback {
    private var camera: Camera? = null
    private var surface_holder: SurfaceHolder? = null
    private var surface_camera: SurfaceView? = null
    private var viewWidth = 0
    private var viewHeight = 0
    private var onTakePictureInfo: OnTakePictureInfo? = null
    private var view_focus: View? = null
    private var frameLayout: PreviewFrameLayout? = null
    private var mode = MODE.NONE // 默认模式


    /**
     * 拍照
     */
    private var safeToTakePicture = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        Companion.context = context

        LayoutInflater.from(context).inflate(R.layout.preview_frame, this)
        frameLayout = findViewById<View>(R.id.frame_layout) as PreviewFrameLayout
        surface_camera = findViewById<View>(R.id.camera_preview) as SurfaceView
        view_focus = findViewById(R.id.view_focus)
        surface_holder = surface_camera!!.holder
        surface_holder!!.addCallback(this)


        frameLayout!!.setOnTouchListener { v, event ->
            if (event!!.action == MotionEvent.ACTION_DOWN) {
                val width = view_focus!!.width
                val height = view_focus!!.height
                view_focus!!.background = resources.getDrawable(
                        R.drawable.ic_focus_focusing)
                view_focus!!.x = event.x - width / 2
                view_focus!!.y = event.y - height / 2
            } else if (event.action == MotionEvent.ACTION_UP) {
                mode = MODE.FOCUSING
                focusOnTouch(event)
            }
            true
        }
        surface_holder!!.setKeepScreenOn(true)
        // CameraTimes =1;
    }

    /**
     * 设置相机焦距 *
     */
    private fun setZoom(mValue: Int) {
        val mParams = camera!!.parameters
        mParams.zoom = mValue
        camera!!.parameters = mParams
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera = cameraInstance
        try {
            if (camera != null && surface_holder != null) camera!!.setPreviewDisplay(surface_holder)
        } catch (e: IOException) {
        }
        updateCameraParameters()
        if (camera != null) camera!!.startPreview()
        safeToTakePicture = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (camera != null && holder != null) {
            camera!!.stopPreview()
            camera!!.release()
            safeToTakePicture = false
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        viewWidth = MeasureSpec.getSize(widthSpec)
        viewHeight = MeasureSpec.getSize(heightSpec)
        super.onMeasure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY))
    }// attempt to get a Camera instance// get camerainfo

    // get cameras number
    var orientation: Int = 0
    private val cameraInstance: Camera?
        private get() {
            var c: Camera? = null
            try {
                var cameraCount = 0
                val cameraInfo = Camera.CameraInfo()
                orientation = cameraInfo.orientation
                cameraCount = Camera.getNumberOfCameras() // get cameras number
                for (camIdx in 0 until cameraCount) {
                    Camera.getCameraInfo(camIdx, cameraInfo) // get camerainfo
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        try {
                            c = Camera.open(camIdx)
                        } catch (e: RuntimeException) {
                        }
                    }
                }
                if (c == null) {
                    c = Camera.open(0) // attempt to get a Camera instance
                }
            } catch (e: Exception) {
                Toast.makeText(context, "摄像头打开失败！", Toast.LENGTH_SHORT)
            }
            if (c == null) {
                Toast.makeText(context, "摄像头打开失败！", Toast.LENGTH_SHORT)
            }
            return c
        }

    private fun updateCameraParameters() {
        if (camera != null) {
            val p = camera!!.parameters

            setCameraDisplayOrientation(context!!)
        }
    }

    //旋转预览角度
    private fun setCameraDisplayOrientation(context: Context?) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(0, info)

        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation: Int = windowManager.defaultDisplay.rotation * 90
        var degrees = 0

        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }


        var result = (info.orientation - degrees + 360) % 360

        //旋转预览的角度
        camera!!.setDisplayOrientation(result)
        LogUtils.e("result  $result")
        //-----------------------------
        //旋转生成的照片角度
        val param = camera!!.parameters
        param.setRotation(result)
        val photoSizes = getPhotoSizes()
//        val chooseVideoSize = chooseVideoSize(photoSizes)
//        LogUtils.e("chooseVideoSize  ${chooseVideoSize!!.height}  ${chooseVideoSize!!.width}")
        val cameraSizeCalculator = CameraSizeCalculator(photoSizes)
        val target = cameraSizeCalculator.findClosestSizeContainingTarget(when (result % 180 == 0) {
            true -> CameraSize(width, height)
            false -> CameraSize(height, width)
        })
   //     LogUtils.e("target  ${target.width}  ${target.height}")
//        for (item in photoSizes) {
//            LogUtils.e("item  ${item!!.height}  ${item!!.width}")
//        }
        param.setPictureSize(target.width, target.height)
        camera!!.parameters = param
    }


    fun getPhotoSizes(): Array<CameraSize> {
        return camera?.parameters?.supportedPictureSizes?.map { CameraSize(it.width, it.height) }
                ?.toTypedArray()!!

    }

    /**
     * 找到最合适的显示分辨率 （防止预览图像变形）
     *
     * @param parameters
     * @return
     */
    private fun findBestPreviewSize(parameters: Camera.Parameters): Camera.Size? {

        //系统支持的所有预览分辨率
        var previewSizeValueString: String? = null
        previewSizeValueString = parameters["preview-size-values"]
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters["preview-size-value"]
        }
        if (previewSizeValueString == null) {
            return camera!!.Size(screenWH.widthPixels,
                    screenWH.heightPixels)
        }
        var bestX = 0f
        var bestY = 0f
        var tmpRadio = 0f
        var viewRadio = 0f
        if (viewWidth != 0 && viewHeight != 0) {
            viewRadio = (Math.min(viewWidth.toFloat(), viewHeight.toFloat())
                    / Math.max(viewWidth.toFloat(), viewHeight.toFloat()))
        }
        val COMMA_PATTERN = previewSizeValueString.split(",").toTypedArray()
        for (prewsizeString in COMMA_PATTERN) {
            var prewsizeString = prewsizeString.trim { it <= ' ' }
            val dimPosition = prewsizeString.indexOf('x')
            if (dimPosition == -1) {
                continue
            }
            var newX = 0f
            var newY = 0f
            try {
                newX = prewsizeString
                        .substring(0, dimPosition).toFloat()
                newY = prewsizeString
                        .substring(dimPosition + 1).toFloat()
            } catch (e: NumberFormatException) {
                continue
            }
            val radio = Math.min(newX, newY) / Math.max(newX, newY)
            if (tmpRadio == 0f) {
                tmpRadio = radio
                bestX = newX
                bestY = newY
            } else if (tmpRadio != 0f && Math.abs(radio - viewRadio) < Math.abs(tmpRadio
                            - viewRadio)) {
                tmpRadio = radio
                bestX = newX
                bestY = newY
            } else if (tmpRadio != 0f && Math.abs(radio - viewRadio) == Math.abs(tmpRadio
                            - viewRadio)) {
                if (Math.abs(newY - viewWidth) < Math.abs(bestY - viewWidth)) {
                    tmpRadio = radio
                    bestX = newX
                    bestY = newY
                }
            }
        }
        return if (bestX > 0 && bestY > 0) {
            camera!!.Size(bestX.toInt(), bestY.toInt())
        } else null
    }

    fun focusOnTouch(event: MotionEvent) {
        val location = IntArray(2)
        frameLayout!!.getLocationOnScreen(location)
        val focusRect = calculateTapArea(view_focus!!.width,
                view_focus!!.height, 1f, event.rawX, event.rawY,
                location[0], location[0] + frameLayout!!.width, location[1],
                location[1] + frameLayout!!.height)
        val meteringRect = calculateTapArea(view_focus!!.width,
                view_focus!!.height, 1.5f, event.rawX, event.rawY,
                location[0], location[0] + frameLayout!!.width, location[1],
                location[1] + frameLayout!!.height)
        if (camera != null) {
            val parameters = camera!!.parameters
            if (parameters != null) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                if (parameters.maxNumFocusAreas > 0) {
                    val focusAreas: MutableList<Camera.Area> = ArrayList()
                    focusAreas.add(Camera.Area(focusRect, 1000))
                    parameters.focusAreas = focusAreas
                }
                if (parameters.maxNumMeteringAreas > 0) {
                    val meteringAreas: MutableList<Camera.Area> = ArrayList()
                    meteringAreas.add(Camera.Area(meteringRect, 1000))
                    parameters.meteringAreas = meteringAreas
                }
                try {
                    camera!!.parameters = parameters
                    camera!!.autoFocus(this)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun calculateTapArea(focusWidth: Int, focusHeight: Int,
                         areaMultiple: Float, x: Float, y: Float, previewleft: Int,
                         previewRight: Int, previewTop: Int, previewBottom: Int): Rect {
        val areaWidth = (focusWidth * areaMultiple).toInt()
        val areaHeight = (focusHeight * areaMultiple).toInt()
        val centerX = (previewleft + previewRight) / 2
        val centerY = (previewTop + previewBottom) / 2
        val unitx = (previewRight.toDouble() - previewleft.toDouble()) / 2000
        val unity = (previewBottom.toDouble() - previewTop.toDouble()) / 2000
        val left = clamp(((x - areaWidth / 2 - centerX) / unitx).toInt(),
                -1000, 1000)
        val top = clamp(((y - areaHeight / 2 - centerY) / unity).toInt(),
                -1000, 1000)
        val right = clamp((left + areaWidth / unitx).toInt(), -1000, 1000)
        val bottom = clamp((top + areaHeight / unity).toInt(), -1000, 1000)
        return Rect(left, top, right, bottom)
    }

    fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) return max
        return if (x < min) min else x
    }

    protected val screenWH: DisplayMetrics
        protected get() {
            var dMetrics = DisplayMetrics()
            dMetrics = this.resources.displayMetrics
            return dMetrics
        }

    fun takePicture() {
        if (camera != null && safeToTakePicture) {
            safeToTakePicture = false
            camera!!.takePicture(null, null, { data, camera ->
                if (data != null && data.isNotEmpty()) {
                    onTakePictureInfo!!.onTakePictureInofo(true, data)
                } else {
                    onTakePictureInfo!!.onTakePictureInofo(false, null)
                }
                camera.stopPreview()
                camera.startPreview()
                safeToTakePicture = true
            })
            mode = MODE.NONE
        }
    }

    fun openCloseFlash(isOpen: Boolean) {
        try {
            //  val m_Camera = Camera.open()

            val mParameters: Camera.Parameters = camera!!.parameters
            if (isOpen) {
                mParameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            } else {
                mParameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }

            camera!!.parameters = mParameters
        } catch (ex: java.lang.Exception) {
            LogUtils.e("ex  ${ex.message}")
        }
    }

    fun setOnTakePictureInfo(_onTakePictureInfo: OnTakePictureInfo?) {
        onTakePictureInfo = _onTakePictureInfo
    }

    override fun onAutoFocus(success: Boolean, _camera: Camera) {
        if (success) {
            mode = MODE.FOCUSED
            view_focus!!.background = resources.getDrawable(
                    R.drawable.ic_focus_focused)
        } else {
            mode = MODE.FOCUSFAIL
            view_focus!!.background = resources.getDrawable(
                    R.drawable.ic_focus_failed)
        }
        setFocusView()
    }

    private fun setFocusView() {
        Handler().postDelayed({ view_focus!!.background = null }, (1 * 1000).toLong())
    }

    /**
     * 模式 NONE：无 FOCUSING：正在聚焦. FOCUSED:聚焦成功 FOCUSFAIL：聚焦失败
     */
    private enum class MODE {
        NONE, FOCUSING, FOCUSED, FOCUSFAIL
    }

    interface OnTakePictureInfo {
        fun onTakePictureInofo(_success: Boolean, _file: ByteArray?)
    }

    companion object {
        private var context: Context? = null

    }
}