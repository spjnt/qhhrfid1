package tramais.hnb.hhrfid.waterimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

object WaterMaskUtil {
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
     * 绘制水印图片
     * @param src 原图
     * @param watermark 水印
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    private fun createWaterMaskBitmap(src: Bitmap?, watermark: Bitmap?, paddingLeft: Int, paddingTop: Int
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
        src.recycle()
        watermark.recycle()
        return newb
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

}