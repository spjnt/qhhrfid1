package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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
import androidx.recyclerview.widget.DividerItemDecoration
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
import tramais.hnb.hhrfid.bean.TouBaoBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.BillListListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.Utils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class ActivityTouBaoList : BaseActivity() {
    var pages = 0
    private var mEtSearch: EditText? = null
    private var mLvContent: RecyclerView? = null
    private val mContext: Context = this@ActivityTouBaoList
    private var touBaoBeanList: MutableList<TouBaoBean?>? = null
    private var module_name: String? = null
    private var touBaoBeans: MutableList<TouBaoBean>? = ArrayList()
    private var currentPage = 1
    private var mRefreshLayout: SmartRefreshLayout? = null
    private val delayRun: Runnable? = Runnable {
        currentPage = 1
        isRefresh = false

        getBill(Utils.getEdit(mEtSearch), currentPage)
    }
    private val handler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UNDER_LIST -> {
                    touBaoBeanList = msg.obj as MutableList<TouBaoBean?>?
                    if (!isRefresh)
                        if (touBaoBeanList == null || touBaoBeanList!!.isEmpty()) {
                            showStr("暂无数据展示")
                            mAdapter!!.setList(null)
                            return
                        }
                    if (isRefresh) {
                        if (touBaoBeanList != null && touBaoBeanList!!.isNotEmpty())
                            touBaoBeanList?.let { mAdapter!!.addData(it) }
                    } else
                        touBaoBeanList?.let { mAdapter!!.setNewInstance(it) }
                    hideAvi()
                }
            }
        }
    }
    var mAdapter: BaseQuickAdapter<TouBaoBean?, BaseViewHolder>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_list)
    }

    override fun initView() {
        setTitleText("投保清单列表")
        setRightText("新增投保清单")
        mEtSearch = findViewById(R.id.et_search)
        mLvContent = findViewById(R.id.lv_content)
        mRefreshLayout = findViewById(R.id.refresh)
        if (!NetUtil.checkNet(this))
            mEtSearch!!.hint = "请输入投保户人姓名/投保单号"

        //  mLvContent!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mLvContent!!.layoutManager = LinearLayoutManager(this)

        setAdapter()
    }

    override fun initData() {
        mAdapter?.let { adapter ->
            if (!NetUtil.checkNet(this)) {
                netTips()
                return
            }
            //  netTips()
            adapter!!.setOnItemClickListener { adapter, view, position ->

                val item = adapter!!.getItem(position)
                if (TextUtils.isEmpty(module_name)) {
                    val intent = Intent(this@ActivityTouBaoList, ActivityUnderWriteDeal::class.java)
                    intent.putExtra("underWrite", item as Serializable)
                    intent.putExtra(Constants.Type, "from_toubaolist")
                    startActivity(intent)
                } else {
                    //数据是使用Intent返回
                    val intent = Intent()
                    //把返回数据存入Intent
                    intent.putExtra("underWrites", item as Serializable)
                    //设置返回数据
                    setResult(123, intent)
                    finish()
                }
            }
        }

        mEtSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (isResume) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //延迟600ms，如果不再输入字符，则执行该线程的run方法
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 200)
                }


            }
        })
        initRefresh()
    }

    override fun initListner() {
        mCustomTitle!!.mRootDelete!!.setOnClickListener { v: View? ->
            if (!NetUtil.checkNet(this)) {
                netTips()
                return@setOnClickListener
            }

            goToFarmList("制作投保清单")
        }
    }

    fun goToFarmList(module: String?) {
        val intent = Intent(this, ActivityFarmList::class.java)
        intent.putExtra(Constants.MODULE_NAME, module)
        startActivity(intent)
    }

    var isResume: Boolean = false
    override fun onResume() {
        super.onResume()
        isRefresh = false
        getBill(Utils.getEdit(mEtSearch), 1)
        isResume = true
    }

    private fun getBillCache(input: String?, pagIndex: Int) {
        var _toLine: MutableList<BillListListCache> = ArrayList()
        val strArr: Array<String>? = if (input.toString().isEmpty()) {
            null
        } else {
            arrayOf("farmer like ?  or number like ? ", "%$input%", "%$input%")
        }
        var fluentQuery: FluentQuery?


        if (strArr != null && strArr.isNotEmpty()) {
            fluentQuery = LitePal.where(*strArr)
        } else {
            fluentQuery = LitePal.where(null)
        }

        if (fluentQuery != null)
            if (pagIndex != 1) {
                fluentQuery = fluentQuery.order("creatTime desc").offset(20).limit(20)
            } else {
                fluentQuery = fluentQuery.order("creatTime desc").limit(20)
            }
        _toLine?.clear()
        fluentQuery.findAsync(BillListListCache::class.java).listen { list: List<BillListListCache>? ->

            if (list != null && list.isNotEmpty()) {
                total = list.size
                _toLine.addAll(list.toMutableList())

                cacheToLine(_toLine)

            } else {
                // mLvContent!!.adapter = null
                showStr("未查找到匹配数据")

            }
        }

    }

    private fun cacheToLine(list: List<BillListListCache>?) {
        touBaoBeans?.clear()
        if (list != null && list.isNotEmpty()) {
            for (item in list) {
                val bean = TouBaoBean()
                bean.category = item.category
                bean.companyNumber = item.companyNumber
                bean.creator = item.creator
                bean.date = item.date
                bean.farmer = item.farmer
                bean.number = item.number
                bean.status = item.status
                bean.sumAmount = item.sumAmount
                bean.area = item.area
                touBaoBeans!!.add(bean)
            }
        }
        val msg = Message()
        msg.obj = touBaoBeans
        msg.what = UNDER_LIST
        handler!!.sendMessage(msg)
    }

    private fun getBill(farmName: String, pageIndex: Int) {
        //  if (touBaoBeans != null) touBaoBeans!!.clear()
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this)!!.getBill(Config.GetBill, "1", farmName, 20, pageIndex, "")
            { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                total = totalNums
                if (datas != null && datas.size > 0)
                    touBaoBeans = instant!!.parseCommonUseArr(datas, TouBaoBean::class.java)
                else {
                    touBaoBeans!!.clear()
                    showStr(message)
                }

                // nextAndPer(totalNums, currentPage)
                val msg = Message()
                msg.obj = touBaoBeans
                msg.what = UNDER_LIST
                handler!!.sendMessage(msg)

            }
        } /*else {
            getBillCache(farmName, pageIndex)
        }*/
    }

    fun setAdapter() {
        mLvContent!!.adapter = object : BaseQuickAdapter<TouBaoBean?, BaseViewHolder>(R.layout.item_only_toubao) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: TouBaoBean?) {
                val tv_name = holder.getView<TextView>(R.id.tv_name)
                val tv_category_name = holder.getView<TextView>(R.id.tv_category_name)
                val tv_category = holder.getView<TextView>(R.id.tv_category)
                val tv_phone_num = holder.getView<TextView>(R.id.tv_phone_num)
                item?.let { farmer_ ->
                    holder.setText(R.id.tv_insure, item.fItemName)
                    tv_phone_num.text = farmer_.date
                    tv_category.text = farmer_.number
                    tv_name.text = farmer_.farmer + "--" + farmer_.farmerNumber
                    if (farmer_.area.isNullOrEmpty())
                        tv_category_name.visibility = View.GONE
                    else
                        tv_category_name.text = farmer_.area
                    tv_phone_num.setTextColor(mContext!!.getColor(R.color.black))
                    tv_phone_num.background = null
                    tv_category.setCompoundDrawables(setDrawableLeft(R.mipmap.item_order), null, null, null)
                    tv_phone_num.setCompoundDrawables(setDrawableLeft(R.mipmap.item_time), null, null, null)
                }
            }
        }.also { mAdapter = it }
    }


    fun setDrawableLeft(resId: Int): Drawable {
        val dwLeft: Drawable = resources.getDrawable(resId)
        dwLeft.setBounds(0, 0, dwLeft.minimumWidth, dwLeft.minimumHeight)
        return dwLeft
    }

    companion object {
        private const val UNDER_LIST = 1 shl 1
    }

    var total = 0
    var isRefresh: Boolean = false
    private fun initRefresh() {
        mRefreshLayout!!.setOnLoadMoreListener {

            currentPage += 1
            if (total == 20) {
                isRefresh = true
                getBill(Utils.getEdit(mEtSearch), currentPage)
                it.finishLoadMore()
            } else {
                isRefresh = false
                it.finishLoadMoreWithNoMoreData()
            }
        }
    }
}