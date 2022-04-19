package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import org.litepal.FluentQuery
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FarmList
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.FarmListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.ActtivityCropSearchBy
import tramais.hnb.hhrfid.ui.crop.CropMakeDealActivity
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.Utils
import java.io.Serializable


class ActivityFarmList : BaseActivity() {
    var currentPage: Int = 1
    var pages = 0
    private var mEtSearch: EditText? = null
    private var mLvContent: RecyclerView? = null

    //    private var mPrePage: TextView? = null
//    private var mPages: TextView? = null
//    private var mNextPage: TextView? = null
//    private var mLlBottomPage: RelativeLayout? = null
    private val context: Context = this@ActivityFarmList
    private var underWrites: MutableList<FarmList?>? = null
    private var writes: MutableList<FarmList>? = ArrayList()
    private var module_name: String? = null
    private var mRefreshLayout: SmartRefreshLayout? = null
    private val handler: Handler? = object : Handler() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UNDER_LIST -> {
                    //underWrites?.clear()
                    underWrites = msg.obj as MutableList<FarmList?>

                    setUnderAdapter(underWrites!!, isRefresh)
                    hideAvi()

                }
            }
        }
    }
    private var getResult: String? = null
    private val delayRun: Runnable? = Runnable {
        currentPage = 1
        getUnderList(Utils.getEdit(mEtSearch), currentPage, module_name!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_list)
    }

    private var mAdapter: QuickAdapter? = null

    //var movies: List<Movie?> = ArrayList()
    override fun initView() {
        setTitleText("农户列表")

        mRefreshLayout = findViewById(R.id.refresh)
        mEtSearch = findViewById(R.id.et_search)
        mLvContent = findViewById(R.id.lv_content)
        if (!NetUtil.checkNet(this))
            mEtSearch!!.hint = "请输入投保户人姓名/手机号码查询"
        mAdapter = QuickAdapter()

        //  mLvContent!!.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        mLvContent!!.layoutManager = LinearLayoutManager(this)
        mLvContent!!.adapter = mAdapter
    }

    var isRefresh: Boolean = false
    private fun initRefresh() {
        mRefreshLayout!!.setOnLoadMoreListener {

            if (total == 20) {
                currentPage += 1
                isRefresh = true
                getUnderList(Utils.getEdit(mEtSearch), currentPage, module_name!!)
                it.finishLoadMore()
            } else {
                isRefresh = false
                it.finishLoadMoreWithNoMoreData()
            }
        }
    }

    override fun initData() {
        val intent = intent
        module_name = intent.getStringExtra(Constants.MODULE_NAME)
        // LogUtils.e("module_name  $module_name")
        if (!module_name.isNullOrEmpty()) {
            if (module_name == "养殖户登记") {
                setRightText("新增农户")
                //  hideRootDelete(0)
            } else if (module_name == "制作投保清单") {
                setRightText("")
            } else if (module_name == "种植险") {
                setRightText("")
                setTitleText("农户承保")
                //  hideRootDelete(8)
            } else if (module_name == "智能点数") {
                setRightText("")
                //hideRootDelete(View.GONE)
            } else if (module_name == "养殖户查询" || module_name == "种植户查询") {
                setRightText("")
                //  hideRootDelete(8)
            } else {
                setRightText("")
            }
        }
        getResult = intent.getStringExtra("GetResult")
        mEtSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                isRefresh = false
                if (isResume) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //延迟600ms，如果不再输入字符，则执行该线程的run方法
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 600)
                }
            }
        })
        initRefresh()
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }

    var isResume: Boolean = false
    override fun onResume() {
        super.onResume()

        currentPage = 1

        getUnderList(Utils.getEdit(mEtSearch), currentPage, module_name!!)

        isResume = true
    }

    override fun initListner() {
        if (mAdapter != null)
            mAdapter!!.setOnItemClickListener { adapter, view, position ->

                val underWrites = mAdapter!!.getItem(position)
                //  val underWrites = underWrites!![position]
                underWrites?.let { underWrites_ ->

                    module_name?.let {
                        when (module_name) {
                            "制作投保清单" -> {
                                val intent = Intent(this@ActivityFarmList, ActivityUnderWriteDeal::class.java)
                                intent.putExtra("underWrite", underWrites_ as Serializable)
                                intent.putExtra(Constants.Type, "from_farmerlist")
                                startActivity(intent)
                                finish()
                            }
                            "养殖户登记" -> {
                                val intent = Intent(this@ActivityFarmList, ActivitySaveFram::class.java)
                                intent.putExtra("underWrites", underWrites_ as Serializable)
                                intent.putExtra("type", "Content")
                                startActivity(intent)
                            }
                            "承保验标" -> {
                                val intent = Intent(this@ActivityFarmList, CameraActivity::class.java)
                                intent.putExtra(Constants.farmer_name, underWrites_.name)
                                intent.putExtra(Constants.farmer_id_nums, underWrites_.zjNumber)
                                intent.putExtra(Constants.farmer_tel, underWrites_.mobile)
                                intent.putExtra(Constants.Ba_num, underWrites_.number)
                                intent.putExtra(Constants.farmer_address, underWrites_.raiseAddress)
                                intent.putExtra(Constants.farmer_sign, underWrites_.signPicture)
                                intent.putExtra(Constants.farmer_zjCategory, underWrites_.zjCategory)//证件类型
                                intent.putExtra(Constants.category, underWrites_.category)//投保类型  自行/集体投保
                                intent.putExtra(Constants.farmer_area, underWrites_.area)//集体户名
                                intent.putExtra(Constants.ear_tag, "")
                                startActivity(intent)
                            }
                            "出险报案" -> {
                                //数据是使用Intent返回
                                val intent = Intent()
                                //把返回数据存入Intent
                                intent.putExtra("underWrites", underWrites_ as Serializable)
                                //设置返回数据
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            "种植险" -> {
                                val intent = Intent(this@ActivityFarmList, CropMakeDealActivity::class.java)
                                intent.putExtra("underWrites", underWrites_ as Serializable)
                                intent.putExtra("type", "Content1")
                                startActivity(intent)
                            }
                            "智能点数" -> {
                                val intent = Intent(this@ActivityFarmList, ActivityNumber::class.java)
                                intent.putExtra(Constants.farmer_name, underWrites_.name)
                                intent.putExtra(Constants.farmer_id_nums, underWrites_.zjNumber)
                                startActivity(intent)
                            }
                            "养殖户查询" -> {
                                val intent = Intent(this@ActivityFarmList, ActtivitySearchByFarmerOrTel::class.java)
                                intent.putExtra(Constants.farmer_name, underWrites_.name)
                                intent.putExtra(Constants.farmer_id_nums, underWrites_.zjNumber)
                                startActivity(intent)
                            }
                            "种植户查询" -> {
                                val intent = Intent(this@ActivityFarmList, ActtivityCropSearchBy::class.java)
                                intent.putExtra(Constants.farmer_name, underWrites_.name)
                                intent.putExtra(Constants.farmer_id_nums, underWrites_.zjNumber)
                                startActivity(intent)
                            }
                        }

                    }


                }
            }
        mCustomTitle!!.mRootDelete!!.setOnClickListener { view: View? ->
            if (module_name == "养殖户登记") {
                val intent = Intent(this, ActivitySaveFram::class.java)
                intent.putExtra("type", "new")
                startActivity(intent)
            } else if (module_name == "种植险") {
                val intent = Intent(this@ActivityFarmList, CropMakeDealActivity::class.java)
                intent.putExtra("type", "farmerlist")
                startActivity(intent)
            }
        }
    }

    private fun getListCache(input: String, pagIndex: Int) {
        val _cache_to: MutableList<FarmListCache> = ArrayList()
        val strArr: Array<String>? = if (input.isEmpty()) {
            null
        } else if (!Utils.isNumeric(input)) {
            arrayOf("name like ?", "%$input%")
        } else {
            arrayOf("mobile like ?", "%$input%")
        }

        var fluentQuery: FluentQuery?

        fluentQuery = if (strArr != null && strArr.isNotEmpty()) {
            LitePal.where(*strArr)
        } else {
            LitePal.where(null)
        }
        if (fluentQuery != null)
            fluentQuery = if (pagIndex != 1) {
                fluentQuery.order("creatTime desc").offset(20).limit(20)
            } else {
                fluentQuery.order("creatTime desc").limit(20)
            }
        _cache_to.clear()
        fluentQuery.findAsync(FarmListCache::class.java).listen { list: List<FarmListCache> ->
            total = list.size
            _cache_to.addAll(list.toMutableList())

            // LogUtils.e("total  $total  ${_cache_to.size}")
            cacheToLine(_cache_to)
        }
    }

    private fun cacheToLine(caches: List<FarmListCache>?) {
        writes?.clear()
        if (caches != null && caches.isNotEmpty()) {
            for (item in caches) {
                val list = FarmList()
                if (module_name != "养殖户登记") {
                    if (!item.zjCategory.isNullOrEmpty() && !item.category.isNullOrEmpty()
                            && item.zjCategory == "营业执照" && item.category == "集体投保") {
                        continue
                    }
                }
                list.accountName = item.accountName
                list.accountNumber = item.accountNumber
                list.area = item.area
                list.bankName = item.bankName
                list.category = item.category
                list.mobile = item.mobile
                list.name = item.name
                list.number = item.number
                list.raiseAddress = item.raiseAddress
                list.sfzAddress = item.sFZAddress
                list.zjCategory = item.zjCategory
                list.zjNumber = item.zjNumber
                list.signPicture = item.singPic
                list.fValidate = item.overdueTime
                list.isPoor = if (item.isPoor.isNullOrEmpty()) {
                    0
                } else {
                    item.isPoor!!.toInt()
                }
                list.remark = item.remark
                writes!!.add(list)
            }

            val message = Message()
            message.obj = writes
            message.what = UNDER_LIST
            handler!!.sendMessage(message)
        }
    }

    var total = 0
    private fun getUnderList(input: String, PageIndex: Int, module_name: String) {
        // if (writes != null) writes!!.clear()
        showAvi()
        val flag = if (module_name == "种植险") {
            module_name
        } else {
            "养殖险"
        }
        if (NetUtil.checkNet(this)) {
           // LogUtils.e("come in  with  net  $input")
            RequestUtil.getInstance(context)!!.getUnderList(input, PageIndex, flag, module_name) { rtnCode: Int, meg: String?, totalNums: Int, datas: JSONArray? ->
                hideAvi()
                total = totalNums
                //
                //  writes?.clear()
                if (datas != null && datas.size > 0) {
                    writes = instant!!.parseCommonUseArr(datas, FarmList::class.java)
                } else {
                    showStr(meg)
                    writes!!.clear()
                }

                val message = Message()
                message.obj = writes
                message.what = UNDER_LIST
                handler!!.sendMessage(message)

            }
        } else {
          //  LogUtils.e("come in  no  net  $input")
            getListCache(input, PageIndex)
        }

    }

    private fun setUnderAdapter(underWrites: MutableList<FarmList?>, isReresh: Boolean) {
       // LogUtils.e("under  ${Gson().toJson(underWrites)}  ${isReresh}")
        /* if (!isReresh)
             if (underWrites.isNullOrEmpty() || underWrites.size == 0) {
                 showStr("暂无数据展示")
                 mAdapter!!.setList(null)
                 return
             }*/
        if (isReresh) {
            if (underWrites.isNotEmpty())
                mAdapter!!.addData(underWrites)
        } else mAdapter!!.setList(underWrites)
    }

    companion object {
        private const val UNDER_LIST = 1 shl 1
    }


    class QuickAdapter : BaseQuickAdapter<FarmList?, BaseViewHolder>(R.layout.item_only_toubao) {
        override fun convert(holder: BaseViewHolder, item: FarmList?) {
            item?.let { farmer_ ->
                holder.getView<TextView>(R.id.tv_insure).visibility = View.GONE
                holder.setText(R.id.tv_name, farmer_.name)
                if (farmer_.area.isNullOrEmpty()) {
                    holder.setGone(R.id.tv_category_name, true)
                } else {
                    holder.setText(R.id.tv_category_name, farmer_.area)
                }
               // LogUtils.e("${farmer_.name}")
                holder.setText(R.id.tv_category, farmer_.zjNumber)
                holder.setText(R.id.tv_phone_num, farmer_.mobile)
                holder.setText(R.id.tv_name, farmer_.name)
                holder.getView<TextView>(R.id.tv_phone_num).setOnClickListener {
                    if (farmer_.mobile.isNullOrEmpty()) return@setOnClickListener
                    Utils.callPhone(context, farmer_.mobile)
                }
            }
        }
    }

}