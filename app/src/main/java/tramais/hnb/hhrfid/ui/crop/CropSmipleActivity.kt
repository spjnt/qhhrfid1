package tramais.hnb.hhrfid.ui.crop

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.CropChaKanBean
import tramais.hnb.hhrfid.lv.NavigatorAdapter
import tramais.hnb.hhrfid.lv.ViewPagerAdapter
import tramais.hnb.hhrfid.ui.crop.frag.CropSmipleBaoAnInfo
import tramais.hnb.hhrfid.ui.crop.frag.CropSmipleConcluInfo
import tramais.hnb.hhrfid.ui.view.CustomScrollViewPager
import java.util.*

class CropSmipleActivity : BaseActivity() {
    private var magicIndicator: MagicIndicator? = null
    private var viewPage: CustomScrollViewPager? = null
    private var smiple_banan: Fragment? = null
//    private var smiple_pass: Fragment? = null
    private var smiple_conclu: Fragment? = null
    private val fragments: MutableList<Fragment> = ArrayList()
    private val titles: MutableList<String> = ArrayList()
    private var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_check_pass)
        initView()
    }

    override fun initView() {
        setTitleText("现场抽样")
        magicIndicator = findViewById<MagicIndicator>(R.id.magic_indicator)
        viewPage = findViewById<CustomScrollViewPager>(R.id.view_page)
//        viewPage!!.setScrollable(false)
    }

    override fun initData() {
        intent?.let {
            cropChaKanBean = it.getSerializableExtra("crop") as CropChaKanBean


        }
        if (smiple_banan == null) smiple_banan = CropSmipleBaoAnInfo()
        if (!fragments.contains(smiple_banan!!)) fragments.add(smiple_banan!!)

       /* if (smiple_pass == null) smiple_pass = CropSmiplePassInfo()
        if (!fragments.contains(smiple_pass!!)) fragments.add(smiple_pass!!)*/

        if (smiple_conclu == null) smiple_conclu = CropSmipleConcluInfo()
        if (!fragments.contains(smiple_conclu!!)) fragments.add(smiple_conclu!!)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewPage!!.adapter = viewPagerAdapter

        titles.add("报案信息")
//        titles.add("抽样过程")
        titles.add("抽样结论")

        val commonNavigator = CommonNavigator(context)
        val adapter = NavigatorAdapter(titles, this, viewPage!!)
        commonNavigator.adapter = adapter
        magicIndicator!!.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, viewPage)
    }

    override fun initListner() {}

    companion object {
        lateinit var cropChaKanBean: CropChaKanBean
    }

}