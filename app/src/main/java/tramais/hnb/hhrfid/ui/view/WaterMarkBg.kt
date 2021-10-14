package tramais.hnb.hhrfid.ui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.IntRange;
class WaterMarkBg
/**
 * 初始化构造
 * @param context 上下文
 * @param labels 水印文字列表 多行显示支持
 * @param degress 水印角度
 * @param fontSize 水印文字大小
 */(private val context: Context, private val labels: List<String>, //角度
    private val degress: Int, //字体大小 单位sp
    private val fontSize: Int) : Drawable() {
    private val paint = Paint()
    override fun draw(@NonNull canvas: Canvas) {
        val width = bounds.right
        val height = bounds.bottom
        canvas.drawColor(Color.parseColor("#40F3F5F9"))
        paint.color = Color.parseColor("#50AEAEAE")
        paint.isAntiAlias = true
        paint.textSize = sp2px(context, fontSize.toFloat()).toFloat()
        canvas.save()
        canvas.rotate(degress.toFloat())
        val textWidth = paint.measureText(labels[0])
        var index = 0
        var positionY = height / 10
        while (positionY <= height) {
            val fromX = -width + index++ % 2 * textWidth
            var positionX = fromX
            while (positionX < width) {
                var spacing = 0 //间距
                for (label in labels) {
                    canvas.drawText(label, positionX, (positionY + spacing).toFloat(), paint)
                    spacing = spacing + 50
                }
                positionX += textWidth * 2
            }
            positionY += height / 10 + 80
        }
        canvas.restore()
    }

    override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) = Unit
    override fun setColorFilter(@Nullable colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    companion object {
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }
}