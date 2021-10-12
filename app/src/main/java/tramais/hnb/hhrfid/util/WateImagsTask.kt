package tramais.hnb.hhrfid.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.watermark.androidwm_light.WatermarkBuilder
import com.watermark.androidwm_light.bean.WatermarkImage
import com.watermark.androidwm_light.bean.WatermarkText
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.constant.Constants

class WateImagsTask {
    var watermarkTexts: MutableList<WatermarkText> = ArrayList()

    fun addWater(context: Context, textList: MutableList<String>, backGroundBitmap: Bitmap): Bitmap? {
        if (textList == null || textList.isEmpty()) return null
        val watermarkImage: WatermarkImage = WatermarkImage(context, R.drawable.water_logo)
                .setImageAlpha(255)
                .setPositionX(0.01)
                .setPositionY(0.65)
                .setSize(0.3)
        val userName = PreferUtils.getString(context, Constants.UserName)
        var name = if (userName.isNullOrBlank()) {
            "未知"
        } else {
            userName
        }
        var startY = 1 - 0.03 * textList.size - 0.01
        for (index in 1..10) {
            var postionX = if (index % 2 == 0)
                0.61
            else
                0.11
            var text = WatermarkText("操作员：$name")
                    .setPositionX(postionX)
                    .setPositionY(0.08 * index)
                    .setTextAlpha(200) // 透明度
                    .setTextColor(Color.GRAY) // 文字水印文字颜色
                    .setTextSize(DisplayUtil.sp2px(context, 8.0f))//DisplayUtil.sp2px(context,8.0f)
                    .setRotation(20.0)
            //  .setTextFont(R.font.myriadroman_1)
            watermarkTexts.add(text)
        }
        for (item in 0 until textList.size) {
            var s = textList[item]
            var index = item + 1
            var text = WatermarkText(s)
                    .setPositionX(0.01)
                    .setPositionY(startY + 0.03 * item)
                    .setTextAlpha(255) // 透明度
                    .setTextColor(Color.WHITE) // 文字水印文字颜色
                    .setTextSize(DisplayUtil.sp2px(context, 12.0f))//DisplayUtil.sp2px(context,10.0f)
            //  .setTextFont(R.font.myriadroman_1)
            if (!watermarkTexts.contains(text))
                watermarkTexts.add(text)
        }
        return WatermarkBuilder.create(context, backGroundBitmap) // 加载背景底图
                .loadWatermarkTexts(watermarkTexts) // 加载水印对象
                .loadWatermarkImage(watermarkImage)
                .watermark.outputImage
    }

}