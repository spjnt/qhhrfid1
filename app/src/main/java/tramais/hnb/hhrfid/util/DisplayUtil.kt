package tramais.hnb.hhrfid.util

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import com.apkfuns.logutils.LogUtils

object DisplayUtil {
    var screenWidthPx //屏幕宽 px
            = 0
    var screenhightPx //屏幕高 px
            = 0
    var density //屏幕密度
            = 0f
    var densityDPI //屏幕密度
            = 0
    var screenWidthDip //  dp单位
            = 0f
    var screenHightDip //  dp单位
            = 0f


    /** 获取屏幕的高度  */
    fun getWindowsHeight(activity: Activity): Int {
        val dm = DisplayMetrics()
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm)
        return dm.heightPixels
    }

    /** 获取屏幕的宽度  */
    fun getWindowsWidth(activity: Activity): Int {
        val dm = DisplayMetrics()
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm)
        return dm.widthPixels
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Float {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    // 将px值转换为sp值
    fun px2sp(context: Context, pxValue: Float): Double {
        val fontScale: Float = context.resources.displayMetrics.scaledDensity

        val toDouble = (pxValue / fontScale + 0.5f).toDouble()
        LogUtils.e("fontScale  px2sp $fontScale    $toDouble")
        return toDouble
    }

    fun dip2px2(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dp2px(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics)
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Double {
        val fontScale: Float = context.resources.displayMetrics.scaledDensity
      //  LogUtils.e("fontScale   sp2px$fontScale")
        return (spValue * fontScale + 0.5f).toDouble()
    }
}