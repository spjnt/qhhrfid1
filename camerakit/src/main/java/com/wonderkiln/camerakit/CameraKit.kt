package com.wonderkiln.camerakit


import android.content.res.Resources
import android.view.LayoutInflater

class CameraKit {
    internal object Internal {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    }

    object Constants {
        const val PERMISSION_REQUEST_CAMERA = 16
        const val FACING_BACK = 0
        const val FACING_FRONT = 1
        const val FLASH_OFF = 0
        const val FLASH_ON = 1
        const val FLASH_AUTO = 2
        const val FLASH_TORCH = 3
        const val FOCUS_OFF = 0
        const val FOCUS_CONTINUOUS = 1
        const val FOCUS_TAP = 2
        const val FOCUS_TAP_WITH_MARKER = 3
        const val METHOD_STANDARD = 0
        const val METHOD_STILL = 1
        const val PERMISSIONS_STRICT = 0
        const val PERMISSIONS_LAZY = 1
        const val PERMISSIONS_PICTURE = 2
        const val VIDEO_QUALITY_480P = 0
        const val VIDEO_QUALITY_720P = 1
        const val VIDEO_QUALITY_1080P = 2
        const val VIDEO_QUALITY_2160P = 3
        const val VIDEO_QUALITY_HIGHEST = 4
        const val VIDEO_QUALITY_LOWEST = 5
        const val VIDEO_QUALITY_QVGA = 6
    }

    internal object Defaults {
        const val DEFAULT_FACING = Constants.FACING_BACK
        const val DEFAULT_FLASH = Constants.FLASH_OFF
        const val DEFAULT_FOCUS = Constants.FOCUS_CONTINUOUS
        const val DEFAULT_PINCH_TO_ZOOM = true
        const val DEFAULT_ZOOM = 1f
        const val DEFAULT_METHOD = Constants.METHOD_STANDARD
        const val DEFAULT_PERMISSIONS = Constants.PERMISSIONS_STRICT
        const val DEFAULT_VIDEO_QUALITY = Constants.VIDEO_QUALITY_480P
        const val DEFAULT_JPEG_QUALITY = 100
        const val DEFAULT_VIDEO_BIT_RATE = 0
        const val DEFAULT_CROP_OUTPUT = false
        const val DEFAULT_DOUBLE_TAP_TO_TOGGLE_FACING = false
        const val DEFAULT_ADJUST_VIEW_BOUNDS = false
    }
}