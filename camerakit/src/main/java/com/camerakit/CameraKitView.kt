package com.camerakit

import android.Manifest

import androidx.annotation.RestrictTo
import androidx.annotation.IntDef

import com.camerakit.type.CameraFacing
import com.camerakit.type.CameraFlash
import android.view.ViewGroup
import android.view.View.MeasureSpec
import com.camerakit.type.CameraSize
import android.os.Build
import android.app.Activity
import android.content.ContextWrapper
import com.camerakit.CameraKitView.ImageCallback
import com.camerakit.CameraPreview.PhotoCallback
import com.camerakit.CameraKitView.VideoCallback
import android.content.pm.PackageManager
import com.camerakit.CameraKit.Facing
import com.camerakit.CameraPreview.LifecycleState
import com.camerakit.CameraKit.Flash
import com.camerakit.CameraKit.Focus
import com.camerakit.CameraKit.SensorPreset
import com.camerakit.CameraKit.PreviewEffect
import jpegkit.Jpeg

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import java.lang.RuntimeException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.ArrayList
import com.camerakit.R

/**
 * CameraKitView provides a high-level, easy to implement, and safe to use way to work with
 * the Android camera.
 *
 * @since v1.0.0
 */
class CameraKitView : GestureLayout {
    /**
     * Represents manifest runtime-permissions that may be used.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        flag = true,
        value = [PERMISSION_CAMERA, PERMISSION_MICROPHONE, PERMISSION_STORAGE, PERMISSION_LOCATION]
    )
    internal annotation class Permission

    /**
     *
     */
    interface CameraListener {
        /**
         *
         */
        fun onOpened()

        /**
         *
         */
        fun onClosed()
    }

    /**
     *
     */
    interface PreviewListener {
        /**
         *
         */
        fun onStart()

        /**
         *
         */
        fun onStop()
    }

    /**
     *
     */
    interface ErrorListener {
        /**
         * @param view
         * @param error
         */
        fun onError(view: CameraKitView?, error: CameraException?)
    }

    /**
     *
     */
    interface GestureListener {
        /**
         * @param view
         * @param x
         * @param y
         */
        fun onTap(view: CameraKitView?, x: Float, y: Float)

        /**
         * @param view
         * @param x
         * @param y
         */
        fun onLongTap(view: CameraKitView?, x: Float, y: Float)

        /**
         * @param view
         * @param x
         * @param y
         */
        fun onDoubleTap(view: CameraKitView?, x: Float, y: Float)

        /**
         * @param view
         * @param ds
         * @param dsx
         * @param dsy
         */
        fun onPinch(view: CameraKitView?, ds: Float, dsx: Float, dsy: Float)
    }

    /**
     *
     */
    interface PermissionsListener {
        fun onPermissionsSuccess()
        fun onPermissionsFailure()
    }

    /**
     *
     */
    interface ImageCallback {
        /**
         * @param view
         * @param jpeg
         */
        fun onImage(view: CameraKitView?, jpeg: ByteArray?)
    }

    /**
     *
     */
    interface VideoCallback {
        /**
         * @param view
         * @param video
         */
        fun onVideo(view: CameraKitView?, video: Any?)
    }

