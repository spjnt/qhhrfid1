package tramais.hnb.hhrfid.util


import android.content.Context
import android.os.Environment
import android.util.Log
import tramais.hnb.hhrfid.ui.view.CustomCameraView

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: Linkin
 * Time：2018/8/27
 * Email：liuzhongjun@novel-supertv.com
 * Blog：https://blog.csdn.net/Android_Technology
 * Desc: TODO
 */
object FileUtils {
    private const val TAG = "FileUtils"

    /**
     * 检测外部存储是否存在
     */
    fun checkSDCard(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    const val MEDIA_TYPE_IMAGE = 1
    const val MEDIA_TYPE_VIDEO = 2

    /**
     * 创建一个文件来保存图片或者视频
     */
    fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
                getSDPath(), CustomCameraView.PHOTO_FILE_NAME
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory")
                return null
            }
        }


        return File(
                mediaStorageDir,
                "temp.jpg"
        )
    }



    fun getSDPath(): String? {
        var sdDir: File? = null
        val sdCardExist =
                Environment.getExternalStorageState() == "mounted"
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory()
        }
        return sdDir.toString()
    }
}