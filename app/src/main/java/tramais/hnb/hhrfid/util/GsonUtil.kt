package tramais.hnb.hhrfid.util

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.bean.CropCheckChengBaoBean.GetAllLandListDTO
import tramais.hnb.hhrfid.bean.ProductDetailBean.Data1DTO
import tramais.hnb.hhrfid.bean.ProductDetailBean.Data2DTO
import tramais.hnb.hhrfid.interfaces.GetResultJsonArarry
import tramais.hnb.hhrfid.interfaces.GetResultJsonObject
import java.util.*

class GsonUtil {
    fun praseAllMessage(str: String?, key: String, getResultJsonObject: GetResultJsonObject?) {
        if (TextUtils.isEmpty(str)) return
        try {
            val jsonObject = JSONObject.parseObject(str)
            val rtnCode = jsonObject.getInteger("Code")
            val msg = jsonObject.getString("Msg")
            var rowCount = 0
            if (jsonObject.containsKey("RowCount")) {
                rowCount = jsonObject.getInteger("RowCount")
            }
            var data: JSONObject? = null
            data = jsonObject.getJSONObject(key)

            getResultJsonObject!!.getResult(rtnCode, msg, rowCount, data)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun praseAllMessage(str: String?, getResultJsonObject: GetResultJsonObject) {
        praseAllMessage(str, "Data", getResultJsonObject)
    }


    fun praseAllMessageArray(str: String?, key: String?, getResultJsonArarry: GetResultJsonArarry) {
        if (TextUtils.isEmpty(str)) return
        val jsonObject = JSONObject.parseObject(str)
        val rtnCode = jsonObject.getInteger("Code")
        val msg = jsonObject.getString("Msg")
        var rowCount = 0
        if (jsonObject.containsKey("RowCount")) {
            rowCount = jsonObject.getInteger("RowCount")
        }
        val jsonArray = jsonObject.getJSONArray(key)
        getResultJsonArarry.getResult(rtnCode, msg, rowCount, jsonArray)
    }

    fun praseAllMessageArray(str: String?, getResultJsonArarry: GetResultJsonArarry) {
        praseAllMessageArray(str, "Data", getResultJsonArarry)
    }


    fun <T> parseCommonUseArr(data: JSONArray?, cls: Class<T>?): MutableList<T>? {
        //  JSONObject.parseObject(data,cls)
        return JSONObject.parseArray(JSONObject.toJSONString(data), cls)
    }




/*    fun praseInsureBillDetail(source: String?): InsureBillDetail? {
        if (TextUtils.isEmpty(source)) return null
        val jsonObject = JSONObject.parseObject(source)
        InsureBillDetail().run {
            Code = jsonObject.getInteger("Code")
            Msg = jsonObject.getString("Msg")
            accountNo = jsonObject.getString("accountNo")
            bank = jsonObject.getString("bank")
            endDate = jsonObject.getString("endDate")
            identifyNumber = jsonObject.getString("identifyNumber")
            identifyType = jsonObject.getString("identifyType")
            insuredAddress = jsonObject.getString("insuredAddress")
            insuredName = jsonObject.getString("insuredName")
            itemCode = jsonObject.getString("itemCode")
            itemName = jsonObject.getString("itemName")
            mobilePhone = jsonObject.getString("mobilePhone")
            policyNo = jsonObject.getString("policyNo")
            quantity = jsonObject.getString("quantity")
            riskCode = jsonObject.getString("riskCode")
            startDate = jsonObject.getString("startDate")
            amount = jsonObject
                    .getString("amount")
            return this
        }

        *//*   val bean = BillDetailBean()
           val jsonObject = JSONObject.parseObject(source)
           bean.area = jsonObject.getString("Area")
           bean.code = jsonObject.getInteger("Code")
           bean.msg = jsonObject.getString("Msg")
           bean.billNumber = jsonObject.getString("BillNumber")
           bean.billDate = jsonObject.getString("BillDate")
           bean.status = jsonObject.getString("Status")
           bean.category = jsonObject.getString("Category")
           bean.beginDate = jsonObject.getString("BeginDate")
           bean.endDate = jsonObject.getString("EndDate")
           bean.labelAddress = jsonObject.getString("LabelAddress")
           bean.riskType = jsonObject.getString("RiskType")
           bean.farmerNumber = jsonObject.getString("FarmerNumber")
           bean.farmerName = jsonObject.getString("FarmerName")
           bean.zJCategory = jsonObject.getString("ZJCategory")
           bean.sFZNumber = jsonObject.getString("SFZNumber")
           bean.fNationbdwf = jsonObject.getString("FNationbdwf")
           bean.fProvincedwbf = jsonObject.getString("FProvincedwbf")
           bean.fCitydwbf = jsonObject.getString("FCitydwbf")
           bean.fCountydwbf = jsonObject.getString("FCountydwbf")
           bean.fRiskRate = jsonObject.getString("FRiskRate")
           if (jsonObject.containsKey("RaiseQty")) bean.raiseQty = jsonObject.getInteger("RaiseQty")
           if (jsonObject.containsKey("InsureQty")) bean.insureQty = jsonObject.getInteger("InsureQty")
           bean.earStartNo = jsonObject.getString("EarStartNo")
           bean.earEndNo = jsonObject.getString("EarEndNo")
           if (jsonObject.containsKey("UnitAmount")) bean.unitAmount = jsonObject.getDouble("UnitAmount")
           if (jsonObject.containsKey("SumAmount")) bean.sumAmount = jsonObject.getDouble("SumAmount")
           if (jsonObject.containsKey("FSubsidies")) bean.fSubsidies = jsonObject.getDouble("FSubsidies")
           if (jsonObject.containsKey("FOwnAmount")) bean.fOwnAmount = jsonObject.getDouble("FOwnAmount")
           bean.signature = jsonObject.getString("Signature")

           val labels = jsonObject.getJSONArray("Labels")
           val lables: MutableList<BillDetailBean.LabelsDTO> = ArrayList()
           if (labels != null && labels.size > 0) {
               for (i in labels.indices) {
                   val labelsBean = BillDetailBean.LabelsDTO()
                   val o = labels[i] as JSONObject
                   val labelNumber = o.getString("LabelNumber")
                   labelsBean.labelNumber = labelNumber
                   lables.add(labelsBean)
               }
           }
           bean.labels = lables


           val farmers = jsonObject.getJSONArray("Farmer")
           val farmers_: MutableList<BillDetailBean.FarmerDTO> = ArrayList()
           if (farmers != null && farmers.size > 0) {
               for (i in farmers.indices) {
                   val farmerBean = BillDetailBean.FarmerDTO()
                   val o = farmers[i] as JSONObject
                   val Fname = o.getString("Fname")
                   val FSfzNumber = o.getString("FSfzNumber")
                   farmerBean.fname= Fname
                   farmerBean.fSfzNumber = FSfzNumber
                   farmers_.add(farmerBean)
               }
           }
           bean.farmer = farmers_
           return bean*//*
    }*/



    fun getRegion(source: String?): Region? {
        if (TextUtils.isEmpty(source)) return null
        val region = Region()
        val jsonObject = JSONObject.parseObject(source)
        region.code = jsonObject.getInteger("Code")
        region.msg = jsonObject.getString("Msg")
        region.fProvince = jsonObject.getString("FProvince")
        region.fCity = jsonObject.getString("FCity")
        region.fCounty = jsonObject.getString("FCounty")
        region.fNumber = jsonObject.getString("FNumber")
        val data = jsonObject.getJSONArray("Data")
        val data_: MutableList<Region.DataBean> = ArrayList()
        if (data != null && data.size > 0) {
            for (i in data.indices) {
                val o = data[i] as JSONObject
                val dataBean = Region.DataBean()
                dataBean.fTown = o.getString("FTown")
                dataBean.fNumber_GY_SA = o.getString("FNumber_GY_SA")
                dataBean.fVillage = o.getString("FVillage")
                dataBean.fRegionNumber = o.getString("FRegionNumber")
                if (dataBean != null) data_.add(dataBean)
            }
        }
        if (data_ != null && data_.size > 0) region.data = data_
        return region
    }



    companion object {
        var gsonUtil: GsonUtil? = null

        @JvmStatic
        val instant: GsonUtil?
            get() {
                if (gsonUtil == null) gsonUtil = GsonUtil()
                return gsonUtil
            }
    }
}