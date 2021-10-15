package tramais.hnb.hhrfid.waterimage

import android.R
import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.apkfuns.logutils.LogUtils

/**
 * <pre>
 * author : echoMu
 * e-mail :
 * time   : 2017/07/12
 * desc   :
 * version:
</pre> *
 */
object WaterMaskUtil {
    /**
     * 设置水印图片在左上角
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    fun createWaterMaskLeftTop(
        context: Context, src: Bitmap?, watermark: Bitmap?,
        paddingLeft: Int, paddingTop: Int
    ): Bitmap? {
        return createWaterMaskBitmap(
            src, watermark,
            dp2px(context, paddingLeft.toFloat()), dp2px(context, paddingTop.toFloat())
        )
    }

    /**
     * 设置水印图片在右下角
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    fun createWaterMaskRightBottom(
        context: Context, src: Bitmap?, watermark: Bitmap?,
        paddingRight: Int, paddingBottom: Int
    ): Bitmap? {
        return createWaterMaskBitmap(
            src, watermark,
            src!!.width - watermark!!.width - dp2px(context, paddingRight.toFloat()),
            src.height - watermark.height - dp2px(context, paddingBottom.toFloat())
        )
    }

    /**
     * 设置水印图片到右上角
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    fun createWaterMaskRightTop(
        context: Context, src: Bitmap?, watermark: Bitmap?,
        paddingRight: Int, paddingTop: Int
    ): Bitmap? {
        return createWaterMaskBitmap(
            src, watermark,
            src!!.width - watermark!!.width - dp2px(context, paddingRight.toFloat()),
            dp2px(context, paddingTop.toFloat())
        )
    }

    /**
     * 设置水印图片到左下角
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    fun createWaterMaskLeftBottom(
        context: Context, src: Bitmap?, watermark: Bitmap?,
        paddingLeft: Int, paddingBottom: Int
    ): Bitmap? {
        return createWaterMaskBitmap(
            src, watermark, dp2px(context, paddingLeft.toFloat()),
            src!!.height - watermark!!.height - dp2px(context, paddingBottom.toFloat())
        )
    }

    /**
     * 设置水印图片到中间
     * @param src
     * @param watermark
     * @return
     */
    fun createWaterMaskCenter(src: Bitmap, watermark: Bitmap): Bitmap? {
        return createWaterMaskBitmap(
            src, watermark,
            (src.width - watermark.width) / 2,
            (src.height - watermark.height) / 2
        )
    }

    /**
     * 绘制水印图片
     * @param src 原图
     * @param watermark 水印
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    private fun createWaterMaskBitmap(
        src: Bitmap?, watermark: Bitmap?,
        paddingLeft: Int, paddingTop: Int
    ): Bitmap? {
        if (src == null) {
            return null
        }
        val width = src.width
        val height = src.height
        //创建一个bitmap
        val newb =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        val canvas = Canvas(newb)
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0f, 0f, null)
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark!!, paddingLeft.toFloat(), paddingTop.toFloat(), null)
        // 保存
        canvas.save()
        // 存储
        canvas.restore()
        return newb
    }

    /**
     * 给图片添加文字到左上角
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    fun drawTextToLeftTop(
        context: Context, bitmap: Bitmap, text: String,
        size: Int, color: Int, paddingLeft: Int, paddingTop: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.textSize = dp2px(context, size.toFloat()).toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return drawTextToBitmap(
            context, bitmap, text, paint, bounds,
            dp2px(context, paddingLeft.toFloat()),
            dp2px(context, paddingTop.toFloat()) + bounds.height()
        )
    }

    /**
     * 绘制文字到右下角
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    fun drawTextToRightBottom(
        context: Context, bitmap: Bitmap, text: String,
        size: Int, color: Int, paddingRight: Int, paddingBottom: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.textSize = dp2px(context, size.toFloat()).toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return drawTextToBitmap(
            context, bitmap, text, paint, bounds,
            bitmap.width - bounds.width() - dp2px(context, paddingRight.toFloat()),
            bitmap.height - dp2px(context, paddingBottom.toFloat())
        )
    }

    /**
     * 绘制文字到右上方
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    fun drawTextToRightTop(
        context: Context, bitmap: Bitmap, text: String,
        size: Int, color: Int, paddingRight: Int, paddingTop: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.textSize = dp2px(context, size.toFloat()).toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return drawTextToBitmap(
            context, bitmap, text, paint, bounds,
            bitmap.width - bounds.width() - dp2px(context, paddingRight.toFloat()),
            dp2px(context, paddingTop.toFloat()) + bounds.height()
        )
    }

    /**
     * 绘制文字到左下方
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    fun drawTextToLeftBottom(
        context: Context, bitmap: Bitmap, text: String,
        size: Int, color: Int, paddingLeft: Int, paddingBottom: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.textSize = dp2px(context, size.toFloat()).toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return drawTextToBitmap(
            context, bitmap, text, paint, bounds,
            dp2px(context, paddingLeft.toFloat()),
            bitmap.height - dp2px(context, (paddingBottom + 2).toFloat())
        )
    }

    /**
     * 绘制文字到中间
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @return
     */
    fun drawTextToCenter(
        context: Context, bitmap: Bitmap, text: String,
        size: Int, color: Int
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.textSize = dp2px(context, size.toFloat()).toFloat()
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return drawTextToBitmap(
            context, bitmap, text, paint, bounds,
            (bitmap.width - bounds.width()) / 2,
            (bitmap.height + bounds.height()) / 2
        )
    }

