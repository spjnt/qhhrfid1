package com.camerakit


import androidx.annotation.RestrictTo
import androidx.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 */
object CameraKit {
    /**
     * The device points away from the screen.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK]
     * Camera2: [android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK]
     *
     * @see .setFacing
     * @see .getFacing
     */
    const val FACING_BACK = 0

    /**
     * The device points in the same direction as the screen.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT]
     * Camera2: [android.hardware.camera2.CameraCharacteristics.LENS_FACING_FRONT]
     *
     * @see .setFacing
     * @see .getFacing
     */
    const val FACING_FRONT = 1

    /**
     * Flash will never activate.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FLASH_MODE_OFF]
     * Camera2: [android.hardware.camera2.CameraCharacteristics.FLASH_MODE_OFF]
     *
     * @see .setFlash
     * @see .getFlash
     */
    const val FLASH_OFF = 0

    /**
     * Flash will activate during a image capture's shutter.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FLASH_MODE_ON]
     * Camera2: [android.hardware.camera2.CameraCharacteristics.FLASH_MODE_SINGLE]
     *
     * @see .setFlash
     * @see .getFlash
     */
    const val FLASH_ON = 1

    /**
     * Flash will activate during a image capture's shutter, if needed.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FLASH_MODE_AUTO]
     * Camera2: [android.hardware.camera2.CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH]
     *
     * @see .setFlash
     * @see .getFlash
     */
    const val FLASH_AUTO = 2

    /**
     * Flash is constantly activated when the preview is showing.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FLASH_MODE_TORCH]
     * Camera2: [android.hardware.camera2.CameraCharacteristics.FLASH_MODE_TORCH]
     *
     * @see .setFlash
     * @see .getFlash
     */
    const val FLASH_TORCH = 3

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FOCUS_MODE_FIXED]
     * Camera2: [android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE_OFF]
     *
     * @see .setFocus
     * @see .getFocus
     */
    const val FOCUS_OFF = 0

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FOCUS_MODE_AUTO]
     * Camera2: [android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE_AUTO]
     *
     * @see .setFocus
     * @see .getFocus
     */
    const val FOCUS_AUTO = 1

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE]
     * Camera2: [android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE]
     *
     * @see .setFocus
     * @see .getFocus
     */
    const val FOCUS_CONTINUOUS = 2

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_BARCODE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_BARCODE]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_NONE = 0

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_BARCODE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_BARCODE]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_ACTION = 1

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_BARCODE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_BARCODE]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_PORTRAIT = 2

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_LANDSCAPE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_LANDSCAPE]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_LANDSCAPE = 3

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_NIGHT]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_NIGHT]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_NIGHT = 4

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_NIGHT_PORTRAIT]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_NIGHT_PORTRAIT = 5

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_THEATRE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_THEATRE]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_THEATRE = 6

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_BEACH]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_BEACH]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_BEACH = 7

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_SNOW]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_SNOW]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_SNOW = 8

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_SUNSET]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_SUNSET]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_SUNSET = 9

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_STEADYPHOTO]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_STEADYPHOTO]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_STEADYPHOTO = 10

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_FIREWORKS]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_FIREWORKS]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_FIREWORKS = 11

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_SPORTS]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_SPORTS]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_SPORTS = 12

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_PARTY]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_PARTY]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_PARTY = 13

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_CANDLELIGHT]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_CANDLELIGHT]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_CANDLELIGHT = 14

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.SCENE_MODE_BARCODE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_SCENE_MODE_BARCODE]
     *
     * @see .setSensorPreset
     * @see .getSensorPreset
     */
    const val SENSOR_PRESET_BARCODE = 15

    /**
     * No effect will be applied to the preview.
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_NONE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_OFF]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_NONE = 0

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_MONO]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_MONO]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_MONO = 1

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_NEGATIVE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_NEGATIVE = 2

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_SOLARIZE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_SOLARIZE = 3

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_SEPIA]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_SEPIA]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_SEPIA = 4

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_POSTERIZE]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_POSTERIZE = 5

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_WHITEBOARD]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_WHITEBOARD = 6

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_BLACKBOARD]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_BLACKBOARD = 7

    /**
     *
     *
     * Related low-level constants:
     * Camera1: [android.hardware.Camera.Parameters.EFFECT_AQUA]
     * Camera2: [android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_AQUA]
     *
     * @see .setPreviewEffect
     * @see .getPreviewEffect
     */
    const val PREVIEW_EFFECT_AQUA = 8

    /**
     * Describes the orientation of the camera lens relative to the screen.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        FACING_BACK, FACING_FRONT
    )
    internal annotation class Facing

    /**
     * Describes how the camera's flash should behave.
     *
     *
     * Use declared constants with [.setFlash].
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(FLASH_OFF, FLASH_ON, FLASH_AUTO, FLASH_TORCH)
    internal annotation class Flash

    /**
     * Describes the constant in-the-background focus strategy for when autoFocus isn't manually
     * triggered.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(FOCUS_OFF, FOCUS_AUTO, FOCUS_CONTINUOUS)
    internal annotation class Focus

    /**
     * Describes the constant in-the-background focus strategy for when autoFocus isn't manually
     * triggered.
     *
     *
     * Use declared constants with [.setSensorPreset].
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        SENSOR_PRESET_NONE,
        SENSOR_PRESET_ACTION,
        SENSOR_PRESET_PORTRAIT,
        SENSOR_PRESET_LANDSCAPE,
        SENSOR_PRESET_NIGHT,
        SENSOR_PRESET_NIGHT_PORTRAIT,
        SENSOR_PRESET_THEATRE,
        SENSOR_PRESET_BEACH,
        SENSOR_PRESET_SNOW,
        SENSOR_PRESET_SUNSET,
        SENSOR_PRESET_STEADYPHOTO,
        SENSOR_PRESET_FIREWORKS,
        SENSOR_PRESET_SPORTS,
        SENSOR_PRESET_PARTY,
        SENSOR_PRESET_CANDLELIGHT,
        SENSOR_PRESET_BARCODE
    )
    internal annotation class SensorPreset

    /**
     * Describes the constant in-the-background focus strategy for when auto focus isn't manually
     * triggered.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        PREVIEW_EFFECT_NONE,
        PREVIEW_EFFECT_MONO,
        PREVIEW_EFFECT_NEGATIVE,
        PREVIEW_EFFECT_SOLARIZE,
        PREVIEW_EFFECT_SEPIA,
        PREVIEW_EFFECT_POSTERIZE,
        PREVIEW_EFFECT_WHITEBOARD,
        PREVIEW_EFFECT_BLACKBOARD,
        PREVIEW_EFFECT_AQUA
    )
    internal annotation class PreviewEffect
}