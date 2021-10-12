package tramais.hnb.hhrfid.ui.crop

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.alibaba.fastjson.JSONObject
import com.bin.david.form.core.SmartTable
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.CropSearch
import tramais.hnb.hhrfid.bean.SearchByFarm
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.lv.CropDelegateMultiAdapter
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils
import java.util.*
import kotlin.collections.ArrayList


class ActtivityCropSearchBy : BaseActivity() {
    var animalSaveLists: MutableList<SearchByFarm>? = ArrayList()
    private var mEtSearch: AutoCompleteTextView? = null
    private var mBtnSearch: Button? = null
    private var mTvFarmId: TextView? = null
    private var mTvTotal: TextView? = null
    private var mLvContent: RecyclerView? = null
    private var mUnit: TextView? = null
    private var currentPage = 1
    private val context: Context = this@ActtivityCropSearchBy
    private var mSmartTable: SmartTable<Any>? = null
    private var mIvStartTime: TextView? = null
    private var mIvEndTime: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_farm_tel)
    }

    private var adapter: CropDelegateMultiAdapter? = null
    override fun initView() {
        setTitleText("投保人查询")
        mSmartTable = findViewById(R.id.smart_table)
        mUnit = findViewById(R.id.age_unit)
        mUnit!!.text = "块"
        mEtSearch = findViewById(R.id.et_search)
        mBtnSearch = findViewById(R.id.btn_search)
        mTvFarmId = findViewById(R.id.tv_farm_id)
        mTvTotal = findViewById(R.id.tv_total)
//        mPrePage = findViewById(R.id.pre_page)
//        mPages = findViewById(R.id.pages)
//        mNextPage = findViewById(R.id.next_page)
        mIvStartTime = findViewById(R.id.iv_start_time)
        mIvEndTime = findViewById(R.id.iv_end_time)
        mIvStartTime!!.text = TimeUtil.getPastDate(3.toString() + "")
        mIvEndTime!!.text = TimeUtil.getTime(Constants.yyyy_mm_dd)

        mSmartTable!!.visibility = View.GONE

        mLvContent = findViewById(R.id.lv_content)
        if (!NetUtil.checkNet(this))
            mEtSearch!!.hint = "请输入投保户人姓名/手机号码查询"
        adapter = CropDelegateMultiAdapter(this)

        mLvContent!!.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        mLvContent!!.layoutManager = LinearLayoutManager(this)
        mLvContent!!.adapter = adapter

    }


    override fun initData() {
        val farmer_name = intent.getStringExtra(Constants.farmer_name)
        mTvFarmId!!.text = farmer_name
        if (NetUtil.checkNet(this))
            getInfoLine(intent.getStringExtra(Constants.farmer_id_nums)!!, Utils.getText(mIvStartTime), Utils.getText(mIvEndTime), 1)
        else {
            netTips()
        }

    }

    override fun initListner() {


        mIvStartTime!!.setOnClickListener { view: View? ->
            TimeUtil.initTimePicker(this, 5, 0) { str: String? ->
                mIvStartTime!!.text = str
                getInfoLine(Utils.getEdit(mEtSearch), str!!, Utils.getText(mIvEndTime), currentPage)
            }
        }
        mIvEndTime!!.setOnClickListener { view: View? ->
            TimeUtil.initTimePicker(this, 5, 0) { str: String? ->
                mIvEndTime!!.text = str
                getInfoLine(Utils.getEdit(mEtSearch), Utils.getText(mIvStartTime), str!!, currentPage)

            }
        }
    }

    /*
     *获取网络请求信息
     * */
    var total = 0
    private fun getInfoLine(input: String, startTime: String, endTime: String, currentPage: Int) {
        if (TextUtils.isEmpty(input)) {
            showStr("请输入投保人姓名或电话")
            return
        }
        val timeCompareSize = TimeUtil.getTimeCompareSize(startTime, endTime)
        if (!timeCompareSize) {
            showStr("结束时间不得早于开始时间")
            return
        }
        if (animalSaveLists != null) animalSaveLists!!.clear()
        if (NetUtil.checkNet(this)) {
            getInstance(context)!!.searchByFramNum(input, startTime, endTime, currentPage, "种植险") { str: String? ->

                if (str!!.isNotEmpty() && str.isNotBlank()) {
                    val cropCheck = JSONObject.parseObject(str, CropSearch::class.java)
                    mTvTotal!!.text = cropCheck.qty.toString()
                    adapter!!.setList(cropCheck.data)
                }
            }
        }

    }


}