    //图片上绘制文字
    private fun drawTextToBitmap(
        context: Context, bitmap: Bitmap, text: String,
        paint: Paint, bounds: Rect, paddingLeft: Int, paddingTop: Int
    ): Bitmap {
        var bitmap = bitmap
        var bitmapConfig = bitmap.config
        paint.isDither = true // 获取跟清晰的图像采样
        paint.isFilterBitmap = true // 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)

//        canvas.drawText(text, paddingLeft, paddingTop, paint);
        drawMultiLineText(text, paddingLeft.toFloat(), paddingTop.toFloat(), paint, canvas)
        return bitmap
    }

    private fun drawMultiLineText(
        str: String, x: Float, y: Float, paint: Paint,
        canvas: Canvas
    ) {
        val lines = str.split("\n").toTypedArray()
        var txtSize = -paint.ascent() + paint.descent()
        if (paint.style == Paint.Style.FILL_AND_STROKE
            || paint.style == Paint.Style.STROKE
        ) {
            txtSize += paint.strokeWidth // add stroke width to the text
        }
        val lineSpace = txtSize * 0.1f // default line spacing
        for (i in lines.indices) {
            canvas.drawText(lines[i], x, y + (txtSize + lineSpace) * i, paint)
        }
    }

    /**
     * 缩放图片
     * @param src
     * @param w
     * @param h
     * @return
     */
    fun scaleWithWH(src: Bitmap?, w: Double, h: Double): Bitmap? {
        return if (w == 0.0 || h == 0.0 || src == null) {
            src
        } else {
            // 记录src的宽高
            val width = src.width
            val height = src.height
            // 创建一个matrix容器
            val matrix = Matrix()
            // 计算缩放比例
            val scaleWidth = (w / width).toFloat()
            val scaleHeight = (h / height).toFloat()
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight)
            // 创建缩放后的图片
            Bitmap.createBitmap(src, 0, 0, width, height, matrix, true)
        }
    }

    /**
     * dip转pix
     * @param context
     * @param dp
     * @return
     */
    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    /**
     * 将一个view转换为Bitmap
     * @param view
     * @return
     */
    fun convertViewToBitmap(activity: AppCompatActivity,view: View?,width: Int,height: Int): Bitmap {
        var me = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(me)
        val widthPixels = me.widthPixels
        val heightPixels = me.heightPixels
        view!!.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.AT_MOST
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        LogUtils.e("view.measuredWidth  ${view.measuredWidth}   ${view.measuredHeight}")
        view.buildDrawingCache()
        return view.drawingCache



    }
    fun loadBitmapFromView(v: View?): Bitmap? {
        if (v == null) {
            return null
        }
        val intw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val inth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(intw, inth)
        val bitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        v.draw(canvas)
        return bitmap
    }

    /**
     * 缩放Bitmap图片
     *
     */
    fun zoomBitmap(bitmap: Bitmap?, width: Int, height: Int): Bitmap {
        val w = bitmap!!.width
        val h = bitmap.height
        val matrix = Matrix()
        val scaleWidth = width.toFloat() / w
        val scaleHeight = height.toFloat() / h
        matrix.postScale(scaleWidth, scaleHeight) // 利用矩阵进行缩放不会造成内存溢出
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
    }
}