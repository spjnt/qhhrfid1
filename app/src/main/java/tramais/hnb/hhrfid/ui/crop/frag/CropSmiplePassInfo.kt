/*
package tramais.hnb.hhrfid.ui.crop.frag

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClient
import com.bin.david.form.core.SmartTable
import com.bin.david.form.data.table.MapTableData
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetHashMap
import tramais.hnb.hhrfid.interfaces.GetRtnMessage
import tramais.hnb.hhrfid.interfaces.GetitemBean
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.CropSmipleActivity
import tramais.hnb.hhrfid.ui.dialog.DialogEdit
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.*
import kotlin.collections.set


class CropSmiplePassInfo : BaseFragment() {
    private lateinit var mInputInfo: EditText
    private lateinit var mPlantJieduan: TextView
    private lateinit var mPlantingType: TextView

    private lateinit var mDisasterAreaUnit: TextView
    private lateinit var mEtDamage: EditText
    private lateinit var mDisasterArea: TextView
    private lateinit var mActualPlantingAreaUnit: TextView
    private lateinit var mEtActPlant: EditText
    private lateinit var mActualPlantingArea: TextView
    private lateinit var mChouyangDate: TextView
    private lateinit var mTvBaoanName: TextView
    private lateinit var mMethod: EditText
    private lateinit var mSave: Button
    private lateinit var mChouyangAdd: TextView
    private lateinit var mAddNew: TextView
    private lateinit var mSmartTable: SmartTable<Any>
    var mLocationClient: LocationClient? = null
    var cropChaKanBean: CropChaKanBean? = null

    override fun initImmersionBar() {

    }

    override fun findViewById(view: View?) {
        view?.let {
            mAddNew = it.findViewById(R.id.btn_add_new)
            mChouyangAdd = it.findViewById(R.id.chouyang_add)
            mTvBaoanName = it.findViewById(R.id.tv_baoan_name)
            mChouyangDate = it.findViewById(R.id.chouyang_date)
            mActualPlantingArea = it.findViewById(R.id.actual_planting_area)
            mEtActPlant = it.findViewById(R.id.et_act_plant)
            mActualPlantingAreaUnit = it.findViewById(R.id.actual_planting_area_unit)
            mDisasterArea = it.findViewById(R.id.disaster_area)
            mEtDamage = it.findViewById(R.id.et_damage)
            mDisasterAreaUnit = it.findViewById(R.id.disaster_area_unit)
            mPlantingType = it.findViewById(R.id.planting_type)
            mPlantJieduan = it.findViewById(R.id.plant_jieduan)
            mInputInfo = it.findViewById(R.id.input_info)
            mMethod = it.findViewById(R.id.chouyan_method)
            mSave = it.findViewById(R.id.only_save)
            mSmartTable = it.findViewById(R.id.smart_table)
            mChouyangDate.text = TimeUtil.getTime(Constants.yyyy_mm_dd)
            mPlantingType.text = "马铃薯"
            mLocationClient = LocationClient(context)
            BDLoactionUtil.initLoaction(mLocationClient)
            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                if (!TextUtils.isEmpty(add)) {
                    mChouyangAdd.text = add
                    mLocationClient!!.stop()
                }
            })


        }
    }

    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_samiple_pass
    }

    override fun initListener() {
        mChouyangDate.setOnClickListener { TimeUtil.initTimePicker(context) { mChouyangDate.text = it } }
        mSave.setOnClickListener { save() }
        mPlantJieduan.setOnClickListener { PopuChoice(activity, mPlantJieduan, "请选择生长阶段", growth) { str -> mPlantJieduan.text = str } }
        mAddNew.setOnClickListener {
            val dialogEdit = DialogEdit(requireContext(), "添加地块", titles) { notifySmart(titles, it) }
            if (!dialogEdit.isShowing) dialogEdit.show()
        }
    }

    override fun initData() {
        cropChaKanBean = CropSmipleActivity.cropChaKanBean
        getGrowStage(cropChaKanBean!!.fLandCategoryid)
        getGrowStage(cropChaKanBean!!.fLandCategoryid)
        getItem(cropChaKanBean!!.fLandCategoryid)
    }


    override fun onResume() {
        super.onResume()
        if (mLocationClient != null) mLocationClient!!.start()
        totalSize = arr.size
    }

    //    var datas: MutableList<SetLandBean> = ArrayList()
    var growth: MutableList<String> = ArrayList()
    fun getGrowStage(category: String) {
        RequestUtil.getInstance(context)!!.getLandGrowthStage(category) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
            if (datas != null && datas.size > 0) {
                val growthBean = GsonUtil.instant?.parseCommonUseArr(datas, GrowthStageBean::class.java)
                for (item in growthBean!!) {
                    val fStage = item.fStage
                    if (!TextUtils.isEmpty(fStage) && !growth.contains(fStage)) growth.add(fStage)
                }
                if (growth.isNotEmpty()) mPlantJieduan.text = growth[0]
            }
        }
    }

    var maps = HashMap<String, String>()
    fun getItem(category: String) {
        weiDuNames.clear()
        RequestUtil.getInstance(context)!!.getitem(category, object : GetitemBean {
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
RequestUtil.getInstance(context)!!.getitem("抽样维度", category) { rtnCode, message, totalNums, datas ->
            if (datas != null && datas.size > 0) {
                maps["1"] = "2"
                val parseCommonUseArr = GsonUtil.instant?.parseCommonUseArr(datas, ItemBean::class.java)
                for (item in parseCommonUseArr!!) {
                    val fItem = item.fItem
                    if (!TextUtils.isEmpty(fItem) && !weiDuNames.contains(fItem)) weiDuNames.add(fItem)
                }
            }
            titles.addAll(weiDuNames)
            val filter3 = titles.filter { it.contains("地块名称") }
            if (filter3.isEmpty()) titles.add(0, "地块名称")
            val filter = titles.filter { it.contains("抽样面积") }
            if (filter.isEmpty()) titles.add("抽样面积")

            val filter1 = titles.filter { it.contains("亩产") }
            if (filter1.isEmpty()) titles.add("亩产")

            val filter2 = titles.filter { it.contains("减产率") }
            if (filter2.isEmpty()) titles.add("减产率")


            //BA2020101000028  cropChaKanBean!!.fNumber

            getChouYang(titles, cropChaKanBean!!.fNumber)
        }


    }

    fun save() {
        val act_plant = Utils.getEdit(mEtActPlant)
        if (TextUtils.isEmpty(act_plant)) {
            "请输入实际种植面积".showStr()

            return
        }

        val damage_plant = Utils.getEdit(mEtDamage)
        if (TextUtils.isEmpty(damage_plant)) {
            "请输入受灾面积".showStr()

            return
        }

        RequestUtil.getInstance(context)!!.SaveChouYang(cropChaKanBean!!.fNumber, Utils.getText(mChouyangDate), act_plant, damage_plant, Utils.getText(mPlantingType), Utils.getText(mPlantJieduan), Utils.getEdit(mMethod), Utils.getEdit(mInputInfo), arr.toJSONString(), GetRtnMessage { rtnCode, message ->
            message.showStr()
        })
    }


    var weiDuNames: MutableList<String> = ArrayList()
    var titles: MutableList<String> = ArrayList()
    var rowids: MutableList<String> = ArrayList()

    private fun getChouYang(titles: MutableList<String>, baoanNumber: String) {
        rowids.clear()
  RequestUtil.getInstance(context)!!.getChouYang(baoanNumber) { rtnCode, message, totalNums, datas ->
            val parseCommonUseArr = GsonUtil.instant?.parseCommonUseArr(datas, ChouYangBean::class.java)
            for (item in parseCommonUseArr!!) {
                val fWeiDuName = item.fWeiDuName
                val fRowID = item.fRowID
                if (!TextUtils.isEmpty(fRowID) && !rowids.contains(fRowID)) rowids.add(fRowID)
            }
            // LogUtils.e("titles" + titles)
            setSmartTable(titles, weiDuNames, parseCommonUseArr)
        }

    }

    var arr = JSONArray()
    var totalSize = 0
    fun setSmartTable(titles: MutableList<String>, weiDuNames: MutableList<String>, chouYangs: MutableList<ChouYangBean>) {
        arr.clear()
        for (rowids in titles) {
            var obje = JSONObject(true)
            val filter = chouYangs.filter { it.fRowID.equals(rowids) }
            if (filter.isEmpty()) {
                for (rowids1 in titles) {
                    if (rowids1.contains("地块名称"))
                        obje[rowids1] = ""
                    else
                        obje[rowids1] = "0.0"
                }
            } else {
                obje["地块名称"] = filter.get(0).fLandName
                obje["抽样面积"] = filter.get(0).fLandArea
                for (chouyangitem in filter) {
                    obje[chouyangitem.fWeiDuName] = chouyangitem.fWeiDuValue
                }
                obje["亩产"] = filter.get(0).fActualQty
                obje["减产率"] = filter.get(0).fProductionPre
            }

            arr.add(obje)
        }
        var value_total: Double = 0.0
        var index = 0
        var obje_new = JSONObject(true)
        obje_new["地块名称"] = "受灾平均"
        for (item in titles) {
            if (!item.equals("地块名称"))
                if (arr != null && arr.size > 0) {
                    for (arrs in arr) {
                        index++
                        val jsonObject = arrs as JSONObject
                        val value = jsonObject.get(item)
                        if (value == null) {
                            value_total += 0
                        } else {
                            value_total += value.toString().toDouble()
                        }

                    }
                    obje_new[item] = Utils.formatDouble(value_total / index)
                    value_total = 0.0
                    index = 0
                } else {
                    obje_new[item] = 0
                }


        }
        arr.add(obje_new)
        totalSize = arr.size
        mSmartTable.config.isShowXSequence = false
        mSmartTable.config.isShowYSequence = false
        mSmartTable.config.isShowTableTitle = false
        val tableData = MapTableData.create("抽样", JsonHelper.jsonToMapList(arr.toJSONString()))
        mSmartTable.tableData = tableData
    }

    fun notifySmart(titles: MutableList<String>, weiDuNames: HashMap<String, String>) {
        if (weiDuNames == null || weiDuNames.size == 0) return
        var index = 0

        var obje = JSONObject(true)
        for (titles_ in titles) {
            val index1 = index++


            if (weiDuNames.containsKey(index1.toString())) {
                obje[titles_] = weiDuNames.get(index1.toString())
            } else {
                obje[titles_] = "0.0"
            }
        }

//        for (weiDuName in weiDuNames) {
//            val index1 = index++
//            if (index1 <= weiDuNames.size)
//                obje[titles.get(index1)] = weiDuName
//        }
        arr.removeAt(totalSize - 1)
        arr.add(totalSize - 1, obje)


        var value_total: Double = 0.0
        var obje_new = JSONObject(true)
        obje_new["地块名称"] = "受灾平均"
        for (item in titles) {
            if (!item.equals("地块名称"))
                if (arr != null && arr.size > 0) {
                    for (arrs in arr) {
                        index++
                        val jsonObject = arrs as JSONObject
                        val value = jsonObject.get(item)
                        if (value == null) {
                            value_total += 0
                        } else {
                            value_total += value.toString().toDouble()
                        }

                    }
                    obje_new[item] = Utils.formatDouble(value_total / index)
                    value_total = 0.0
                    index = 0
                } else {
                    obje_new[item] = 0
                }


        }
        totalSize = arr.size
        arr.add(obje_new)
        mSmartTable.config.isShowXSequence = false
        mSmartTable.config.isShowYSequence = false
        mSmartTable.config.isShowTableTitle = false
        val tableData = MapTableData.create("抽样", JsonHelper.jsonToMapList(arr.toJSONString()))
        mSmartTable.tableData = tableData

    }


}
*/
