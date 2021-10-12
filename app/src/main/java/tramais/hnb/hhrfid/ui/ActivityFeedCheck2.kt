/*
package tramais.hnb.hhrfid.ui

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
import tramais.hnb.hhrfid.ui.fragment.FragmentBasic2
import tramais.hnb.hhrfid.ui.fragment.FragmentDingSun
import tramais.hnb.hhrfid.ui.fragment.FragmentSign2
import tramais.hnb.hhrfid.ui.fragment.FragmentSunshi
import tramais.hnb.hhrfid.ui.view.CustomScrollViewPager


class ActivityFeedCheck2 : BaseActivity() {
    private lateinit var mMagicIndicator: MagicIndicator
    var fragment_banan: Fragment? = null
    var fragment_check: Fragment? = null
    var fragment_basic: Fragment? = null
    var fragment_sign: Fragment? = null
    var fragmentList: MutableList<Fragment>? = ArrayList()
    var titleList: MutableList<String> = ArrayList()
    private var context: Context = this
    private lateinit var mViewPage: CustomScrollViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor_mangment)
    }


    override fun initView() {
        setTitleText("养殖险现场定损")
        mMagicIndicator = findViewById<MagicIndicator>(R.id.magic_indicator)

        mViewPage = findViewById<CustomScrollViewPager>(R.id.view_page)
        mViewPage.setScrollable(false)
        initViewPage()
    }


    fun initViewPage() {
        if (fragment_banan == null)
            fragment_banan = FragmentBasic2()
        if (!fragmentList!!.contains(fragment_banan!!))
            fragmentList!!.add(fragment_banan!!)



        if (fragment_basic == null)
            fragment_basic = FragmentSunshi()
        if (!fragmentList!!.contains(fragment_basic!!))
            fragmentList!!.add(fragment_basic!!)

        if (fragment_check == null)
            fragment_check = FragmentDingSun()
        if (!fragmentList!!.contains(fragment_check!!))
            fragmentList!!.add(fragment_check!!)

        if (fragment_sign == null)
            fragment_sign = FragmentSign2()
        if (!fragmentList!!.contains(fragment_sign!!))
            fragmentList!!.add(fragment_sign!!)
        var viewPageAdaper = ViewPagerAdapter(supportFragmentManager, fragmentList!!)
        mViewPage.adapter = viewPageAdaper
    }

    override fun initData() {
        intent?.let {
            fenPei = it.getSerializableExtra("FenPei") as FenPei
        }
        titleList.add("报案信息")
        titleList.add("损失情况")
        titleList.add("定损情况")
        titleList.add("定损签名")
        val commonNavigator = CommonNavigator(context)
        val adapter = NavigatorAdapter(titleList, this, mViewPage)
        commonNavigator.adapter = adapter
        mMagicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mMagicIndicator, mViewPage)


    }


    override fun initListner() {

    }

    companion object {
        lateinit var fenPei: FenPei
    }
}*/
