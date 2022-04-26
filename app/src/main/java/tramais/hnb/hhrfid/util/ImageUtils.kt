package tramais.hnb.hhrfid.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.constant.Config
import java.io.*

object ImageUtils {


    @Throws(IOException::class)
    fun getBitmapFormUri(context: Context, uri: Uri?): Bitmap? {
        var input = context.contentResolver.openInputStream(uri!!)

        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true //不加载到内存
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input!!.close()
        val originalWidth = onlyBoundsOptions.outWidth
        val originalHeight = onlyBoundsOptions.outHeight
        if (originalWidth == -1 || originalHeight == -1) return null

        //图片分辨率以480x800为标准
        val hh = 800f //这里设置高度为800f
        val ww = 480f //这里设置宽度为480f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1 //be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) { //如果宽度大的话根据宽度固定大小缩放
            be = (originalWidth / ww).toInt()
        } else if (originalWidth < originalHeight && originalHeight > hh) { //如果高度高的话根据宽度固定大小缩放
            be = (originalHeight / hh).toInt()
        }
        /* if (be <= 0)
            be = 1;*/
        //比例压缩
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = be //设置缩放比例
        bitmapOptions.inDither = true
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565
        input = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input!!.close()
        return compressImage(bitmap) //再进行质量压缩
    }

  /*  fun getStreamT(photoPathurl: String?): String {
        if (TextUtils.isEmpty(photoPathurl)) return ""
        if (!File(photoPathurl).isFile) return ""
        val decodeFile = getimageOnly(BitmapFactory.decodeFile(photoPathurl))
        return bitmapToBase64(decodeFile)!!

    }*/

    fun getStream(photoPathurl: String?): String {
        var uploadBuffer: String = ""

        var fin: FileInputStream? = null
        val baos = ByteArrayOutputStream()
        try {
            fin = FileInputStream(photoPathurl)
            val buffer = ByteArray(1024)
            var count = 0
            while (fin.read(buffer).also { count = it } != -1) {
                baos.write(buffer, 0, count)
            }
            baos.flush()
            baos.close()
            fin.close()
            uploadBuffer = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            baos.reset()
        } catch (e: Exception) {
            LogUtils.e("bitmapToBase64  e" + e.message)
            e.printStackTrace()

        }
        return uploadBuffer
    }

 /*   fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, Config.img_quality_small, baos)
                baos.flush()
                baos.close()
                val bitmapBytes = baos.toByteArray()
                baos.reset()
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            }
        } catch (e: IOException) {
            LogUtils.e("bitmapToBase64  e" + e.message)
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                LogUtils.e("bitmapToBase64  eee" + e.message)
                e.printStackTrace()
            }
        }
        return result
    }*/


    @JvmStatic
    fun saveBitmap(context: Context?, bitmap: Bitmap?, path: String?, photoName: String?, quality: Int? = 80): String {
        // 首先保存图片
        val appDir = path?.let { File(it) }
        if (!appDir!!.exists()) {
            appDir.mkdir()
        }
        //LogUtils.e("quality  $quality")
        val file = photoName?.let { File(appDir, it) }
        if (!file!!.parentFile?.exists()!!) file.parentFile?.mkdirs()
        try {
            val fos = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, quality!!, fos)
            fos.flush()
            fos.close()
            //  bitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        context!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.absolutePath)))
        return file.absolutePath
    }

    fun getimage(srcPath: String?): Bitmap? {
        if (!TextUtils.isEmpty(srcPath)) {
            val newOpts = BitmapFactory.Options()
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeFile(srcPath, newOpts) // 此时返回bm为空
            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            val hh = 800f// 这里设置高度为800f
            val ww = 480f // 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 2 // be=1表示不缩放
            if (w > h && w > ww) { // 如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) { // 如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0) be = 1
            newOpts.inSampleSize = be // 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
            val i = readPictureDegree(srcPath)
            val bitmap1 = rotateBitmap(i, bitmap)
            return compressImage(bitmap1) // 压缩好比例大小后再进行质量压缩
        }
        return null
    }



    fun compressImage(image: Bitmap?): Bitmap? {
        if (image != null) {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, Config.img_quality_common, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 100
            while (baos.toByteArray().size / 1024 > 1024) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset() //重置baos即清空baos
                //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
                image.compress(Bitmap.CompressFormat.JPEG, options, baos) //这里压缩options，把压缩后的数据存放到baos中
                options -= 10 //每次都减少10
                if (options <= 0) break
            }
            val isBm = ByteArrayInputStream(baos.toByteArray()) //把压缩后的数据baos存放到ByteArrayInputStream中
            return BitmapFactory.decodeStream(isBm, null, null) //把ByteArrayInputStream数据生成图片
        }
        return null
    }

    //读取图片旋转角度
    fun readPictureDegree(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path!!)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    degree = 90
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    degree = 180
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    degree = 270
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtils.e("e  ${e.message}")
        }
        return degree
    }

    //旋转图片
    fun rotateBitmap(angle: Int, bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height,
                matrix, true)
    }
}