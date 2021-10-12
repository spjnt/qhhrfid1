package tramais.hnb.hhrfid.ui.crop

import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bin.david.form.core.SmartTable
import com.bin.david.form.data.CellRange
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.MapTableData
import com.bin.david.form.data.table.TableData
import okhttp3.internal.notify
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetBool
import tramais.hnb.hhrfid.interfaces.GetChouYangBean
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetitemBean
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogCheckInfo
import tramais.hnb.hhrfid.ui.dialog.DialogDelete
import tramais.hnb.hhrfid.ui.dialog.DialogEdit
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.*
import java.text.DecimalFormat

class ActivityFieldSampling : BaseActivity() {

    private var mCheckInfo: TextView? = null
    private var mInputInfo: EditText? = null
    private var mEtDamage: EditText? = null
    private var mEtActPlant: EditText? = null
    private var mEtInputConclu: EditText? = null
    private var mChouyangDate: TextView? = null
    private var mMethod: EditText? = null
    private var mSave: Button? = null
    private var mChouyangAdd: EditText? = null
    private var mAddNew: TextView? = null
    private var mSmartTable: SmartTable<Any>? = null
    var mLocationClient: LocationClient? = null
    var cropChaKanBean: FenPei? = null


    override fun initListner() {
        mCheckInfo!!.setOnClickListener { DialogCheckInfo(this, "查勘信息", cropChaKanBean!!).show() }
        mChouyangDate!!.setOnClickListener { TimeUtil.initTimePicker(this) { mChouyangDate!!.text = it } }
        mSave!!.setOnClickListener { save() }
        mAddNew!!.setOnClickListener {
            val dialogEdit = DialogEdit(this, "添加地块", titles) { notifySmart(titles, it) }
            if (!dialogEdit.isShowing) dialogEdit.show()
        }
    }

    var sunShiQty: String? = null
    var shouZaiQty: String? = null
    override fun initData() {
        getItem(cropChaKanBean!!.FLandCategoryId)
        RequestUtil.getInstance(this)!!.getLandChaKanFidDetail(cropChaKanBean!!.number,
                "",
                object : GetCommon<FidDetail> {
                    override fun getCommon(t: FidDetail) {
                        t.let {
                            sunShiQty = it.fSunShiqty
                            shouZaiQty = it.fShouZaiqty
                        }

                    }
                })

    }


    override fun onResume() {
        super.onResume()
        if (mLocationClient != null) mLocationClient!!.start()
        totalSize = arr.size
    }


    fun getItem(category: String?) {
        titles.clear()
        RequestUtil.getInstance(this)!!.getitem(category, object : GetitemBean {
            override fun getItemBean(bean: ItemBean?) {
                if (bean!!.data != null && bean!!.data!!.isNotEmpty()) {
                    // titles.add("编辑")
                    for (item in bean!!.data!!) {
                        val fItem = item.fItem
                        if (!TextUtils.isEmpty(fItem) && !titles.contains(fItem) && fItem != "样点") titles.add(fItem.toString())
                    }
                }

                getChouYang(titles, cropChaKanBean!!.number!!)
            }

        })

    }

    fun save() {
        val act_plant = Utils.getEdit(mEtActPlant)
        if (TextUtils.isEmpty(act_plant)) {
            showStr("请输入实际种植面积")
            return
        }

        val damage_plant = Utils.getEdit(mEtDamage)
        if (TextUtils.isEmpty(damage_plant)) {
            showStr("请输入受灾面积")
            return
        }

        RequestUtil.getInstance(this)!!.SaveChouYang(cropChaKanBean!!.number, Utils.getText(mChouyangDate), act_plant, damage_plant,
                Utils.getEdit(mEtInputConclu), Utils.getText(mChouyangAdd), Utils.getEdit(mMethod), Utils.getEdit(mInputInfo), arr.toJSONString())
        { _, message ->
            showStr(message)
        }
    }


    //    var weiDuNames: MutableList<String> = ArrayList()
    var titles: MutableList<String> = ArrayList()
    var rowids: MutableList<String> = ArrayList()

