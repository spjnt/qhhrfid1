package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.BaoAnListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.ActivityFieldSampling
import tramais.hnb.hhrfid.ui.crop.CropCheckPassActivity
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.Utils.callPhone

class ActivityCheck : BaseActivity() {
    private var lvContent: RecyclerView? = null
    private val mContext: Context = this@ActivityCheck
    private lateinit var mEtSearch: EditText
    private lateinit var mBtnSearch: Button
    private lateinit var mTypeChoice: TextView
    private lateinit var mIvArrow: ImageView
    private lateinit var mRlAnimalType: LinearLayout
    private var mRefreshLayout: SmartRefreshLayout? = null
    private var choice_type: MutableList<String> = mutableListOf("全部", "待查勘", "已查勘")
    private var chouyang_type: MutableList<String> = mutableListOf("全部", "待抽样", "已抽样")

    private var submit_type: MutableList<String> = mutableListOf("全部", "待同步", "已同步")
    var fenPeis: MutableList<FenPei?> = ArrayList()
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                3 -> {
                    fenPeis = msg.obj as MutableList<FenPei?>
                    setAdapter(
                            fenPeis, isLoadMore
                    )
                    // lvContent!!.adapter = CheckAdapter(context, fenPeis).also { }
                    hideAvi()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
    }

    override fun initView() {

        lvContent = findViewById(R.id.lv_content)
        mRlAnimalType = findViewById(R.id.rl_animal_type)
        mTypeChoice = findViewById(R.id.type_choice)
        mIvArrow = findViewById(R.id.iv_arrow)
        mEtSearch = findViewById(R.id.et_search)
        mBtnSearch = findViewById(R.id.btn_search)
        mRefreshLayout = findViewById(R.id.refresh)
        lvContent!!.layoutManager = LinearLayoutManager(this)

        initAdapter()
    }

