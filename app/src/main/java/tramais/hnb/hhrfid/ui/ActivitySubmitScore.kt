package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.itextpdf.awt.AsianFontMapper
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BadPdfFormatException
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetResult
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogCheckInfo
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.ShareUtils.shareWechatFriend
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ActivitySubmitScore : BaseActivity() {
    var pageIndex = 1
    var pdf_address = ""
    private val mContext: Context = this@ActivitySubmitScore
    private var etSearch: EditText? = null

    private var rv: RecyclerView? = null
    private var mRefreshLayout: SmartRefreshLayout? = null

    private var llTitle: LinearLayout? = null
    private var ivBack_: ImageView? = null
    private var ivHome: ImageView? = null
    private var title: TextView? = null
    private var rlPayType: LinearLayout? = null
    private var mIvArrow: ImageView? = null
    private var mTypeChoice: TextView? = null
    var fenPeis: MutableList<FenPei?> = ArrayList()
    private val handler = Handler { msg ->
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
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_pay)
        // initView()
    }


    override fun initView() {
        hideAllTitle()

        mRefreshLayout = findViewById(R.id.refresh)
        etSearch = findViewById(R.id.et_search)

        rv = findViewById(R.id.lv_content)
        llTitle = findViewById(R.id.ll_title)
        llTitle!!.visibility = View.GONE
        ivBack_ = findViewById(R.id.iv_back_public)
        ivHome = findViewById(R.id.setting_public)
        ivHome!!.visibility = View.GONE
        title = findViewById(R.id.root_title_public)
        rlPayType = findViewById(R.id.rl_pay_type)
        mIvArrow = findViewById(R.id.iv_arrow)
        mTypeChoice = findViewById(R.id.type_choice)
        title!!.text = "提交核心"
        //  rlPayType!!.visibility = View.GONE
        // rv!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv!!.layoutManager = LinearLayoutManager(this)
        initAdapter()

    }

    override fun onResume() {
        super.onResume()

        if (NetUtil.checkNet(mContext)) {
            isLoadMore = false
            currentIndex = 1
            getBill(Utils.getEdit(etSearch), "9", currentIndex)
        }
    }

    override fun initData() {
        initRefresh()
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (NetUtil.checkNet(mContext)) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //延迟600ms，如果不再输入字符，则执行该线程的run方法
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 600)
                }
            }
        })
    }

    override fun initListner() {
        //  ivHome!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        ivBack_!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val item = adapter.getItem(position) as FenPei

                DialogCheckInfo(this, "提交核心", item).show()
            }
        }


        rlPayType!!.setOnClickListener {
            isLoadMore = false
            mIvArrow!!.animate().setDuration(100).rotation(90f).start()//缴费状态：未同步 传f5 ，未缴费  传f7 ，已缴费  传f9
            PopuUtils(this).initChoicePop(rlPayType, choice_type) { str: String? ->
//未初审  传f1，未公示  传f3 ，已公示  传f5
                mTypeChoice!!.text = str

                mIvArrow!!.animate().setDuration(100).rotation(0f).start()
                getBill(Utils.getEdit(etSearch), str!!, pageIndex)
            }
        }
    }

    private var choice_type: MutableList<String> = mutableListOf("全部", "待提交", "已提交")


    private val delayRun = Runnable {
        isLoadMore = false
        currentIndex = 1
        getBill(Utils.getEdit(etSearch), "9", currentIndex)
    }

    var currentIndex = 1
    private var touBaoBeans: MutableList<FenPei>? = ArrayList()
    var total = 0
    private var isLoadMore = false
    private fun initRefresh() {
        mRefreshLayout!!.setOnLoadMoreListener {
            if (total == 20) {
                isLoadMore = true
                currentIndex += 1
                getBill(Utils.getEdit(etSearch), currnet_status, currentIndex)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }


    }

    var currnet_status: String = "7"
    private fun getBill(input: String, statu: String, pageIndex: Int) {


        //状态：1新建，3已分配，5已查勘、7已立案、9已定损、

        var statu_ = when (statu) {
            "全部" ->
                "9"
            "待提交" ->
                "f7"
            "已提交" ->
                "f9"
            else ->
                "9"

        }
        currnet_status = statu_
        //     LogUtils.e("statu_  $statu  $statu_")
        RequestUtil.getInstance(this)!!.getBill(Config.GetBaoAn, statu_, input, 20, pageIndex, "养殖险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
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

    var mAdapter: BaseQuickAdapter<FenPei?, BaseViewHolder>? = null
    fun initAdapter() {
        rv!!.adapter = object : BaseQuickAdapter<FenPei?, BaseViewHolder>(R.layout.item_public) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: FenPei?) {

                val tv_first = holder.getView<TextView>(R.id.tv_name)
                val tv_second = holder.getView<TextView>(R.id.tv_insure_sum)
                val tv_third = holder.getView<TextView>(R.id.tv_make_deal_time)
                val tv_fourth = holder.getView<TextView>(R.id.tv_public_time)
                val tv_five = holder.getView<TextView>(R.id.tv_address)
                val check = holder.getView<TextView>(R.id.tv_statu)
                val fStatus = item?.status
                holder.setText(R.id.tv_insure, item!!.fItemDetailList)
                check.text = fStatus
               /* if (fStatus == "查勘定损") {
                    check.text = "公示"
                } else if (fStatus == "公示") {
                    check.text = "清单分享"
                } else {
                    check.text = "未知状态"
                }*/
                check.setOnClickListener { v: View? ->
                    if (!NetUtil.checkNet(mContext)) {
                        showStr("请在联网环境下操作")
                        return@setOnClickListener
                    }

                }
                tv_first.text = "投保人：" + item?.farmerName + " - " + item?.farmerNumber
                tv_second.text = item?.number
                tv_third.text = item?.baoAnDate
                tv_fourth.text = item?.FShowTimeBegin
                tv_five.text = item?.riskAddress
            }
        }.also { mAdapter = it }
    }


    fun setAdapter(fenPei: MutableList<FenPei?>, isLoadMore: Boolean) {

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