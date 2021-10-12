package tramais.hnb.hhrfid.ui.view



interface CameraView {
    /**
     * 与生命周期onResume调用
     */
    fun onResume()

    /**
     * 与生命周期onPause调用
     */
    fun onPause()

    /**
     * 拍照
     */
    fun takePicture()

    /**
     * 拍照(有回调)
     */
    fun takePicture(takePictureCallback: TakePictureCallback?)

    /**
     * 设置保存的图片文件
     *
     * @param pictureSavePath 拍摄的图片返回的绝对路径
     */
    fun setPictureSavePath(pictureSavePath: String?)

    /**
     * 切换相机摄像头
     */
    fun switchCameraFacing()
    interface TakePictureCallback {
        fun success(picturePath: String?)
        fun error(error: String?)
    }
}