package tramais.hnb.hhrfid.ui.crop

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.LandInsureBillList
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivityFarmList
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class CropBaoAnChoiceActivity : BaseActivity() {
    var currentPage = 1
    var total = 0
    private var mEtSearch: EditText? = null
    private val mRegistNew: Button? = null
    private var mLvContent: RecyclerView? = null
    private val context: Context = this@CropBaoAnChoiceActivity
    private val mBtnSearch: Button? = null
    private var underWrites: List<LandInsureBillList>? = null
    private val handler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UNDER_LIST -> {
                    /* underWrites = msg.obj as List<LandInsureBillList>
                     if (NetUtil.checkNet(this@CropBaoAnChoiceActivity)) {
                         setUnderAdapter(underWrites)
                     }*/
                    hideAvi()
                }
            }
        }
    }
    private var so: String? = null
    private var mRefreshLayout: SmartRefreshLayout? = null

    /**
     * 在上拉刷新的时候，判断，是否处于上拉刷新，如果是的话，就禁止在一次刷新，保障在数据加载完成之前
     * 避免重复和多次加载
     */
    private var isLoadMore = false
    private val delayRun: Runnable? = Runnable {
        isLoadMore = false
        getUnderList(Utils.getEdit(mEtSearch), 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_list)
    }

    override fun initView() {
        setTitleText("已生成投保单列表")
        mEtSearch = findViewById<View>(R.id.et_search) as EditText
        mLvContent = findViewById(R.id.lv_content)
        mRefreshLayout = findViewById(R.id.refresh)
        setRightText("新增投保单")
        // mLvContent!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mLvContent!!.layoutManager = LinearLayoutManager(this)
        initAdapter()
    }

    override fun initData() {
        so = intent.getStringExtra(Constants.SO_WHAT)
        mEtSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                isLoadMore = false
                if (NetUtil.checkNet(context)) {
                    if (isResume) {
                        if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                        //延迟600ms，如果不再输入字符，则执行该线程的run方法
                        if (handler != null && delayRun != null) handler.postDelayed(delayRun, 100)
                    }

                }
            }
        })
        initRefresh()
    }

    var mAdapter: BaseQuickAdapter<LandInsureBillList, BaseViewHolder>? = null
    fun initAdapter() {
        mLvContent!!.adapter = object : BaseQuickAdapter<LandInsureBillList, BaseViewHolder>(R.layout.item_chengbao_common_two) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: LandInsureBillList) {
                item?.let { farmer_ ->
                    holder.setText(R.id.tv_name, "农户：" + farmer_.fname + "--" + farmer_.fFarmerNumber)
                    holder.setText(R.id.tv_category_name, farmer_.fLandCategory)
                    holder.setText(R.id.tv_category, farmer_.fNumber)

                    var time = holder.getView<TextView>(R.id.tv_phone_num)
                    time.setTextColor(Color.BLACK)
                    time.setCompoundDrawables(setDrawableLeft(R.mipmap.item_time), null, null, null)
                    time.text = farmer_.fCreateTime
                }


            }
        }.also { mAdapter = it }
    }

    fun setDrawableLeft(resId: Int): Drawable {
        val dwLeft: Drawable = resources.getDrawable(resId)
        dwLeft.setBounds(0, 0, dwLeft.minimumWidth, dwLeft.minimumHeight)
        return dwLeft
    }

    var isResume: Boolean = false
    override fun onResume() {
        super.onResume()
        isLoadMore = false
        mEtSearch!!.setText("")
        if (NetUtil.checkNet(this)) {

            getUnderList("", 1)
        }
        isResume = true
    }

    override fun initListner() {
        mCustomTitle!!.mRootDelete.setOnClickListener { v: View? ->
            val intent = Intent(context, ActivityFarmList::class.java)
            intent.putExtra(Constants.MODULE_NAME, "种植险")
            startActivity(intent)
        }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->

                val landInsureBillList = it.getItem(position) as LandInsureBillList
                if (!TextUtils.isEmpty(so) && so == "ForResult") {
                    //数据是使用Intent返回
                    val intent = Intent()
                    //把返回数据存入Intent
                    intent.putExtra("underWrites", landInsureBillList)
                    //设置返回数据
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val intent = Intent(this, CropMakeDealActivity::class.java)
                    intent.putExtra("underWrites", landInsureBillList)
                    intent.putExtra("type", "Content")
                    startActivity(intent)
                }
            }


        }
    }

    var landInsureBillLists: MutableList<LandInsureBillList>? = ArrayList()
    private fun getUnderList(input: String, PageIndex: Int) {
        showAvi()
        val flag = ""
        RequestUtil.getInstance(this)!!.getLandInsureBillList("1", input, 20, PageIndex) { rtnCode, message, totalNums, datas ->
            total = totalNums
            landInsureBillLists = instant!!.parseCommonUseArr(datas, LandInsureBillList::class.java)

            setUnderAdapter(landInsureBillLists)
            hideAvi()

        }
    }

    private fun setUnderAdapter(underWrites: MutableList<LandInsureBillList>?) {
        if (!isLoadMore)
            if (underWrites == null || underWrites.isEmpty()) {
                showStr("暂无数据展示")
                mAdapter!!.setList(null)
                return
            }
        if (isLoadMore) {
            if (underWrites != null && underWrites.isNotEmpty())
                mAdapter!!.addData(underWrites)

        } else mAdapter!!.setNewInstance(underWrites)
    }

    private fun initRefresh() {

        mRefreshLayout!!.setOnLoadMoreListener {

            if (total == 20) {
                currentPage += 1
                isLoadMore = true
                getUnderList(Utils.getEdit(mEtSearch), currentPage)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }
    }

    companion object {
        private const val UNDER_LIST = 1 shl 1
    }
}