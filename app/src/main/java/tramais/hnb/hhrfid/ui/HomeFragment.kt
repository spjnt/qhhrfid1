package tramais.hnb.hhrfid.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.BarUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.liangmutian.randomtextviewlibrary.RandomTextView
import com.gyf.immersionbar.ktx.immersionBar
import com.zhouwei.mzbanner.MZBannerView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.BillCount
import tramais.hnb.hhrfid.bean.NewsList
import tramais.hnb.hhrfid.bean.Roles
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetBillCount
import tramais.hnb.hhrfid.lv.BannerViewHolder
import tramais.hnb.hhrfid.lv.DelegateMultiAdapter
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.CropBaoAnChoiceActivity
import tramais.hnb.hhrfid.ui.view.RecyleViewForScrollView
import tramais.hnb.hhrfid.util.GsonUtil
import tramais.hnb.hhrfid.util.StatusBarUtil
import tramais.hnb.hhrfid.util.Utils
import java.util.*


class HomeFragment : BaseFragment() {
    var list_pic: MutableList<String> = ArrayList()

    private var rv: RecyleViewForScrollView? = null
    private var newsLists: List<NewsList>? = ArrayList()
    private var tv_yangzhi_first: RandomTextView? = null
    private var tv_yangzhi_second: RandomTextView? = null
    private var tv_yangzhi_third: RandomTextView? = null
    private var tv_yangzhi_fourth: RandomTextView? = null
    private var tv_zhongzhi_first: RandomTextView? = null
    private var tv_zhongzhi_second: RandomTextView? = null
    private var tv_zhongzhi_third: RandomTextView? = null
    private var tv_zhongzhi_fourth: RandomTextView? = null
    private var tv_zhongzhi_data: TextView? = null
    private var llYzNew: LinearLayout? = null
    private var ivYzNew: ImageView? = null
    private var llYzTask: LinearLayout? = null
    private var ivYzTask: ImageView? = null
    private var llZzNew: LinearLayout? = null
    private var ivZzNew: ImageView? = null
    private var llZzTask: LinearLayout? = null
    private var llNumber: LinearLayout? = null
    private var llLiPei: LinearLayout? = null
    private var ivZzTask: ImageView? = null
    var banner: MZBannerView<String?>? = null
    var roles: MutableList<Roles>? = ArrayList()
    private var view_: View? = null
    private var adapter: DelegateMultiAdapter? = null
    override fun findViewById(view: View?) {

        roles = MainActivity.roles
        view?.let {
            banner = it.findViewById(R.id.banner)
            rv = it.findViewById(R.id.home_rv)
            llYzNew = it.findViewById(R.id.ll_yz_new)
            ivYzNew = it.findViewById(R.id.iv_yz_new)
            llYzTask = it.findViewById(R.id.ll_yz_task)
            ivYzTask = it.findViewById(R.id.iv_yz_task)
            llZzNew = it.findViewById(R.id.ll_zz_new)
            ivZzNew = it.findViewById(R.id.iv_zz_new)
            llZzTask = it.findViewById(R.id.ll_zz_task)
            ivZzTask = it.findViewById(R.id.iv_zz_task)
            tv_yangzhi_first = it.findViewById(R.id.yangzhi_first)
            tv_yangzhi_second = it.findViewById(R.id.yangzhi_second)
            tv_yangzhi_third = it.findViewById(R.id.yangzhi_third)
            tv_yangzhi_fourth = it.findViewById(R.id.yangzhi_fourth)
            tv_zhongzhi_first = it.findViewById(R.id.zhongzhi_first)
            tv_zhongzhi_second = it.findViewById(R.id.zhongzhi_second)
            tv_zhongzhi_third = it.findViewById(R.id.zhongzhi_third)
            tv_zhongzhi_fourth = it.findViewById(R.id.zhongzhi_fourth)
            view_ = it.findViewById(R.id.view)

            llNumber = it.findViewById(R.id.ll_ai_number)
            llLiPei = it.findViewById(R.id.ll_body_weight)
            val params: LinearLayout.LayoutParams = view_!!.layoutParams as LinearLayout.LayoutParams

            params.weight = LinearLayout.LayoutParams.MATCH_PARENT.toFloat()

            params.height = BarUtils.getStatusBarHeight()

            view_!!.layoutParams = params

        }
        adapter = DelegateMultiAdapter(requireContext())
//        mAdapter = QuickAdapter()

        rv!!.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ColorDrawable(Color.GRAY))
        rv!!.addItemDecoration(divider)
        rv!!.adapter = adapter
        //  mAdapter!!.setList(earTags)
        getInfo()
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_home
    }

    fun showAnimal(view: RandomTextView?, num: String) {
        view?.let {
            it.text = num
            it.setPianyilian(RandomTextView.FIRSTF_LAST)
            it.start()
        }

    }

    override fun initListener() {
        llNumber!!.setOnClickListener {
            //  Utils.goToNextUI(ActivityNumber::class.java)
            var intent = Intent(context, ActivityFarmList::class.java)
            intent.putExtra(Constants.MODULE_NAME, "智能点数")
            startActivity(intent)
        }
        llLiPei!!.setOnClickListener {
            Utils.goToNextUI(ActivityGoToCamer::class.java)
        }
    }

    override fun initData() {

        //根据状态栏颜色来决定状态栏文字用黑色还是白色
      //  activity?.let { StatusBarUtil.setStatusBarMode(it, true, R.color.red) }

        if (haveRoles("养殖险", "承保", "新建投保单")) {
            ivYzNew!!.setBackgroundResource(R.mipmap.a_new)
        } else {
            ivYzNew!!.setBackgroundResource(R.mipmap.a_new_gray)
        }

        if (haveRoles("养殖险", "理赔", "任务调度")) {
            ivYzTask!!.setBackgroundResource(R.mipmap.a_diaodu)
        } else {
            ivYzTask!!.setBackgroundResource(R.mipmap.a_diaodu_gray)
        }

        if (haveRoles("种植险", "承保", "新增投保单")) {
            ivZzNew!!.setBackgroundResource(R.mipmap.a_new)
        } else {
            ivZzNew!!.setBackgroundResource(R.mipmap.a_new_gray)
        }

        if (haveRoles("种植险", "理赔", "任务调度")) {
            ivZzTask!!.setBackgroundResource(R.mipmap.a_diaodu)
        } else {
            ivZzTask!!.setBackgroundResource(R.mipmap.a_diaodu_gray)
        }
        adapter?.let {
            it.setOnItemClickListener { adapter, view, position ->

                val homeNewBean = it.data[position]
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra("web", Config.GetNewsListDetail + homeNewBean?.id)
                intent.putExtra("title", "新闻详情")
                startActivity(intent)
            }
        }
        /* rv!!.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
             val homeNewBean = newsLists!![position]
             val intent = Intent(context, WebActivity::class.java)
             intent.putExtra("web", Config.GetNewsListDetail + homeNewBean.id)
             intent.putExtra("title", "新闻详情")
             startActivity(intent)
         }*/
        news
       // getInfo()


        llYzNew!!.setOnClickListener { v ->
            if (haveRoles("养殖险", "承保", "新建投保单"))
                Utils.goToNextUI(ActivityTouBaoList::class.java)
            else
                tips()

        }

        llYzTask!!.setOnClickListener { v ->
            if (haveRoles("养殖险", "理赔", "任务调度"))
                goToFenPei("养殖险")
            //Utils.goToNextUI(ActivityFenPei::class.java)
            else
                tips()
        }
        llZzNew!!.setOnClickListener { v ->
            if (haveRoles("种植险", "承保", "新增投保单"))
                Utils.goToNextUI(CropBaoAnChoiceActivity::class.java)
            else
                tips()
        }

        llZzTask!!.setOnClickListener { v ->
            if (haveRoles("种植险", "理赔", "任务调度"))
                goToFenPei("种植险")
            //Utils.goToNextUI(CropActivityFenPei::class.java)
            else
                tips()
        }

    }
    fun goToFenPei(module_name: String) {
        var intent = Intent(context, ActivityFenPei::class.java)
        intent.putExtra(Constants.MODULE_NAME, module_name)
        requireContext().startActivity(intent)
    }

    fun tips() {
        "暂无该模块权限".showStr()
    }

    private val news: Unit
        private get() {
            RequestUtil.getInstance(context)!!.getNewsList { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                if (datas != null && datas.size > 0) newsLists = GsonUtil.instant!!.parseCommonUseArr(datas, NewsList::class.java)
                if (newsLists != null && newsLists!!.isNotEmpty()) getBannerData(newsLists)
//                mAdapter!!.setList(newsLists)
                adapter!!.setList(newsLists)
            }
        }


    fun getInfo() {

        RequestUtil.getInstance(context)!!.getBillCount(object : GetBillCount {
            override fun getBillCount(bean: BillCount?) {
                activity!!.runOnUiThread {
                    if (bean!=null){
                        showAnimal(tv_yangzhi_first, bean?.insurebillCou ?: "0")
                        showAnimal(tv_yangzhi_second, bean?.lipeibaoanCou ?: "0")
                        showAnimal(tv_yangzhi_third, bean?.lipeichakanCou ?: "0")
                        showAnimal(tv_yangzhi_fourth, bean?.animalsendbill_cou ?: "0")
                        showAnimal(tv_zhongzhi_first, bean?.landbillCou ?: "0")
                        showAnimal(tv_zhongzhi_second, bean?.landbaoanCou ?: "0")
                        showAnimal(tv_zhongzhi_third, bean?.landchakanCou ?: "0")
                        showAnimal(tv_zhongzhi_fourth, bean?.landsendbill_cou ?: "0")
                    }
                }
            }
        })
    }

    fun getBannerData(newsLists: List<NewsList>?) {
        if (list_pic != null) list_pic!!.clear()
        for (item in newsLists!!) {
            val pictureurl = item.pictureurl
            if (item.pictureurlIndex in 1..3 && !pictureurl.isNullOrBlank()) {
                list_pic!!.add(pictureurl)
            }
        }
        banner!!.setIndicatorRes(R.drawable.dot_unselect, R.drawable.dot_select)
        banner!!.setPages(list_pic as List<String?>?) { BannerViewHolder() }
        banner!!.start()

    }

    override fun onPause() {
        super.onPause()
        banner!!.pause()
        tv_yangzhi_first!!.destroy()
        tv_yangzhi_second!!.destroy()
        tv_yangzhi_third!!.destroy()
        tv_zhongzhi_first!!.destroy()
        tv_zhongzhi_second!!.destroy()
        tv_zhongzhi_third!!.destroy()

    }

    override fun initImmersionBar() {
        immersionBar {

            statusBarDarkFont(false)
                    .statusBarView(view_)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun haveRoles(type: String, FObjSubGroup: String, FObjectName: String): Boolean {
        return haveRoles(roles, type, FObjSubGroup, FObjectName)
        //  return false


    }
}