    var module_name: String? = null
    var module_type: String? = null
    override fun initData() {
        module_name = intent.getStringExtra(Constants.MODULE_NAME)
        module_name?.let {
            if (it!!.contains("-")) {
                module_name = it.split("-")[0]
                module_type = it.split("-")[1]
            }
        }
        setTitleText(module_type)
        mEtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (NetUtil.checkNet(mContext)) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //延迟600ms，如果不再输入字符，则执行该线程的run方法
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 600)
                } /*else {
                    getBillCache(editable.toString(), Utils.getText(mTypeChoice))
                }*/
            }
        })
        initRefresh()
    }

    private val delayRun = Runnable {
        isLoadMore = false
        currentIndex = 1
        if (NetUtil.checkNet(this))
            getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), currentIndex)
        else {
            netTips()
        }
    }

    override fun onResume() {
        super.onResume()

        if (NetUtil.checkNet(mContext)) {
            if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
            //延迟600ms，如果不再输入字符，则执行该线程的run方法
            if (handler != null && delayRun != null) handler.postDelayed(delayRun, 500)
        }else{
            netTips()
        }
    }

    var end_type: MutableList<String> = ArrayList()
    override fun initListner() {
        mRlAnimalType.setOnClickListener {
            isLoadMore = false
            mIvArrow.animate().setDuration(100).rotation(90f).start()
            end_type?.clear()
            if (module_type == "现场抽样")
                end_type.addAll(chouyang_type)
            else if (module_type == "提交核心")
                end_type.addAll(submit_type)
            else
                end_type.addAll(choice_type)
            PopuUtils(this).initChoicePop(mRlAnimalType, end_type) { str: String? ->
                PreferUtils.putString(mContext, Constants.animal_type_o, str)
                mTypeChoice.text = str
                mIvArrow.animate().setDuration(100).rotation(0f).start()
                if (NetUtil.checkNet(this)) {
                    getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), currentIndex)
                } else {
                    netTips()
                    //   getBillCache(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice))
                }
            }
        }
        if (mAdapter != null && mAdapter!!.data != null) {
            //   LogUtils.e("position  ${mAdapter!!.data.size}")
            mAdapter?.let {
                if (!NetUtil.checkNet(this)) {
                    netTips()
                    return
                }
                it.setOnItemClickListener { _, _, position: Int ->
                    if (position >= it.data.size) return@setOnItemClickListener
                    val fenPei = it.getItem(position) as FenPei
                    var intent = Intent()
                    fenPei.Tag = module_name
                    intent.putExtra("FenPei", fenPei)
                    if (module_name.equals("养殖险")) {
                        when (module_type) {
                            "任务查勘" -> {
                                intent.setClass(this, ActivityFeedCheck::class.java)
                            }

                            "提交核心" -> {
                                intent.setClass(this, ActivitySubmitCore::class.java)
                            }
                        }
                    } else if (module_name.equals("种植险")) {
                        when (module_type) {
                            "任务查勘" -> {
                                intent.setClass(this, CropCheckPassActivity::class.java)
                            }
                            "现场抽样" -> {
                                intent.setClass(this, ActivityFieldSampling::class.java)

                            }
                            "提交核心" -> {
                                intent.setClass(this, ActivitySubmitCore::class.java)

                            }
                        }

                    }
                    startActivity(intent)
                }

            }
        }


    }

    var currentIndex = 1
    var total = 0
    var touBaoBeans: MutableList<FenPei>? = ArrayList()
    private fun getBill(farmName: String, statu: String, pageIndex: Int) {
        showAvi("数据加载中")
        // touBaoBeans?.clear()
        //状态：1新建，3已分配，5已查勘、7已立案、9已定损、
        var statu_ =
                if (module_type == "现场抽样") {
                    when (statu) {
                        "全部" ->
                            "7"
                        "待抽样" ->
                            "f5"
                        "已抽样" ->
                            "f7"
                        else ->
                            "7"
                    }

                } else if (module_type == "提交核心") {
                    if (module_name == "种植险") {
                        when (statu) {
                            "全部" ->
                                "11"
                            "待同步" ->
                                "f9"
                            "已同步" ->
                                "f11"
                            else ->
                                "11"
                        }
                    } else {
                        when (statu) {
                            "全部" ->
                                "9"
                            "待同步" ->
                                "f7"
                            "已同步" ->
                                "f9"
                            else ->
                                "9"
                        }
                    }

                    //任务查勘
                } else {
                    when (statu) {
                        "全部" ->
                            "5"
                        "待查勘" ->
                            "f3"
                        "已查勘" ->
                            "f5"
                        else ->
                            "5"
                    }
                }
        var req_url = if (module_name == "养殖险") {
            Config.GetBaoAn
        } else {
            Config.GetLandBaoAn
        }
        RequestUtil.getInstance(this)!!.getBill(req_url, statu_, farmName, 20, pageIndex, module_name.toString()) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
            hideAvi()
            total = totalNums
            if (datas != null && datas.size > 0)
                touBaoBeans = GsonUtil.instant!!.parseCommonUseArr(datas, FenPei::class.java)
            else {
                touBaoBeans!!.clear()
                showStr(message)
            }

            val msg = Message()
            msg.obj = touBaoBeans
            msg.what = 3
            handler.sendMessage(msg)


        }
    }

    private fun getBillCache(input: String, statu: String) {

        val farmListCaches: MutableList<BaoAnListCache> = java.util.ArrayList()
        farmListCaches.clear()
        if (TextUtils.isEmpty(input)) {
            LitePal.order("creatTime desc").findAsync(BaoAnListCache::class.java).listen { list ->
                if (list.size > 0) {
                    farmListCaches.addAll(list)
                    cacheToLine(farmListCaches, statu)
                }
            }
        } else {
            val numeric = Utils.isNumeric(input)
            if (!numeric) {
                LitePal.where("farmerName like ?", "%$input%").order("creatTime desc").findAsync(BaoAnListCache::class.java).listen { list ->
                    if (list.size > 0) {
                        farmListCaches.addAll(list)
                        cacheToLine(farmListCaches, statu)

                    } else {
                        lvContent!!.adapter = null
                        showStr("未查找到匹配数据")
                    }
                }
            } else {
                LitePal.where("number like ? ", "%$input%").order("creatTime desc").findAsync(BaoAnListCache::class.java).listen { list ->
                    if (list.size > 0) {
                        farmListCaches.addAll(list)
                        cacheToLine(farmListCaches, statu)

                    } else {
                        lvContent!!.adapter = null
                        showStr("未查找到匹配数据")
                    }
                }
            }
        }

    }

    fun cacheToLine(list: MutableList<BaoAnListCache>, statu: String) {
        if (list == null || list.size == 0) return
        touBaoBeans?.clear()
        list.forEach {
            var statu_: Boolean = false
            if (statu.equals("全部")) {
                statu_ = it.status.equals("已查勘") || it.status.equals("已分配")
            } else {
                statu_ = it.status.equals(statu)
            }
            if (statu_) {
                var fenPei = FenPei()
                fenPei.riskAddress = it.riskAddress
                fenPei.riskDate = it.riskDate
                fenPei.baoAnDate = it.baoAnDate
                fenPei.riskReason = it.riskReason
                fenPei.riskQty = it.riskQty
                fenPei.riskProcess = it.riskProcess
                fenPei.status = it.status
                fenPei.employeeNo = it.employeeNo
                fenPei.employeeName = it.employeeName
                fenPei.number = it.number
                fenPei.companyNumber = it.companyNumber
                fenPei.farmerNumber = it.farmerNumber
                fenPei.farmerName = it.farmerName
                fenPei.mobile = it.mobile
                fenPei.insureNumber = it.insureNumber
                touBaoBeans!!.add(fenPei)
            }

        }

        val msg = Message()
        msg.obj = touBaoBeans
        msg.what = 3
        handler.sendMessage(msg)

    }

    private var isLoadMore = false
    private fun initRefresh() {
        mRefreshLayout!!.setOnLoadMoreListener {
            if (total >= 20) {
                isLoadMore = true
                currentIndex += 1
                if (NetUtil.checkNet(this))
                    getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), currentIndex)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }


    }

    var mAdapter: BaseQuickAdapter<FenPei?, BaseViewHolder>? = null
    fun initAdapter() {
        lvContent!!.adapter = object : BaseQuickAdapter<FenPei?, BaseViewHolder>(R.layout.item_check_dingsun) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: FenPei?) {
                var tv_first = holder.getView<TextView>(R.id.tv_first)
                var tv_middle = holder.getView<TextView>(R.id.tv_second)
                var tv_third = holder.getView<TextView>(R.id.tv_third)
                var tv_unupload = holder.getView<TextView>(R.id.tv_unupload)
                var tv_fourth = holder.getView<TextView>(R.id.tv_fourth)
                var tv_five = holder.getView<TextView>(R.id.tv_five)

                item?.let { fileInfo ->
                    val status = fileInfo.status
                    if (module_type == "任务查勘") {
                        if (status == "查勘" || status == "查勘定损") {
                            tv_unupload!!.text = "已查勘"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.black))
                        } else if (status == "分配") {
                            tv_unupload!!.text = "待查勘"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.orange))
                        } else {
                            tv_unupload!!.text = "未知状态"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.red))
                        }
                    } else if (module_type == "现场抽样") {
                        if (status == "查勘定损") {
                            tv_unupload!!.text = "待抽样"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.orange))
                        } else if (status == "抽样") {
                            tv_unupload!!.text = "已抽样"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.black))
                        } else {
                            tv_unupload!!.text = "未知状态"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.red))
                        }
                    } else if (module_type == "提交核心") {
                        if (status == "公示") {
                            tv_unupload!!.text = "待同步"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.orange))
                        } else if (status == "同步") {
                            tv_unupload!!.text = "已同步"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.black))
                        } else {
                            tv_unupload!!.text = "未知状态"
                            tv_unupload!!.setTextColor(mContext.resources.getColor(R.color.red))
                        }
                    }

                    tv_third!!.setOnClickListener { v: View? ->
                        if (TextUtils.isEmpty(fileInfo.mobile)) {
                            return@setOnClickListener
                        }
                        callPhone(context, fileInfo.mobile)
                    }
                    // holder.tv_unupload!!.text = fileInfo.status
                    tv_first!!.text = "投保人: ${fileInfo.farmerName}"
                    tv_middle!!.text = fileInfo.number
                    tv_third!!.text = fileInfo.mobile
                    tv_fourth!!.text = fileInfo.fItemDetailList
                    tv_five!!.text = fileInfo.riskAddress


                }
            }
        }.also { mAdapter = it }
    }

    fun setAdapter(fenPei: MutableList<FenPei?>, isLoadMore: Boolean) {
        //  LogUtils.e("fenP  ${fenPei.size}  ${isLoadMore}")
        if (!isLoadMore)
            if (fenPei == null || fenPei.size == 0) {
                showStr("暂无查勘任务")
                mAdapter!!.setList(null)
                return
            }
        if (isLoadMore) {

            if (fenPei != null && fenPei.isNotEmpty())
                mAdapter!!.addData(fenPei)
        } else
            mAdapter!!.setNewInstance(fenPei)
    }
}