package tramais.hnb.hhrfid.lv

import android.content.Context
import android.graphics.Color
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import tramais.hnb.hhrfid.R

// 导航适配器
class NavigatorAdapter(//title
        var str: MutableList<String>, var context: Context, var viewpager: ViewPager) : CommonNavigatorAdapter() {

    //返回title长度
    override fun getCount(): Int {
        return str.size
    }

    //设置title的属性样式
    override fun getTitleView(context: Context, index: Int): IPagerTitleView {
        val titleView = SimplePagerTitleView(context) //新建简单titleView
        titleView.text = str[index] //设置title内容
        titleView.textSize = 16f //设置title字体大小
        titleView.selectedColor = ContextCompat.getColor(context, R.color.new_theme) //设置选中时的title颜色
        titleView.normalColor = Color.BLACK //设置未选中时的title颜色
        titleView.setOnClickListener {
            viewpager.currentItem = index //切换到相应的viewpager页面
        }
        return titleView
    }

    //设置指示条的属性样式
    override fun getIndicator(context: Context): IPagerIndicator {
        val indicator = LinePagerIndicator(context) //新建指示条
        indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT //设置指示条与内容同宽
        indicator.roundRadius = 5f //设置指示条圆角度
        indicator.setColors(Color.BLUE) //设置指示条颜色（此方法可设置多个颜色）
        indicator.startInterpolator = AccelerateDecelerateInterpolator() //设置指示条插值器
        return indicator
    }

}