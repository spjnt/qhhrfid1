package tramais.hnb.hhrfid.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.constant.Constants
import kotlin.Throws
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.PreferUtils
import java.io.*
import java.lang.Exception

object ImageUtils {
    /*  */
    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    /*
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e("TAG", "原图被旋转角度： ========== " + orientation );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }*/
    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap? {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }
        if (returnBm == null) {
            returnBm = bitmap
        }
        if (bitmap != returnBm) {
            bitmap.recycle()
        }
        return returnBm
    }

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

    fun getStream(photoPathurl: String?): String {
        if (TextUtils.isEmpty(photoPathurl) || photoPathurl === "null") return ""
        var uploadBuffer: String = ""
        var fin: FileInputStream? = null
        try {
            fin = FileInputStream(photoPathurl)
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count = 0
            while (fin.read(buffer).also { count = it } >= 0) {
                baos.write(buffer, 0, count)
            }
            uploadBuffer = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uploadBuffer
    }

    fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes = baos.toByteArray()
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
    }

    /**
     * 绘制文字到右下角
     *
     * @param context
     * @param
     */
    fun drawTextToRightBottom(context: Context, url: String?, epc: String, lat_lon: String, address: String): Bitmap? {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.textSize = dp2px(context, 6.0f).toFloat()
        val bounds = Rect()

//        paint.getTextBounds(ids, 0, ids.length(), bounds);
        val bitmap = getimage(url)
        return drawTextToBitmap(context, bitmap, epc, lat_lon, address, paint, bounds,
                dp2px(context, 3f), bitmap!!.height - bounds.height() - dp2px(context, 3f))
    }

    //图片上绘制文字
    private fun drawTextToBitmap(context: Context, bitmap: Bitmap?, epc: String, lat_lon: String, address: String,
                                 paint: Paint, bounds: Rect, paddingLeft: Int, paddingTop: Int): Bitmap? {
        var bitmap = bitmap
        var bitmapConfig = bitmap!!.config
        paint.isDither = true // 获取跟清晰的图像采样
        paint.isFilterBitmap = true // 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.RGB_565
        }
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)
        val nowTime = TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss)
        canvas.drawText("PICC", paddingLeft.toFloat(), (paddingTop - 200).toFloat(), paint)
        canvas.drawText("验标员:" + PreferUtils.getString(context, Constants.UserName), paddingLeft.toFloat(), (paddingTop - 160).toFloat(), paint)
        canvas.drawText(epc, paddingLeft.toFloat(), (paddingTop - 120).toFloat(), paint)
        canvas.drawText("时间:$nowTime", paddingLeft.toFloat(), (paddingTop - 80).toFloat(), paint)
        canvas.drawText(lat_lon, paddingLeft.toFloat(), (paddingTop - 40).toFloat(), paint)
        if (!lat_lon.contains("查勘地点")) canvas.drawText("地址:$address", paddingLeft.toFloat(), paddingTop.toFloat(), paint)
        return bitmap
    }

    /**
     * dip转pix
     *
     * @param context
     * @param dp
     * @return
     */
    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun saveBitmap(context: Context?, bitmap: Bitmap?, path: String?, photoName: String?): String {
        // 首先保存图片
        val appDir = File(path)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val file = File(appDir, photoName)
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        try {
            val fos = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), photoName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
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
            val hh = 800f // 这里设置高度为800f
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

    fun getimageOnly(image: Bitmap?): Bitmap? {
        if (image != null) {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 65, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
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

    fun compressImage(image: Bitmap?): Bitmap? {
        if (image != null) {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 90, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 100
            while (baos.toByteArray().size / 1024 > 500) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
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
            LogUtils.e("原图被旋转角度： ========== $orientation")
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270
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