    private fun getChouYang(titles: MutableList<String>, baoanNumber: String) {
        rowids.clear()
        RequestUtil.getInstance(this)!!.getChouYang(baoanNumber, object : GetCommon<ChouYangBean> {
            override fun getCommon(bean: ChouYangBean) {
                bean?.let {

                    if (it.data != null && it.data!!.isNotEmpty()) {
                        //  LogUtils.e("${it.data!!.size}")
                        for (item in it.data!!) {
                            val fRowID = item.fRowID
                            if (!TextUtils.isEmpty(fRowID) && !rowids.contains(fRowID)) rowids.add(fRowID.toString())
                        }
                        setSmartTable(titles, rowids, it.data as MutableList<ChouYangBean.DataDTO>)
                    } else {
                        showStr("暂无样点地块数据")
                    }
                    runOnUiThread {
                        // mChouyangAdd!!.text = it.fAddr
                        if (it.fAddr.isNullOrEmpty()) {
                            mLocationClient = LocationClient(this@ActivityFieldSampling)
                            BDLoactionUtil.initLoaction(mLocationClient)
                            mLocationClient!!.start()
                            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                                if (!TextUtils.isEmpty(add)) {
                                    mChouyangAdd!!.setText(add)
                                    mLocationClient!!.stop()
                                }
                            })
                        } else {
                            mChouyangAdd!!.setText(it.fAddr)
                        }
                        mChouyangDate!!.text = it.fSampleDate
                                ?: TimeUtil.getTime(Constants.yyyy_mm_dd)
                        mEtActPlant!!.setText(it.fAreaQty ?: sunShiQty)
                        mEtDamage!!.setText(it.fRiskQty ?: shouZaiQty)
                        mMethod!!.setText(it.fSampleType)
                        mInputInfo!!.setText(it.fRiskDetail)
                        mEtInputConclu!!.setText(it.fSampleResult)
                    }
                }

            }

        })
    }

    var mapTableData: MapTableData? = null
    var arr = JSONArray()
    var totalSize = 0
    fun setSmartTable(titles: MutableList<String>, rowids: MutableList<String>, chouYangs: MutableList<ChouYangBean.DataDTO>) {
        arr.clear()
        if (chouYangs == null || chouYangs.isEmpty()) return
        var index_ = 1
        for (row_item in rowids) {
            var obje = JSONObject(true)
            val filter_row = chouYangs.filter { it.fRowID == row_item }
            obje["编辑"] = "删除"
            obje["样点"] = index_++.toString()
            if (filter_row != null && filter_row.isNotEmpty()) {
                for (item_title in titles) {
                    when (item_title) {

                        "地块名称" -> obje[item_title] = filter_row[0].fLandName.toString()
                        "抽样面积(㎡)" -> obje[item_title] = filter_row[0].fLandArea.toString()
                        "实际亩产(kg/亩)" -> obje[item_title] = filter_row[0].fActualQty.toString()
                        "减产率(%)" -> obje[item_title] = filter_row[0].fProductionPre.toString()
                        else -> {
                            val filter_item = filter_row.filter { it.fWeiDuName == item_title }
                            if (filter_item != null && filter_item.isNotEmpty()) {

                                obje[item_title] = filter_item[0].fWeiDuValue.toString()

                            } else {
                                obje[item_title] = ""
                            }
                        }
                    }

                }

                arr.add(obje)
            } else {
                for (chouyangitem in titles) {
                    obje[chouyangitem] = ""
                }
                arr.add(obje)
            }


        }

        upDateTable(arr)
    }

    fun notifySmart(titles: MutableList<String>, weiDuNames: HashMap<String, String>) {
        if (weiDuNames == null || weiDuNames.size == 0) return

        if (!titles.contains("减产率(%)")) titles.add("减产率(%)")
        var obje = JSONObject(true)
        obje["编辑"] = "删除"
        for (title in titles) {

            if (totalSize != 0)
                obje["样点"] = (totalSize).toString()
            else {
                obje["样点"] = "1"
            }
            if (title != "减产率(%)") {
                if (title != "编辑") {
                    if (weiDuNames.containsKey(title)) {
                        obje[title] = weiDuNames[title]
                    } else {
                        obje[title] = "0.0"
                    }
                }
            } else {

                var liLun = if (weiDuNames["理论亩产(kg)"].isNullOrEmpty()) {
                    0.0
                } else {
                    if (Utils.isNumeric(weiDuNames["理论亩产(kg)"]))
                        weiDuNames["理论亩产(kg)"]!!.toDouble()
                    else 0.0
                }
                var shiJi = if (weiDuNames["实际亩产(kg/亩)"].isNullOrEmpty()) {
                    0.0
                } else {
                    if (Utils.isNumeric(weiDuNames["实际亩产(kg/亩)"]))
                        weiDuNames["实际亩产(kg/亩)"]!!.toDouble()
                    else 0.0
                }

                obje[title] = Utils.formatDouble(((liLun - shiJi) / liLun) * 100)
            }

        }

        if (totalSize != 0) {
            arr.removeAt(totalSize - 1)
            arr.add(totalSize - 1, obje)
        } else {
            arr.add(0, obje)
        }

        upDateTable(arr)

    }

    fun deleteItem(titles: MutableList<String>, arr_: JSONArray) {
        var index = 1
        var arr_new = JSONArray()
        arr_.removeLast()
        for (item in arr_) {
            val jsonObject = item as JSONObject
            var obje_new = JSONObject(true)
            obje_new["编辑"] = "删除"
            obje_new["样点"] = index++.toString()
            for (title_item in titles) {
                obje_new[title_item] = jsonObject[title_item]
            }
            arr_new.add(obje_new)
        }
        arr.clear()
        arr.addAll(arr_new)
        upDateTable(arr)
    }

    var ranges: MutableList<CellRange> = ArrayList()
    fun upDateTable(arr: JSONArray) {
        ranges.clear()
        var value_total = 0.0
        var liLun = 0.0
        var shiJi = 0.0
        var obje_new = JSONObject(true)
        obje_new["地块名称"] = "受灾平均"
        obje_new["样点"] = ""
        obje_new["编辑"] = ""
        var size = arr.size ?: 0
        for (item in titles) {
            if (item != "地块名称" && item != "样点" && item != "编辑") {
                if (size > 0) {
                    for (index in 0 until size) {
                        val jsonObject = arr[index] as JSONObject
                        val value = jsonObject[item] as String?
                        if (value.isNullOrEmpty()) {
                            value_total += 0
                        } else {
                            if (Utils.isNumeric(value.toString()))
                                value_total += value.toString().toDouble()
                            else value_total += 0
                        }
                        if (index == size - 1) {
                            if (item == "理论亩产(kg)")
                                liLun = value_total
                            if (item == "实际亩产(kg/亩)")
                                shiJi = value_total
                            if (item != "减产率(%)") {
                                obje_new[item] = Utils.formatDouble(value_total / size)
                            } else {
                                obje_new[item] = Utils.formatDouble(((liLun - shiJi) / liLun) * 100)
                            }
                            value_total = 0.0
                        }
                    }
                } else {
                    obje_new[item] = 0
                }
            }
        }

        totalSize = arr.size
        arr.add(obje_new)
        ranges.add(CellRange(arr.size - 1, arr.size - 1, 0, 2))
        mapTableData = MapTableData.create("抽样", JsonHelper.jsonToMapList(arr.toJSONString()))
//        val tableData = mapTableData as TableData<ChouYangBean.DataDTO>
//        tableData!!.userCellRange=ranges
        mSmartTable!!.tableData = mapTableData
        mSmartTable?.let { initConfig1(it) }
        mapTableData?.let {
            it.setOnItemClickListener { column, value, t, col, row ->
                if (col == 0 && row != arr.size - 1)
                    DialogDelete(this, "样点 ${row + 1}", object : GetBool {
                        override fun getBool(tf: Boolean) {
                            if (tf) {
                                arr.removeAt(row)
                                deleteItem(titles, arr)
                                // upDateTable(arr)
                            }
                        }
                    }).show()
            }
        }

    }

    fun initConfig1(mSmartTmrData: SmartTable<Any>) {
        mSmartTmrData.config.isShowTableTitle = false
        mSmartTmrData.config.isShowXSequence = false
        mSmartTmrData.config.isShowYSequence = false
        mSmartTmrData.config.verticalPadding = 20
        mSmartTmrData.config.horizontalPadding = 20
        mSmartTmrData.config.contentStyle = FontStyle(this, 12, resources.getColor(R.color.black))
        mSmartTmrData.config.columnTitleStyle = FontStyle(this, 12, resources.getColor(R.color.black))
        mSmartTmrData.config.isFixedYSequence = true
        mSmartTmrData.setZoom(true, 1f, 0.5f)
        val outSize = Point()
        windowManager.defaultDisplay.getRealSize(outSize)
        val x = outSize.x
        mSmartTmrData.config.minTableWidth = x

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_field_sample)
    }

    override fun initView() {
        cropChaKanBean = intent.getSerializableExtra("FenPei") as FenPei
        setTitleText("现场抽样")
        mAddNew = findViewById(R.id.btn_add_new)
        mChouyangAdd = findViewById(R.id.chouyang_add)
        mChouyangDate = findViewById(R.id.chouyang_date)
        mEtActPlant = findViewById(R.id.et_act_plant)
        mEtDamage = findViewById(R.id.et_damage)
        mInputInfo = findViewById(R.id.input_info)
        mMethod = findViewById(R.id.chouyan_method)
        mSave = findViewById(R.id.only_save)
        mSmartTable = findViewById(R.id.smart_table)
        mEtInputConclu = findViewById(R.id.input_conclusion)
        mCheckInfo = findViewById(R.id.check_info)
    }
}