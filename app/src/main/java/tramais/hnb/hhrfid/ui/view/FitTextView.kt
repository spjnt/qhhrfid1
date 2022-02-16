package tramais.hnb.hhrfid.ui.view

import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import tramais.hnb.hhrfid.ui.view.FitTextView

class FitTextView(context: Context?, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatTextView(context!!, attrs) {
    private var mTextPaint: Paint? = null
    private var mMaxTextSize // 获取当前所设置文字大小作为最大文字大小
            = 0f
    private val mMinTextSize = 3f

    constructor(context: Context?) : this(context, null) {}

    protected override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        refitText(text.toString(), this.width)
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    private fun initialise() {
        mTextPaint = TextPaint()
        (mTextPaint as TextPaint).set(this.paint)
        // 最大的大小默认为特定的文本大小，除非它太小了
        mMaxTextSize = this.textSize
        //          mMinTextSize = 8;
    }

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw) {
            refitText(this.getText().toString(), w)
        }
    }

    /**
     * Resize the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     *
     */
    private fun refitText(text: String, textWidth: Int) {
        if (textWidth > 0) {
            val availableWidth: Int = textWidth - this.getPaddingLeft() - this.getPaddingRight()
            var trySize = mMaxTextSize
            mTextPaint!!.textSize = trySize
            while (mTextPaint!!.measureText(text) > availableWidth) {
                trySize -= 1f
                if (trySize <= mMinTextSize) {
                    trySize = mMinTextSize
                    break
                }
                mTextPaint?.textSize = trySize
            }

            // setTextSize参数值为sp值
            textSize = px2sp(context, trySize)
        }
    }

    companion object {
        /**
         * 将px值转换为sp值，保证文字大小不变
         */
        fun px2sp(context: Context, pxValue: Float): Float {
            val fontScale: Float = context.resources.displayMetrics.scaledDensity
            return pxValue / fontScale
        }
    }

    init {
        gravity = gravity or Gravity.CENTER_VERTICAL // 默认水平居中
        setLines(1)
        initialise()
    }
}