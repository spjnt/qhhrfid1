package tramais.hnb.hhrfid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.bin.david.form.core.SmartTable
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseBackgroundFormat
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.PageTableData
import com.bin.david.form.data.table.TableData
import org.litepal.LitePal.findAllAsync
import org.litepal.LitePal.where
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.SearchByFarm
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils
import kotlin.math.ceil


class ActtivitySearchByFarmerOrTel : BaseActivity() {
    var animalSaveLists: MutableList<SearchByFarm>? = ArrayList()
    private var mEtSearch: AutoCompleteTextView? = null
    private var mBtnSearch: Button? = null
    private var mTvFarmId: TextView? = null
    private var mTvTotal: TextView? = null
    private var mPrePage: TextView? = null
    private var mPages: TextView? = null
    private var mNextPage: TextView? = null
    private var currentPage = 1
    private val context: Context = this@ActtivitySearchByFarmerOrTel
    private var animalSaveLists1: List<SearchByFarm>? = ArrayList()
    private var mIvStartTime: TextView? = null
    private var mIvEndTime: TextView? = null
    private var nameCache: MutableList<String?>? = null
    private var mSmartTable: SmartTable<SearchByFarm>? = null
    private var pageTableData: PageTableData<SearchByFarm>? = null
    private var pages = 0
    private val handler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                2 -> {
                    animalSaveLists1 = msg.obj as List<SearchByFarm>

                    //   mTvTotal!!.text = animalSaveLists1!!.size.toString() + ""
                    //nextAndPer(animalSaveLists1!!.size ?: 0, currentPage)
                    initSmartTable(animalSaveLists1)
                }
            }
        }
    }
    private var mLvContent: RecyclerView? = null
    private val delayRun: Runnable? = Runnable {
        getInfoLine(Utils.getEdit(mEtSearch), Utils.getText(mIvStartTime), Utils.getText(mIvEndTime), 1)
    }

    /*
     * 本地缓存信息
     * */
    private val search_list: MutableList<String?> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_farm_tel)
    }

    override fun initView() {
        setTitleText("投保人查询")
        //  setTitlTextColor(R.color.light_blue)
        mLvContent = findViewById(R.id.lv_content)
        mEtSearch = findViewById(R.id.et_search)
        mBtnSearch = findViewById(R.id.btn_search)
        mTvFarmId = findViewById(R.id.tv_farm_id)
        mTvTotal = findViewById(R.id.tv_total)
        mPrePage = findViewById(R.id.pre_page)
        mPages = findViewById(R.id.pages)
        mNextPage = findViewById(R.id.next_page)
        mIvStartTime = findViewById(R.id.iv_start_time)
        mIvEndTime = findViewById(R.id.iv_end_time)
        mIvStartTime!!.text = TimeUtil.getPastDate(3.toString() + "")
        mIvEndTime!!.text = TimeUtil.getTime(Constants.yyyy_mm_dd)
        if (!NetUtil.checkNet(context)) {
            nameCache = getNameCache()
            //  LogUtils.e("name $nameCache")
        }
        mSmartTable = findViewById(R.id.smart_table)
        mSmartTable!!.visibility = View.VISIBLE
        mLvContent!!.visibility = View.GONE
    }

    override fun initData() {
        val farmer_name = intent.getStringExtra(Constants.farmer_name)
        mTvFarmId!!.text = farmer_name
        mEtSearch!!.setText(farmer_name)
        if (NetUtil.checkNet(this))
            getInfoLine(intent.getStringExtra(Constants.farmer_id_nums)!!, Utils.getText(mIvStartTime), Utils.getText(mIvEndTime), 1)
        else {
            netTips()
        }
    }

    override fun initListner() {
        mPrePage!!.setOnClickListener { v: View? ->
            if (currentPage > 1) {
                currentPage--
                getInfoLine(Utils.getEdit(mEtSearch), Utils.getText(mIvStartTime), Utils.getText(mIvEndTime), currentPage)
            } else if (currentPage == 1) {
                showStr("已经是第一页")
            }
        }
        mNextPage!!.setOnClickListener { v: View? ->
            if (currentPage < pages) {
                currentPage++
                getInfoLine(Utils.getEdit(mEtSearch), Utils.getText(mIvStartTime), Utils.getText(mIvEndTime), currentPage)
            } else if (currentPage == pages) {
                showStr("已经是最后一页")
            }
        }
        mBtnSearch!!.setOnClickListener { view: View? ->
            getInfoLine(Utils.getEdit(mEtSearch), Utils.getText(mIvStartTime), Utils.getText(mIvEndTime), currentPage)
        }

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
            getInstance(context)!!.searchByFramNum(input, startTime, endTime, currentPage, "养殖险") { str: String? ->
                //   LogUtils.e("str  ${str}")
                if (!TextUtils.isEmpty(str)) {
                    val jsonObject = JSONObject.parseObject(str)
                    val jsonArray = jsonObject.getJSONArray("Data")
                    val farmerName = jsonObject.getString("FarmerName")
                    total = jsonObject.getInteger("Qty")
                    //    LogUtils.e("total  $total")
                    mTvTotal!!.text = total.toString()
                    nextAndPer(total, currentPage)
                    mTvFarmId!!.text = farmerName
                    if (jsonArray != null && jsonArray.size > 0) {
                        animalSaveLists = instant!!.parseCommonUseArr(jsonArray, SearchByFarm::class.java)
                        val message = Message()
                        message.obj = animalSaveLists
                        message.what = 2
                        handler!!.sendMessage(message)
                    } else {
                        showStr("暂无信息返回")
                    }
                } else {
                    showStr("暂无信息返回")
                }
            }
        } else {
            val farmListCaches: MutableList<AnimalSaveCache> = ArrayList()
            farmListCaches.clear()
            val condition_arr: Array<String>?
            val numeric = Utils.isNumeric(input)
            condition_arr = if (!numeric) {
                arrayOf("FarmName like ? and creatTime>= ?  and creatTime<= ? ", "%$input%", "$startTime 00:00:00", "$endTime 23:59:59")
            } else {
                arrayOf("Tel = ? and creatTime>= ?  and creatTime<= ? ", "%$input%", "$startTime 00:00:00", "$endTime 23:59:59")
            }
            where(*condition_arr).order("creatTime desc").findAsync(AnimalSaveCache::class.java).listen { list: List<AnimalSaveCache> ->
                LogUtils.e("list  ${list.size}")
                if (list.isNotEmpty()) {
                    total = list.size
                    farmListCaches.addAll(list)
                    mTvTotal!!.text = total.toString()
                    cacheToLine(farmListCaches)
                    nextAndPer(total, currentPage)
                } else {
                    showStr("未查找到匹配数据")
                }
            }
        }

    }

    private fun getNameCache(): MutableList<String?> {
        findAllAsync(AnimalSaveCache::class.java).listen { list: List<AnimalSaveCache> ->
            if (list.isNotEmpty()) {
                for (item in list) {
                    val lableNum = item.farmName
                    LogUtils.e("lableNum:$lableNum")
                    val tel = item.tel
                    if (!TextUtils.isEmpty(lableNum) && !search_list.contains(lableNum)) search_list.add(lableNum)
                    if (!TextUtils.isEmpty(tel) && !search_list.contains(tel)) search_list.add(tel)
                }
            }
        }
        return search_list
    }


    private fun nextAndPer(total: Int, index: Int) {
        pages = ceil((total * 1.0 / 20)).toInt()

        runOnUiThread {
            if (pages != 1) {
                when (index) {
                    1 -> {
                        mPrePage!!.setBackgroundResource(R.drawable.big_conner_gray)
                        mNextPage!!.setBackgroundResource(R.drawable.big_conner_red)
                    }
                    pages -> {
                        mPrePage!!.setBackgroundResource(R.drawable.big_conner_red)
                        mNextPage!!.setBackgroundResource(R.drawable.big_conner_gray)
                    }
                    else -> {
                        mPrePage!!.setBackgroundResource(R.drawable.big_conner_red)
                        mNextPage!!.setBackgroundResource(R.drawable.big_conner_red)
                    }
                }
            } else {
                mPrePage!!.setBackgroundResource(R.drawable.big_conner_gray)
                mNextPage!!.setBackgroundResource(R.drawable.big_conner_gray)
            }
            mPages!!.text = formatColor(index, pages)
        }
    }

    private fun cacheToLine(caches: List<AnimalSaveCache>?) {
        if (animalSaveLists != null) animalSaveLists!!.clear()
        if (caches != null && caches.isNotEmpty()) {
            val saveCache = caches[0]
            mTvFarmId!!.text = saveCache.farmName
            for (item in caches) {
                val list = SearchByFarm()
                list.labelNumber = item.lableNum
                list.category = item.animalType
                list.checkTime = item.creatTime
                list.status = "新建"
                list.name = item.farmName
                animalSaveLists!!.add(list)
            }
            val message = Message()
            message.obj = animalSaveLists
            message.what = 2
            handler!!.sendMessage(message)
        }
    }

    // var ranges: MutableList<CellRange> = ArrayList()
    private fun initSmartTable(underWrites: List<SearchByFarm>?) {
        if (underWrites == null || underWrites.isEmpty()) {
            showStr("暂无数据展示")
            mSmartTable!!.visibility = View.GONE
            mSmartTable!!.invalidate()
            mSmartTable!!.notifyDataChanged()
            return
        }
        //   耳标，姓名，险种，状态，验标时间
        mSmartTable!!.visibility = View.VISIBLE
        val labelnumber_column = Column<String>("耳标号", "LabelNumber")
        val name_column = Column<String>("姓名", "Name")
        val category_column = Column<String>("险种", "Category")
        // val status_column = Column<String>("状态", "Status")
        val checktime_column = Column<String>("验标时间", "CheckTime")
        if (NetUtil.checkNet(context)) {
            // ranges.add(CellRange(2, 2, 0, 2))

            val tableData =
                    TableData("", underWrites, labelnumber_column, name_column, category_column, checktime_column)
            //   tableData.userCellRange=ranges
            //   tableData.addCellRange(CellRange(2, 2, 0, 2))
            mSmartTable!!.setTableData(tableData)
        } else {
            pageTableData = PageTableData("", underWrites, labelnumber_column, name_column, category_column, checktime_column)
            pageTableData!!.pageSize = 20
            mSmartTable!!.setTableData(pageTableData)
        }
        initConfig1(mSmartTable!!)
        labelnumber_column.setOnColumnItemClickListener { column, value, s, position ->
            val labelNumber = underWrites[position].labelNumber
            val intent = Intent(context, ActivitySearchByEarTag::class.java)
            intent.putExtra(Constants.animal_lable, labelNumber)
            startActivity(intent)
        }
    }

    fun initConfig1(mSmartTmrData: SmartTable<SearchByFarm>) {

        mSmartTmrData.config.isShowTableTitle = false
        mSmartTmrData.config.isShowXSequence = false
        mSmartTmrData.config.isShowYSequence = false
        mSmartTmrData.config.verticalPadding = 20
        mSmartTmrData.config.horizontalPadding = 20
        mSmartTmrData.config.contentStyle = FontStyle(this, 16, resources.getColor(R.color.black))
        mSmartTmrData.config.columnTitleStyle = FontStyle(this, 16, resources.getColor(R.color.white))
        mSmartTmrData.config.isFixedYSequence = true
        mSmartTmrData.config.contentCellBackgroundFormat = object : BaseCellBackgroundFormat<CellInfo<*>?>() {
            override fun getBackGroundColor(t: CellInfo<*>?): Int {
                /*  if (t!!.col == 0) {
                      return ContextCompat.getColor(this@ActtivitySearchByFarmerOrTel, R.color.f08c792)
                  }*/
                return TableConfig.INVALID_COLOR
            }

            override fun getTextColor(t: CellInfo<*>?): Int {
                if (t!!.col == 0) {
                    return ContextCompat.getColor(this@ActtivitySearchByFarmerOrTel, R.color.light_blue)
                }
                return TableConfig.INVALID_COLOR
            }

        }
        mSmartTmrData.config.columnTitleBackground =
                BaseBackgroundFormat(resources.getColor(R.color.light_blue))

        val outSize = Point()
        windowManager.defaultDisplay.getRealSize(outSize)
        val x = outSize.x
        mSmartTmrData.config.minTableWidth = x
    }

}