    interface FrameCallback {
        /**
         * @param view
         * @param jpeg
         */
        fun onFrame(view: CameraKitView?, jpeg: ByteArray?)
    }
    /**
     * @return
     * @see .setAdjustViewBounds
     */
    /**
     * @param adjustViewBounds
     */
    var adjustViewBounds = false
    /**
     * @return
     * @see .setAspectRatio
     */
    /**
     * @param aspectRatio
     */
    var aspectRatio = 0f
    private var mFacing = 0
    private var mFlash = 0
    /**
     * @return one of [CameraKit.Focus]'s constants.
     * @see .setFocus
     */
    /**
     * @param focus one of [CameraKit.Focus]'s constants.
     * @see CameraKit.FOCUS_OFF
     *
     * @see CameraKit.FOCUS_AUTO
     *
     * @see CameraKit.FOCUS_CONTINUOUS
     */
    @get:Focus
    var focus = 0
    /**
     * @return
     * @see .setZoomFactor
     */
    /**
     * @param zoomFactor
     */
    var zoomFactor = 0f
    /**
     * @return one of [CameraKit.SensorPreset]'s constants.
     * @see .setSensorPreset
     */
    /**
     * @param sensorPreset one of [CameraKit.SensorPreset]'s constants.
     * @see CameraKit.SENSOR_PRESET_NONE
     *
     * @see CameraKit.SENSOR_PRESET_ACTION
     *
     * @see CameraKit.SENSOR_PRESET_PORTRAIT
     *
     * @see CameraKit.SENSOR_PRESET_LANDSCAPE
     *
     * @see CameraKit.SENSOR_PRESET_NIGHT
     *
     * @see CameraKit.SENSOR_PRESET_NIGHT_PORTRAIT
     *
     * @see CameraKit.SENSOR_PRESET_THEATRE
     *
     * @see CameraKit.SENSOR_PRESET_BEACH
     *
     * @see CameraKit.SENSOR_PRESET_SNOW
     *
     * @see CameraKit.SENSOR_PRESET_SUNSET
     *
     * @see CameraKit.SENSOR_PRESET_STEADYPHOTO
     *
     * @see CameraKit.SENSOR_PRESET_FIREWORKS
     *
     * @see CameraKit.SENSOR_PRESET_SPORTS
     *
     * @see CameraKit.SENSOR_PRESET_PARTY
     *
     * @see CameraKit.SENSOR_PRESET_CANDLELIGHT
     *
     * @see CameraKit.SENSOR_PRESET_BARCODE
     */
    @get:SensorPreset
    var sensorPreset = 0
    /**
     * @return one of [CameraKit.PreviewEffect]'s constants.
     * @see .setPreviewEffect
     */
    /**
     * @param previewEffect one of [CameraKit.PreviewEffect]'s constants.
     * @see CameraKit.PREVIEW_EFFECT_NONE
     *
     * @see CameraKit.PREVIEW_EFFECT_MONO
     *
     * @see CameraKit.PREVIEW_EFFECT_SOLARIZE
     *
     * @see CameraKit.PREVIEW_EFFECT_SEPIA
     *
     * @see CameraKit.PREVIEW_EFFECT_POSTERIZE
     *
     * @see CameraKit.PREVIEW_EFFECT_WHITEBOARD
     *
     * @see CameraKit.PREVIEW_EFFECT_BLACKBOARD
     *
     * @see CameraKit.PREVIEW_EFFECT_AQUA
     */
    @get:PreviewEffect
    var previewEffect = 0
    /**
     * @return
     * @see .setPermissions
     */
    /**
     * @param permissions
     */
    @get:Permission
    var permissions = 0
    private var mImageMegaPixels = 0f
    private var mImageJpegQuality = 0
    /**
     * @return
     * @see .setGestureListener
     */
    /**
     * @param gestureListener
     */
    var gestureListener: GestureListener? = null
    /**
     * @return CameraListener
     */
    /**
     * @param cameraListener
     */
    var cameraListener: CameraListener? = null
    /**
     * @return PreviewListener
     */
    /**
     * @param previewListener
     */
    var previewListener: PreviewListener? = null
    /**
     * @return ErrorListener
     * @see .setErrorListener
     */
    /**
     * @param errorListener
     */
    var errorListener: ErrorListener? = null
    private var mPermissionsListener: PermissionsListener? = null
    private var mCameraPreview: CameraPreview? = null

