package tramais.hnb.hhrfid.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import org.litepal.FluentQuery
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.EmployeeListBean
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetEmployee
import tramais.hnb.hhrfid.interfaces.GetEmployeeList
import tramais.hnb.hhrfid.litePalBean.BaoAnListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogCheckInfo
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceEmployee
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.Utils.callPhone

class ActivityFenPei : BaseActivity() {
    private var mListview: RecyclerView? = null
    private val context: Context = this@ActivityFenPei
    private var toOther: TextView? = null
    private var toSelf: TextView? = null

    private lateinit var mEtSearch: EditText

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                3 -> {
                    touBaoBeans = msg.obj as MutableList<FenPei>?
                    //  mAdapter!!.setList(touBaoBeans)
                    setAdapter(touBaoBeans)
                    // mListview!!.adapter = FenPeiAdapter(context, touBaoBeans).also { adapter = it }
                    hideAvi()
                }
            }
        }
    }

    //private var employeeName: TextView? = null
    private var touBaoBeans: MutableList<FenPei>? = ArrayList()
    private var choice_type: MutableList<String> = mutableListOf("全部", "待分配", "已分配")
    private var employeeListBeans: MutableList<EmployeeListBean.DataDTO?>? = ArrayList()
    private lateinit var mTypeChoice: TextView
    private lateinit var mIvArrow: ImageView
    private lateinit var mRlAnimalType: LinearLayout

    private var mRefreshLayout: SmartRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baoan_fenpei)
    }

    override fun initView() {
        mListview = findViewById<View>(R.id.lv_content) as RecyclerView
        setTitleText("任务调度")
        //setTitlTextColor(R.color.light_blue)
        toOther = findViewById(R.id.fenpei_to_other)
        toSelf = findViewById(R.id.fenpei_to_self)
        //  employeeName = findViewById(R.id.empolyee_name)
        mEtSearch = findViewById(R.id.et_search)
        //  mBtnSearch = findViewById(R.id.btn_search)
        mRlAnimalType = findViewById(R.id.rl_animal_type)
        mTypeChoice = findViewById(R.id.type_choice)
        mIvArrow = findViewById(R.id.iv_arrow)
        mRefreshLayout = findViewById(R.id.refresh)

        //  mListview!!.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        mListview!!.layoutManager = LinearLayoutManager(mContext)
        initAdapter()
    }

    var currentPage = 1
    var userName_: String = ""
    var userNumber: String = ""
    var module_name: String? = null
    override fun initData() {
        module_name = intent.getStringExtra(Constants.MODULE_NAME)
        getEmployeList()
        userName_ = PreferUtils.getString(this, Constants.UserName)
        toSelf!!.text = "分配给自己 ${"("}${userName_}${")"} "

        userNumber = PreferUtils.getString(this, Constants.userNumber)
        mEtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (NetUtil.checkNet(context)) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //延迟600ms，如果不再输入字符，则执行该线程的run方法
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 500)
                } /*else {
                    getBillCache(editable.toString(), Utils.getText(mTypeChoice))
                }*/
            }
        })
        initRefresh()
    }

    private val delayRun = Runnable {
        isLoadMore = false
        currentPage = 1
        touBaoBeans?.clear()
        getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), "", "", currentPage)

    }

    override fun onResume() {
        super.onResume()
        if (NetUtil.checkNet(this)) {
            if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
            //延迟600ms，如果不再输入字符，则执行该线程的run方法
            if (handler != null && delayRun != null) handler.postDelayed(delayRun, 100)
        }/* else {
            getBillCache(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice))
        }*/
    }

    var employeelist: MutableList<String> = ArrayList()
    private fun getEmployeList() {
        employeeListBeans!!.clear()
        employeelist.clear()
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this)!!.getEmplyeListnNew("", "全部", object : GetEmployeeList {
                override fun getEmployee(infoDetail: EmployeeListBean?) {
                    infoDetail?.let {
                        if (it.data != null && it.data!!.isNotEmpty()) {
                            employeeListBeans!!.addAll(it.data!!)
                        }

                    }
                }
            })
        }/* else {
            LitePal.findAllAsync(EmployeeCache::class.java).listen {
                for (item in it) {
                    var bean = EmployeeListBean.DataDTO()
                    bean.employeeName = item.employeeName
                    bean.employeeNo = item.employeeNo
                    employeeListBeans!!.add(bean)
                    val employeeName = item.employeeName
                    if (!employeelist.contains(employeeName) && !TextUtils.isEmpty(employeeName)) {
                        employeelist.add(employeeName)
                    }
                }
            }
        }*/

    }

    private fun getBill(farmName: String, statu: String, mobile: String, sfzNumber: String, pageIndex: Int) {
        //  touBaoBeans!!.clear()

        //状态：1新建，3已分配，5已查勘、7已立案、9已定损、

        var statu_ = when (statu) {
            "全部" ->
                "3"
            "待分配" ->
                "f1"
            "已分配" ->
                "f3"
            else ->
                "3"

        }
        val req_url = if (module_name == "养殖险") {
            Config.GetBaoAn
        } else {
            Config.GetLandBaoAn
        }
        RequestUtil.getInstance(this)!!.getBill(req_url, statu_, farmName, 20, pageIndex, module_name.toString()) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
            total = totalNums
            if (datas != null && datas.size > 0)
                touBaoBeans = GsonUtil.instant!!.parseCommonUseArr(datas, FenPei::class.java)
            else {
                showStr(message)
                return@getBill
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
        var array: Array<String>? = null
        array = if (TextUtils.isEmpty(input)) {
            null
        } else {
            val numeric = Utils.isNumeric(input)
            if (!numeric) {
                arrayOf("farmerName like ?", "%$input%")
            } else {
                arrayOf("number like ?  ", "%$input%")
            }
        }

        val fluentQuery: FluentQuery? = if (array != null) {
            LitePal.where(*array).order("number desc").limit(20)
        } else {
            LitePal.where(null).order("number desc").limit(20)
        }
        fluentQuery?.findAsync(BaoAnListCache::class.java)?.listen { list: List<BaoAnListCache> ->
            if (list.isNotEmpty()) {
                cacheToLine(farmListCaches, statu)
            } else {
                //  mListview!!.adapter = null
                showStr("未查找到匹配数据")
            }
        }
    }

    private fun cacheToLine(list: MutableList<BaoAnListCache>, statu: String) {
        if (list.size == 0) return
        touBaoBeans?.clear()
        list.forEach {
            //  var statu_: Boolean = false
            val statu_ = if (statu == "全部") {
                it.status.equals("新建") || it.status.equals("已分配")
            } else {
                it.status.equals(statu)
            }
            if (statu_) {
                val fenPei = FenPei()
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
        /* if (touBaoBeans != null && touBaoBeans!!.size > 0) {
             val msg = Message()
             msg.obj = touBaoBeans
             msg.what = 3
             handler.sendMessage(msg)
         } else {
             mListview!!.adapter = null
             showStr("暂无查勘任务")
         }*/
    }

    var numbers: MutableList<String?> = ArrayList()
    override fun initListner() {
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val fenPei = it.getItem(position) as FenPei
                fenPei.let {
                    val dialogCheckInfo = DialogCheckInfo(context, "报案信息", it)
                    if (!isFinishing && !dialogCheckInfo.isShowing) dialogCheckInfo.show()
                }
            }
        }

        toOther!!.setOnClickListener { v: View? ->
            if (!NetUtil.checkNet(this)) {
                netTips()
                return@setOnClickListener
            }
            if (mAdapter == null) {
                return@setOnClickListener
            }

            if (selected.isEmpty()) {
                showStr("请选择分配任务")
                return@setOnClickListener
            }
            selected.entries.forEach {
                if (it.value) {
                    val number = it.key.number
                    numbers.add(number.toString())
                }
            }
            if (numbers.isEmpty()) {
                showStr("请选择分配任务")
                return@setOnClickListener
            }
            if (employeeListBeans == null || employeeListBeans!!.size == 0) {
                showStr("暂无查勘员可选")
                return@setOnClickListener
            }

            val dialogChoiceEmployee = DialogChoiceEmployee(this, employeeListBeans, object : GetEmployee {
                override fun getString(data_: EmployeeListBean.DataDTO?) {
                    data_?.let {
                        toOther!!.text = "任务分配 ${"("}${it.employeeName}${")"}"

                        userName_ = it.employeeName.toString()
                        userNumber = it.employeeNo.toString()
                        if (NetUtil.checkNet(context)) {
                            RequestUtil.getInstance(this@ActivityFenPei)!!.assignBaoAn(Utils.list2String(numbers), userNumber, userName_) { str ->
                                showStr(str)
                                numbers.clear()
                                selected.clear()
                                if (str.contains("成功")) {
                                    touBaoBeans?.clear()
                                    getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), "", "", 1)
                                }
                            }
                        }/* else {
                            upDateInfo(numbers, userNumber, userName_)
                        }*/
                    }

                }
            })
            if (!dialogChoiceEmployee.isShowing && !isFinishing) dialogChoiceEmployee.show()
            /*PopuChoice(this@ActivityFenPei, toOther, "请选择理赔员", employeelist) { str: String? ->
                toOther!!.text = "任务分配 ${"("}${userName_}${")"} "
                //  employeeName!!.text = str
                userName_ = str.toString()
                userNumber = employeeListBeans!!.filter { it.employeeName.equals(str) }[0].employeeNo


            }*/

        }
        toSelf!!.setOnClickListener {
            if (!NetUtil.checkNet(this)) {
                netTips()
                return@setOnClickListener
            }

            if (mAdapter == null) return@setOnClickListener

            if (selected.isEmpty()) {
                showStr("请选择分配任务")
                return@setOnClickListener
            }
            selected.entries.forEach {

                if (it.value) {
                    val number = it.key.number
                    numbers.add(number.toString())
                }
            }
            if (numbers.isEmpty()) {
                showStr("请选择分配任务")
                return@setOnClickListener
            }
            if (NetUtil.checkNet(context)) {
                RequestUtil.getInstance(this)!!.assignBaoAn(Utils.list2String(numbers), userNumber, userName_) { str ->
                    showStr(str)
                    numbers.clear()
                    selected.clear()
                    if (str.contains("成功")) {
                        touBaoBeans?.clear()
                        getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), "", "", 1)
                    }
                }
            } /*else {
                upDateInfo(numbers, userNumber, userName_)
            }*/


        }
        mRlAnimalType.setOnClickListener {
            isLoadMore = false
            mIvArrow.animate().setDuration(100).rotation(90f).start()
            PopuUtils(this).initChoicePop(mRlAnimalType, choice_type) { str: String? ->
                PreferUtils.putString(context, Constants.animal_type_o, str)
                mTypeChoice.text = str
                mIvArrow.animate().setDuration(100).rotation(0f).start()
                if (NetUtil.checkNet(this)) {
                    touBaoBeans?.clear()
                    getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), "", "", 1)
                }
            }
        }
    }

    fun upDateInfo(numbers: MutableList<String?>, userNo: String, employeeName: String) {
        if (numbers.size == 0) return
        numbers.forEach {
            val banAnInfo = BaoAnListCache()
            banAnInfo.status = "已分配"
            banAnInfo.employeeNo = userNo
            banAnInfo.employeeName = employeeName
            val updateAll = banAnInfo.updateAll("number= ?", it)
            numbers.remove(it)
            showStr(if (updateAll >= 1) "分配成功" else "分配失败")
        }
        selected.clear()
        getBillCache("", Utils.getText(mTypeChoice))
    }

    var total = 0
    private var isLoadMore = false
    private fun initRefresh() {
        mRefreshLayout!!.setOnLoadMoreListener {
            if (total == 20) {
                currentPage += 1
                isLoadMore = true
                getBill(Utils.getEdit(mEtSearch), Utils.getText(mTypeChoice), "", "", currentPage)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }
    }

    var mContext: Context = this@ActivityFenPei
    var mAdapter: BaseQuickAdapter<FenPei, BaseViewHolder>? = null
    fun initAdapter() {
        mListview!!.adapter = object : BaseQuickAdapter<FenPei, BaseViewHolder>(R.layout.item_fenpei) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: FenPei) {
                var ll_right = holder.getView<CardView>(R.id.ll_right)
                val tv_first = holder.getView<TextView>(R.id.tv_first)
                val tv_middle = holder.getView<TextView>(R.id.tv_second)
                val tv_third = holder.getView<TextView>(R.id.tv_third)
                val tv_unupload = holder.getView<TextView>(R.id.tv_unupload)
                val tv_fourth = holder.getView<TextView>(R.id.tv_fourth)
                val tv_five = holder.getView<TextView>(R.id.tv_five)
                val mItemBag = holder.getView<ImageView>(R.id.item_bag)
                item.let { fileInfo ->
                    val status = fileInfo.status
                    if (status == "分配") {
                        tv_unupload.text = "已分配"
                        tv_unupload.setTextColor(mContext.resources.getColor(R.color.black))
                    } else if (status == "新建") {
                        tv_unupload.text = "待分配"
                        tv_unupload.setTextColor(mContext.resources.getColor(R.color.orange))
                    }
                    tv_first.text = "投保人: ${fileInfo.farmerName}"
                    tv_middle.text = fileInfo.number
                    tv_third.text = fileInfo.mobile
                    val fHandleInfoDetail = fileInfo.FHandleInfoDetail
                    tv_fourth.text = if (fHandleInfoDetail!!.isNotEmpty()) {
                        fileInfo.fItemDetailList + "\n" + fileInfo.FHandleInfoDetail
                    } else {
                        fileInfo.fItemDetailList
                    }
                    tv_five.text = fileInfo.riskAddress
                    mItemBag.isSelected = selected.containsKey(fileInfo)
                    mItemBag.isSelected = fileInfo.isCheck
                    mItemBag.setOnClickListener { v: View? ->
                        fileInfo.isCheck = !fileInfo.isCheck
                        mItemBag.isSelected = !fileInfo.isCheck
                        selected[fileInfo] = fileInfo.isCheck
                        if (!fileInfo.isCheck)
                            selected.remove(fileInfo)
                        notifyDataSetChanged()
                    }
                    tv_third.setOnClickListener { v: View? ->
                        if (TextUtils.isEmpty(fileInfo.mobile)) {
                            return@setOnClickListener
                        }
                        callPhone(context, fileInfo.mobile)
                    }


                }
            }
        }.also { mAdapter = it }
    }

    var selected: MutableMap<FenPei, Boolean> = HashMap()
    fun setAdapter(fenPei: MutableList<FenPei>?) {
        if (!isLoadMore)
            if (fenPei == null || fenPei.size == 0) {
                showStr("暂无数据展示")
                mAdapter!!.setList(null)
                return
            }

        if (isLoadMore) {
            if (fenPei != null && fenPei.isNotEmpty())
                fenPei?.let { mAdapter!!.addData(it) }
        } else {
            mAdapter!!.setNewInstance(fenPei)
        }

    }


}