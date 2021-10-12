package tramais.hnb.hhrfid.ui.crop

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.lv.NavigatorAdapter
import tramais.hnb.hhrfid.lv.ViewPagerAdapter
import tramais.hnb.hhrfid.ui.crop.frag.*
import tramais.hnb.hhrfid.ui.view.CustomScrollViewPager
import java.util.*

class CropCheckPassActivity : BaseActivity() {
    private var magicIndicator: MagicIndicator? = null
    private var viewPage: CustomScrollViewPager? = null

    //    private var crop_banan: Fragment? = null
//    private var crop_chengbao: Fragment? = null
    private var crop_chakan: Fragment? = null

    private var crop_FarmList: Fragment? = null
    private val fragments: MutableList<Fragment> = ArrayList()
    private val titles: MutableList<String> = ArrayList()
    private var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_check_pass)
        initView()
    }

    override fun initView() {
        setTitleText("现场查勘")
        magicIndicator = findViewById<MagicIndicator>(R.id.magic_indicator)
        viewPage = findViewById<CustomScrollViewPager>(R.id.view_page)
//        viewPage!!.setScrollable(false)
    }

    override fun initData() {
        intent?.let {
            cropChaKanBean = it.getSerializableExtra("FenPei") as FenPei


        }
//        if (crop_banan == null) crop_banan = CropBaoAnInfo()
//        if (!fragments.contains(crop_banan!!)) fragments.add(crop_banan!!)
//
//        if (crop_chengbao == null) crop_chengbao = CropChengBaoInfo()
//        if (!fragments.contains(crop_chengbao!!)) fragments.add(crop_chengbao!!)
//crop_standard


        if (crop_chakan == null) crop_chakan = CropChaKanInfo()
        if (!fragments.contains(crop_chakan!!)) fragments.add(crop_chakan!!)

        if (crop_FarmList == null) crop_FarmList = CropFarmListFrag()
        if (!fragments.contains(crop_FarmList!!)) fragments.add(crop_FarmList!!)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewPage!!.adapter = viewPagerAdapter

//        titles.add("报案信息")
//        titles.add("承保信息")
        titles.add("查勘信息")
        titles.add("农户信息")

        val commonNavigator = CommonNavigator(context)
        val adapter = NavigatorAdapter(titles, this, viewPage!!)
        commonNavigator.adapter = adapter
        magicIndicator!!.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, viewPage)
    }

    companion object {
        lateinit var cropChaKanBean: FenPei
    }

    override fun initListner() {}
}