    constructor(context: Context) : super(context) {
        obtainAttributes(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        obtainAttributes(context, attrs)
    }

    private fun obtainAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CameraKitView)
        adjustViewBounds = a.getBoolean(R.styleable.CameraKitView_android_adjustViewBounds, false)
        aspectRatio = a.getFloat(R.styleable.CameraKitView_camera_aspectRatio, -1f)
        mFacing = a.getInteger(R.styleable.CameraKitView_camera_facing, CameraKit.FACING_BACK)
        if (cameraFacing === CameraFacing.FRONT) {
            mFacing = CameraKit.FACING_FRONT
        }
        mFlash = a.getInteger(R.styleable.CameraKitView_camera_flash, CameraKit.FLASH_OFF)
        if (cameraFlash === CameraFlash.ON) {
            mFlash = CameraKit.FLASH_ON
        }
        focus = a.getInteger(R.styleable.CameraKitView_camera_focus, CameraKit.FOCUS_AUTO)
        zoomFactor = a.getFloat(R.styleable.CameraKitView_camera_zoomFactor, 1.0f)
        permissions = a.getInteger(R.styleable.CameraKitView_camera_permissions, PERMISSION_CAMERA)
        mImageMegaPixels = a.getFloat(R.styleable.CameraKitView_camera_imageMegaPixels, 2f)
        mImageJpegQuality = a.getInteger(R.styleable.CameraKitView_camera_imageJpegQuality, 100)
        a.recycle()
        mCameraPreview = CameraPreview(getContext())
        addView(mCameraPreview)
        mCameraPreview!!.listener = object : CameraPreview.Listener {
            override fun onCameraOpened() {
                if (cameraListener != null) {
                    post { cameraListener!!.onOpened() }
                }
            }

            override fun onCameraClosed() {
                if (cameraListener != null) {
                    post { cameraListener!!.onClosed() }
                }
            }

            override fun onPreviewStarted() {
                if (previewListener != null) {
                    post { previewListener!!.onStart() }
                }
            }

            override fun onPreviewStopped() {
                if (previewListener != null) {
                    post { previewListener!!.onStop() }
                }
            }
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        if (adjustViewBounds) {
            val layoutParams = layoutParams
            if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                throw CameraException("android:adjustViewBounds=true while both layout_width and layout_height are setView to wrap_content - only 1 is allowed.")
            } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                var width = 0
                val height = MeasureSpec.getSize(heightMeasureSpec)
                if (aspectRatio > 0) {
                    width = (height * aspectRatio).toInt()
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                } else if (mCameraPreview != null && mCameraPreview!!.surfaceSize.area() > 0) {
                    val (width1, height1) = mCameraPreview!!.surfaceSize
                    width = (height.toFloat() / height1.toFloat() * width1).toInt()
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                }
            } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                val width = MeasureSpec.getSize(widthMeasureSpec)
                var height = 0
                if (aspectRatio > 0) {
                    height = (width * aspectRatio).toInt()
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                } else if (mCameraPreview != null && mCameraPreview!!.surfaceSize.area() > 0) {
                    val (width1, height1) = mCameraPreview!!.surfaceSize
                    height = (width.toFloat() / width1.toFloat() * height1).toInt()
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    public override fun onTap(x: Float, y: Float) {
        if (gestureListener != null) {
            gestureListener!!.onTap(this, x, y)
        }
    }

    public override fun onLongTap(x: Float, y: Float) {
        if (gestureListener != null) {
            gestureListener!!.onLongTap(this, x, y)
        }
    }

    public override fun onDoubleTap(x: Float, y: Float) {
        if (gestureListener != null) {
            gestureListener!!.onDoubleTap(this, x, y)
        }
    }

    public override fun onPinch(ds: Float, dsx: Float, dsy: Float) {
        if (gestureListener != null) {
            gestureListener!!.onPinch(this, ds, dsx, dsy)
        }
    }

    fun onStart() {
        if (isInEditMode) {
            return
        }
        val missingPermissions = missingPermissions
        if (Build.VERSION.SDK_INT >= 23 && missingPermissions.size > 0) {
            var activity: Activity? = null
            var context = context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    activity = context
                }
                context = context.baseContext
            }
            if (activity != null) {
                val requestPermissions: MutableList<String> = ArrayList()
                val rationalePermissions: MutableList<String> = ArrayList()
                for (permission in missingPermissions) {
                    if (!activity.shouldShowRequestPermissionRationale(permission)) {
                        requestPermissions.add(permission)
                    } else {
                        rationalePermissions.add(permission)
                    }
                }
                if (requestPermissions.size > 0) {
                    activity.requestPermissions(
                        requestPermissions.toTypedArray(),
                        PERMISSION_REQUEST_CODE
                    )
                }
                if (rationalePermissions.size > 0 && mPermissionsListener != null) {
                    mPermissionsListener!!.onPermissionsFailure()
                }
            }
            return
        }
        if (mPermissionsListener != null) {
            mPermissionsListener!!.onPermissionsSuccess()
        }
        flash = mFlash
        imageMegaPixels = mImageMegaPixels
        cameraFacing =
            if (facing == CameraKit.FACING_BACK) CameraFacing.BACK else CameraFacing.FRONT
        mCameraPreview!!.start(cameraFacing!!)
    }

    fun onStop() {
        if (isInEditMode) {
            return
        }
        mCameraPreview!!.stop()
    }

    fun onResume() {
        if (isInEditMode) {
            return
        }
        mCameraPreview!!.resume()
    }

    /**
     *
     */
    fun onPause() {
        if (isInEditMode) {
            return
        }
        mCameraPreview!!.pause()
    }

    /**
     * @param callback
     */
    fun captureImage(callback: ImageCallback) {
        mCameraPreview!!.capturePhoto(object : PhotoCallback {
            override fun onCapture(jpeg: ByteArray) {
                post { callback.onImage(this@CameraKitView, jpeg) }
            }
        })
    }

    /**
     *
     */
    fun startVideo() {}

    /**
     *
     */
    fun stopVideo() {}

    /**
     * @param callback
     */
    fun captureVideo(callback: VideoCallback?) {}

    /**
     *
     */
    fun captureFrame(callback: FrameCallback?) {}

    /**
     *
     */
    fun setFrameCallback(callback: FrameCallback?) {}

    /**
     * @return
     */
    private val missingPermissions: List<String>
        private get() {
            val manifestPermissions: MutableList<String> = ArrayList()
            if (Build.VERSION.SDK_INT < 23) {
                return manifestPermissions
            }
            if (permissions or PERMISSION_CAMERA == permissions) {
                val manifestPermission = Manifest.permission.CAMERA
                if (context.checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_DENIED) {
                    manifestPermissions.add(manifestPermission)
                }
            }
            if (permissions or PERMISSION_MICROPHONE == permissions) {
                val manifestPermission = Manifest.permission.RECORD_AUDIO
                if (context.checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_DENIED) {
                    manifestPermissions.add(manifestPermission)
                }
            }
            if (permissions or PERMISSION_STORAGE == permissions) {
                val manifestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                if (context.checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_DENIED) {
                    manifestPermissions.add(manifestPermission)
                }
            }
            if (permissions or PERMISSION_LOCATION == permissions) {
                val manifestPermission = Manifest.permission.ACCESS_FINE_LOCATION
                if (context.checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_DENIED) {
                    manifestPermissions.add(manifestPermission)
                }
            }
            return manifestPermissions
        }

    fun setPermissionsListener(permissionsListener: PermissionsListener?) {
        mPermissionsListener = permissionsListener
    }

    /**
     */
    fun requestPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            val manifestPermissions = missingPermissions
            if (manifestPermissions.size > 0) {
                activity.requestPermissions(
                    manifestPermissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var approvedPermissions = 0
            var deniedPermissions = 0
            for (i in permissions.indices) {
                var flag = 0
                when (permissions[i]) {
                    Manifest.permission.CAMERA -> {
                        flag = PERMISSION_CAMERA
                    }
                    Manifest.permission.RECORD_AUDIO -> {
                        flag = PERMISSION_MICROPHONE
                    }
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        flag = PERMISSION_STORAGE
                    }
                }
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    approvedPermissions = approvedPermissions or flag
                } else {
                    deniedPermissions = deniedPermissions or flag
                }
            }
            onStart()
        }
    }
    /**
     * @return one of [CameraKit.Facing]'s constants.
     * @see .setFacing
     */
    /**
     * @param facing one of [CameraKit.Facing]'s constants.
     * @see CameraKit.FACING_BACK
     *
     * @see CameraKit.FACING_FRONT
     */
    @get:Facing
    var facing: Int
        get() = mFacing
        set(facing) {
            mFacing = facing
            when (mCameraPreview!!.lifecycleState) {
                LifecycleState.PAUSED, LifecycleState.STARTED -> {
                    onStop()
                    onStart()
                }
                LifecycleState.RESUMED -> {
                    onStop()
                    onStart()
                    onResume()
                }
            }
        }

    /**
     *
     */
    fun toggleFacing() {
        facing = if (facing == CameraKit.FACING_BACK) {
            CameraKit.FACING_FRONT
        } else {
            CameraKit.FACING_BACK
        }
    }

    /**
     * Determine if device is capable of flash
     * @return boolean if device is capable of flash
     */
    fun hasFlash(): Boolean {
        return mCameraPreview!!.hasFlash()
    }

    /**
     * Get supported flash types on device
     * @return array of supported flash types
     */
    val supportedFlashTypes: Array<CameraFlash>?
        get() = mCameraPreview!!.getSupportedFlashTypes()
    /**
     * @return one of [CameraKit.Flash]'s constants.
     * @see .setFlash
     */
    /**
     * @param flash one of [CameraKit.Flash]'s constants.
     * @see CameraKit.FLASH_OFF
     *
     * @see CameraKit.FLASH_ON
     *
     * @see CameraKit.FLASH_AUTO
     *
     * @see CameraKit.FLASH_TORCH
     */
    @get:Flash
    var flash: Int
        get() = mFlash
        set(flash) {
            mFlash = flash
            try {
                when (flash) {
                    CameraKit.FLASH_OFF -> {
                        cameraFlash = CameraFlash.OFF
                    }
                    CameraKit.FLASH_ON -> {
                        cameraFlash = CameraFlash.ON
                    }
                    CameraKit.FLASH_AUTO -> {
                        throw CameraException("FLASH_AUTO is not supported in this version of CameraKit.")
                    }
                    CameraKit.FLASH_TORCH -> {
                        throw CameraException("FLASH_TORCH is not supported in this version of CameraKit.")
                    }
                }
            } catch (exception: CameraException) {
                Log.e("CameraException: Flash", exception.message!!)
                return
            }
            mCameraPreview!!.flash = cameraFlash!!
        }
    var imageMegaPixels: Float
        get() = mImageMegaPixels
        set(imageMegaPixels) {
            mImageMegaPixels = imageMegaPixels
            mCameraPreview!!.imageMegaPixels = mImageMegaPixels
        }

    class GestureListenerAdapter : GestureListener {
        /**
         * @see GestureListener.onTap
         */
        override fun onTap(view: CameraKitView?, x: Float, y: Float) {}

        /**
         * @see GestureListener.onLongTap
         */
        override fun onLongTap(view: CameraKitView?, x: Float, y: Float) {}

        /**
         * @see GestureListener.onDoubleTap
         */
        override fun onDoubleTap(view: CameraKitView?, x: Float, y: Float) {}

        /**
         * @see GestureListener.onPinch
         */
        override fun onPinch(view: CameraKitView?, ds: Float, dsx: Float, dsy: Float) {}
    }

    /**
     * Delete CameraListener
     */
    fun removeCameraListener() {
        cameraListener = null
    }

    /**
     * Delete PreviewListener
     */
    fun removePreviewListener() {
        previewListener = null
    }

    /**
     * Delete ErrorListener
     */
    fun removeErrorListener() {
        errorListener = null
    }

    val previewResolution: CameraSize?
        get() = if (mCameraPreview!!.previewSize.area() == 0) {
            null
        } else mCameraPreview!!.previewSize
    val photoResolution: CameraSize?
        get() = if (mCameraPreview!!.photoSize.area() == 0) {
            null
        } else mCameraPreview!!.photoSize

    /**
     *
     */
    interface JpegCallback {
        fun onJpeg(jpeg: Jpeg?)
    }

    /**
     *
     */
    class Size(val width: Int, val height: Int) : Comparable<Size> {
        override fun equals(o: Any?): Boolean {
            return if (o == null) {
                false
            } else if (this === o) {
                true
            } else if (o is Size) {
                val size = o
                width == size.width && height == size.height
            } else {
                false
            }
        }

        override fun toString(): String {
            return width.toString() + "x" + height
        }

        override fun hashCode(): Int {
            return height xor (width shl Integer.SIZE / 2 or (width ushr Integer.SIZE / 2))
        }

        override fun compareTo(another: Size): Int {
            return width * height - another.width * another.height
        }
    }

    /**
     *
     */
    class CameraException : RuntimeException {
        constructor() : super() {}
        constructor(message: String?) : super(message) {}
        constructor(message: String?, cause: Throwable?) : super(message, cause) {}

        val isFatal: Boolean
            get() = false
    }

    companion object {
        /**
         * Request code for a runtime permissions intent.
         */
        private const val PERMISSION_REQUEST_CODE = 99107

        /**
         * Flag for handling requesting the [android.Manifest.permission.CAMERA]
         * permission.
         */
        const val PERMISSION_CAMERA = 1

        /**
         * Flag for handling requesting the [android.Manifest.permission.RECORD_AUDIO]
         * permission.
         */
        const val PERMISSION_MICROPHONE = 1 shl 1

        /**
         * Flag for handling requesting the [android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
         * permission.
         */
        const val PERMISSION_STORAGE = 1 shl 2

        /**
         * Flag for handling requesting the [android.Manifest.permission.ACCESS_FINE_LOCATION]
         * permission.
         */
        const val PERMISSION_LOCATION = 1 shl 3
        private var cameraFacing: CameraFacing? = null
        private var cameraFlash: CameraFlash? = null
    }
}