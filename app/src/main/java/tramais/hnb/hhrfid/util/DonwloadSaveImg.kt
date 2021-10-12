/*
package tramais.hnb.hhrfid.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.util.DonwloadSaveImg
import java.io.IOException
import java.lang.Exception
import java.net.URL


object DonwloadSaveImg {

    private var context: Context? = null
    private var filePath: String? = null
    private var bannerName: String? = null
    private var mBitmap: Bitmap? = null
    private var mSaveMessage = "失败"


    private val messageHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {}
    }
    private val saveFileRunnable = Runnable {
        try {
            if (!TextUtils.isEmpty(filePath)) { //网络图片
                // 对资源链接
                val url = URL(filePath)
                //打开输入流
                val inputStream = url.openStream()
                //对网上资源进行下载转换位图图片
                mBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
            ImageUtils.saveBitmap(context, mBitmap, FileUtil.getSDPath() + Constants.sdk_qr, "qr" + ".jpg")
            mSaveMessage = "图片保存成功！"
        } catch (e: IOException) {
            mSaveMessage = "图片保存失败！"
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        messageHandler.sendMessage(messageHandler.obtainMessage())
    }

    fun donwloadImg(contexts: Context?, filePaths: String?, bannername: String?) {
        context = contexts
        filePath = filePaths
        bannerName = bannername
        Thread(saveFileRunnable).start()
    }
}*/
