package tramais.hnb.hhrfid.ui

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_farm_list.*
import kotlinx.android.synthetic.main.activity_under_write_deal.*
import kotlinx.android.synthetic.main.item_text.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.*
import tramais.hnb.hhrfid.listener.DetailLocationListener
import tramais.hnb.hhrfid.litePalBean.*
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogDelete
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ActivityUnderWriteDeal : BaseActivity() {
    var mLocationClient: LocationClient? = null
    private var mOrderNum: TextView? = null
    private var mInsureDate: TextView? = null
    private var mInsureType: TextView? = null
    private var mInsureStartDate: TextView? = null
    private var mInsureEndDate: TextView? = null
    private var mInsureMan: TextView? = null
    private var mCardType: TextView? = null
    private var mPublicDate: TextView? = null
    private var mCardNum: TextView? = null
    private var mCollectionName: TextView? = null
    private var mBreedNum: TextView? = null
    private var mInsureNum: TextView? = null
    private var mEarTags: TextView? = null
    private var mInsureMoney: TextView? = null
    private var mSaveInsure: Button? = null
    private var mStartTag: TextView? = null
    private var mEndTag: TextView? = null
    private var govMoney: TextView? = null
    private var selfMoney: TextView? = null
    private var mTotalMoney: TextView? = null
    private lateinit var mInsureTypeType: TextView
    private lateinit var mInsureAddress: TextView
    private lateinit var mAnimalType: TextView
    private var etAreaName: TextView? = null
    private var etAreaDetail: TextView? = null
    private var addressDeatail: EditText? = null
    private var ivChoice: ImageView? = null
    private var mIvMore: ImageView? = null
    private var mDeleteDeal: Button? = null
    private var mChange: Button? = null
    private var mSubmit: Button? = null
    private var mBottom: LinearLayout? = null
    private var moneyDetail: LinearLayout? = null
    private var dealTitle: TextView? = null
    private var nationalMoney: TextView? = null
    private var proviceMoney: TextView? = null
    private var cityMoney: TextView? = null
    private var counteyMoney: TextView? = null
    private var toSign: TextView? = null
    private var ivSign: ImageView? = null
    private var tvDept: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_under_write_deal)

    }

    private var cropTypeLists: MutableList<CropTypeList>? = ArrayList()
    private var crop_type: CropTypeList? = CropTypeList()
    private val insure_type: MutableList<String> = java.util.ArrayList()
    private var currentTime = ""
    override fun initView() {
        showRootSetting()
        setTitleText("制作投保清单")
        addressDeatail = findViewById(R.id.et_detail_address)
        etAreaDetail = findViewById(R.id.et_areaName_detail)
        tvDept = findViewById(R.id.insure_dept)
        dealTitle = findViewById(R.id.deal_title)
        mPublicDate = findViewById(R.id.public_date)
        mIvMore = findViewById(R.id.iv_more)
        currentTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
        mOrderNum = findViewById(R.id.order_num)
        mInsureDate = findViewById(R.id.insure_date)
        mInsureType = findViewById(R.id.insure_type)
        mInsureStartDate = findViewById(R.id.insure_start_date)
        mInsureEndDate = findViewById(R.id.insure_end_date)
        mInsureMan = findViewById(R.id.insure_man)
        mCardType = findViewById(R.id.card_type)
        mCardNum = findViewById(R.id.card_num)
        mCollectionName = findViewById(R.id.collection_name)
        mBreedNum = findViewById(R.id.breed_num)
        mInsureNum = findViewById(R.id.insure_num)
        mEarTags = findViewById(R.id.ear_tags)
        mInsureMoney = findViewById(R.id.insure_money)
        mSaveInsure = findViewById(R.id.save_insure)
        toSign = findViewById(R.id.to_sign)
        ivSign = findViewById(R.id.iv_sign)
        mStartTag = findViewById(R.id.start_tag)
        mEndTag = findViewById(R.id.end_tag)
        mTotalMoney = findViewById(R.id.total_money)

        mChange = findViewById(R.id.change)
        mInsureTypeType = findViewById(R.id.insure_type_type)
        mInsureAddress = findViewById<EditText>(R.id.insure_address)
        mBottom = findViewById(R.id.ll_bottom)
        mAnimalType = findViewById(R.id.animal_type)

        etAreaName = findViewById(R.id.et_areaName)
        ivChoice = findViewById(R.id.iv_choice)
        govMoney = findViewById(R.id.gov_money)
        selfMoney = findViewById(R.id.self_money)
        mSubmit = findViewById(R.id.submit)
        mDeleteDeal = findViewById(R.id.delete_deal)

        moneyDetail = findViewById(R.id.money_detail)
        nationalMoney = findViewById(R.id.national_money)
        proviceMoney = findViewById(R.id.provice_money)
        cityMoney = findViewById(R.id.city_money)
        counteyMoney = findViewById(R.id.country_money)


        val transition = LayoutTransition()
        moneyDetail!!.layoutTransition = transition
    }

    var ifChage_Click: Boolean = false

    fun setEnable(ifChange: Boolean) {
        toSign!!.isEnabled = ifChange
        mInsureDate!!.isEnabled = ifChange
        mInsureStartDate!!.isEnabled = ifChange
        mInsureEndDate!!.isEnabled = ifChange
        mPublicDate!!.isEnabled = ifChange
        mInsureTypeType.isEnabled = ifChange
        etAreaName!!.isEnabled = ifChange
        etAreaDetail!!.isEnabled = ifChange
        addressDeatail!!.isEnabled = ifChange
        // if (ifChange) dealTitle!!.text = "未上传耳标清单" else dealTitle!!.text = "已上传耳标清单"
    }

    private var dwbf: String? = null
    private var dwbe: String? = null
    private var bxfl: String? = null
    private var grdwbf: String? = null
    private var animals: MutableList<String> = java.util.ArrayList<String>()
    private var farmerNumber: String? = null
    private var category_: String? = null
    private var animal_type: String? = null
    private var farmer_area: String? = null


    private var areCode: String? = null
    var type: String? = null
    override fun initData() {
        ifChage_Click = false
        setEnable(false)
        val intent = intent
        if (intent != null) {
            type = intent.getStringExtra(Constants.Type)
            LogUtils.e("type  ${type}")
            val intent_list = intent.getSerializableExtra("underWrite")
            val number = intent.getStringExtra(Constants.Ba_num)
            when (type) {
                "from_pay",
                "from_public",
                -> {
                    setEnable(false)
                    mBottom!!.visibility = GONE

                    if (NetUtil.checkNet(this))
                        getBillDetail(number)

                }
                "from_farmerlist" -> {
                    setEnable(true)

                    mSaveInsure!!.visibility = VISIBLE
                    mSubmit!!.visibility = GONE
                    mChange!!.visibility = GONE
                    mDeleteDeal!!.visibility = GONE
                    if (intent_list != null) {
                        intent_list as FarmList
                        areCode = intent_list.areaCode
                        category_ = intent_list.category
                        farmerNumber = intent_list.zjNumber
                        farmer_area = intent_list.area
                        getCrop(Utils.getText(mAnimalType))
                        runOnUiThread {
                            mInsureDate!!.text = currentTime
                            mInsureStartDate!!.text = TimeUtil.getFeatDay(1)
                            mPublicDate!!.text = TimeUtil.getFeatDay(1)
                            mInsureEndDate!!.text = TimeUtil.getFeatureYearOrMonth(TimeUtil.getFeatDay(1), 1, Calendar.YEAR)
                            mInsureMan!!.text = intent_list.name
                            mCardType!!.text = intent_list.zjCategory
                            mCardNum!!.text = intent_list.zjNumber
                            mCollectionName!!.text = intent_list.area
                            mInsureType!!.text = intent_list.category
                            // etAreaDetail!!.text = intent_list.raiseAddress
                        }
                    }
                }

                "from_toubaolist" -> {
                    mSaveInsure!!.visibility = GONE
                    mSubmit!!.visibility = VISIBLE
                    mChange!!.visibility = VISIBLE
                    mDeleteDeal!!.visibility = VISIBLE
                    intent_list as TouBaoBean
                    areCode = intent_list.areaCode
                    if (NetUtil.checkNet(this)) {
                        farmerNumber = intent_list.farmerNumber
                        category_ = intent_list.category
                        getBillDetail(intent_list.number)

                    }
                }
            }
        }
        animals.clear()
        if (NetUtil.checkNet(this) && type == "from_farmerlist") {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(DetailLocationListener { lat: Double, log: Double, add: String? ->
                if (!add.isNullOrEmpty() && !add.contains("null")) {
                    mLocationClient!!.stop()
                    if (!add.contains("null")) {
                        addressDeatail!!.setText(add)
                    }
                }
            })
        }
        getDept()
        getAddress()
    }

    var dept_code: MutableMap<String?, String?> = HashMap()
    var depts: MutableList<String> = ArrayList()
    fun getDept() {
        depts.clear()
        RequestUtil.getInstance(this)!!.getCompanyDept(object : GetCommon<Dept> {
            override fun getCommon(t: Dept) {
                t.data.let {
                    for (item in it!!) {
                        val fCompanyDept = item.fCompanyDept
                        dept_code[fCompanyDept] = item.fCompanyDeptCode
                        if (!fCompanyDept.isNullOrEmpty() && !depts.contains(fCompanyDept))
                            depts.add(fCompanyDept)
                    }
                }
            }
        })
    }

    fun getLable(insure: String, areCode: String?, farmerNumber: String?, crop_type: CropTypeList) {
        RequestUtil.getInstance(this)!!.getLable(insure, areCode, farmerNumber, crop_type.fproductCode, crop_type.fCropCode, object : GetLbaleBean {
            override fun getLanleBean(lables: LableBean?) {
                shouTagDetail_?.clear()
                if (type == "from_farmerlist")
                    list_farmers_line.clear()
                buffer.delete(0, buffer.length)
                lables?.let {
                    val data = it.data
                    if (data != null && data.isNotEmpty()) {
                        for (item in data) {

                            val fFarmerNumber = item.fFarmerNumber
                            for (item_dto in item.data!!) {
                                val lable = item.fFarmerName + "-" + item_dto.fLabelNumber
                                buffer.append(item_dto.fLabelNumber + "~")
                                buffer.append("$fFarmerNumber|")
                                if (!shouTagDetail_!!.contains(lable))
                                    shouTagDetail_!!.add(lable)


                            }
                            if (type == "from_farmerlist") {
                                val detail = item.fFarmerName + "(" + item.fFarmerNumber + ")"
                                if (!list_farmers_line.contains(detail))
                                    list_farmers_line.add(detail)
                            }

                        }
                        showTag(shouTagDetail_)
                        showPrice(shouTagDetail_!!.size, crop_type)
                    } else {
                        showStr("暂无耳标数据")
                        mStartTag!!.text = ""
                        mEndTag!!.text = ""
                        mBreedNum!!.text = "0"
                        mInsureNum!!.text = "0"
                        showPrice(0, crop_type)
                    }


                }
            }
        })
    }

    fun getAddress() {
        RequestUtil.getInstance(this)!!.address(object : GetCommon<Address> {
            override fun getCommon(t: Address) {

                etAreaDetail!!.text = t.companyAddress
            }
        })
    }

    fun getCrop(animalType: String?) {
        //  LogUtils.e("animalType   $animalType")
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this)!!.getCrop("养殖险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                if (!datas.isNullOrEmpty()) {
                    cropTypeLists = GsonUtil.instant!!.parseCommonUseArr(datas, CropTypeList::class.java)
                    if (cropTypeLists != null && cropTypeLists!!.size > 0) {
                        for (item in cropTypeLists!!) {
                            if (item.fCropName != null) {
                                val s = item.fCropName + "~" + item.fproductCode + "~" + item.fCropCode + "~" + item.fClauseCode
                                if (!insure_type.contains(s)) insure_type.add(s)
                                if (animalType == s) {
                                    crop_type = item
                                }
                            }
                        }

                    }

                }

            }
        }
    }

    var friskrownum: String? = null


    var list_tags_line: MutableList<String> = ArrayList()
    var list_farmers_line: MutableList<String> = ArrayList()
    fun getBillDetail(number: String?) {
        list_tags_line.clear()
        ifChage_Click = true
        RequestUtil.getInstance(this)!!.getBillDetail(number) {
            if (it.code == 0) {
                farmer_area = it.area
                getCrop(it.riskType)
                val labels = it.labels
                for (item in labels!!.indices) {
                    val labelsDTO = labels[item]
                    val labelNumber = labelsDTO.farmerName + "-" + labelsDTO.labelNumber
                    if (!list_tags_line.contains(labelNumber))
                        list_tags_line.add(labelNumber)

                }
                val farmer = it.farmer
                var index = 1
                list_farmers_line?.clear()
                farmer?.let {
                    for (item in it) {
                        var detail = item.fname + "(" + item.fSfzNumber + ")"
                        if (!list_farmers_line.contains(detail))
                            list_farmers_line.add(detail)
                    }
                }

                runOnUiThread {
                    deptCode = it.FCompanyDeptCode
                    dept = it.FCompanyDept
                    tvDept!!.text = dept
                    mInsureDate!!.text = it.billDate
                    mInsureStartDate!!.text = it.beginDate
                    mInsureEndDate!!.text = it.endDate
                    mPublicDate!!.text = it.fShowTime
                    farmerNumber = it.sFZNumber
                    animal_type = it.riskType
                    mInsureTypeType!!.text = it.riskType
                    mOrderNum!!.text = it.billNumber
                    mInsureMan!!.text = it.farmerName
                    mCardType!!.text = it.zJCategory
                    mCardNum!!.text = farmerNumber
                    mCollectionName!!.text = it.area
                    mInsureType!!.text = it.category
                    mBreedNum!!.text = it.raiseQty.toString()
                    mInsureNum!!.text = it.insureQty.toString()
                    mStartTag!!.text = it.earStartNo
                    mEndTag!!.text = it.earEndNo
//                    tvRate!!.text = it.fRiskRate
                    mInsureMoney!!.text = it.unitAmount.toString()
                    mTotalMoney!!.text = it.sumAmount.toString()
                    govMoney!!.text = it.fSubsidies.toString()
                    selfMoney!!.text = it.fOwnAmount.toString()
                    etAreaDetail!!.text = it.FAddress1
                    addressDeatail!!.setText(it.FAddress2)
                    farmer_sign = it.signature
                    nationalMoney!!.text = it.fNationbdwf
                    proviceMoney!!.text = it.fProvincedwbf
                    cityMoney!!.text = it.fCitydwbf
                    counteyMoney!!.text = it.fCountydwbf
                    if (!farmer_sign.isNullOrEmpty()) Glide.with(this).load(farmer_sign).into(ivSign!!)
                }

            }
        }
    }


    //    var list_farmers: MutableList<String> = ArrayList()
    var tag_ = 0
    val buffer = StringBuffer()

    var shouTagDetail_: MutableList<String?>? = ArrayList()


    fun showTag(list_tags_: MutableList<String?>?) {
        runOnUiThread {
            if (list_tags_ != null && list_tags_.size > 0) {
                tag_ = list_tags_!!.size
                mStartTag!!.text = list_tags_[0]!!.split("-")[1]
                mEndTag!!.text = list_tags_[list_tags_.size - 1]!!.split("-")[1]
                mBreedNum!!.text = "${list_tags_.size}"
                mInsureNum!!.text = "${list_tags_.size}"
            } else {
                tag_ = 0
                mStartTag!!.text = ""
                mEndTag!!.text = ""
                mBreedNum!!.text = "0"
                mInsureNum!!.text = "0"
            }
        }
    }

    fun showPrice(animalNum: Int, crop_type: CropTypeList?) {
        //  if (animalNum == 0) return
        if (crop_type == null) return
        runOnUiThread {
            dwbf = crop_type?.dwbf ?: "0.00"
            dwbe = crop_type?.dwbe ?: "0.00"
            bxfl = crop_type?.bxfl ?: "0.00"
            grdwbf = crop_type?.grdwbf ?: "0.00"
            var nationbdwf = crop_type?.nationbdwf ?: "0.00"
            var provincedwbf = crop_type?.provincedwbf ?: "0.00"
            var citydwbf = crop_type?.citydwbf ?: "0.00"
            var countydwbf = crop_type?.countydwbf ?: "0.00"
            mInsureMoney!!.text = dwbe
            var total = (dwbe?.toDouble()?.times(animalNum) ?: 0.0) * bxfl?.toDouble()!!
            mTotalMoney!!.text = Utils.formatDouble(total)
            govMoney!!.text = Utils.formatDouble(total * (1 - grdwbf?.toDouble()!!))
            selfMoney!!.text = Utils.formatDouble(total * grdwbf?.toDouble()!!)

            nationalMoney!!.text = Utils.formatDouble(total * nationbdwf.toDouble()!!)
            proviceMoney!!.text = Utils.formatDouble(total * provincedwbf.toDouble()!!)
            cityMoney!!.text = Utils.formatDouble(total * citydwbf.toDouble()!!)
            counteyMoney!!.text = Utils.formatDouble(total * countydwbf.toDouble()!!)
        }
    }

    var isShowDetail: Boolean = false
    var farmer_sign: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.REQUEST_SING) {
                farmer_sign = data!!.getStringExtra(Constants.Sign_Path)
                if (!TextUtils.isEmpty(farmer_sign)) {
                    ivSign?.let { Glide.with(this).load(farmer_sign).into(it) }
                }
            }
        }
    }

    var deptCode: String? = null
    var dept: String? = null
    override fun initListner() {
        tvDept!!.setOnClickListener {
            PopuChoice(this, tvDept, "请选择归属单位", depts) {
                tvDept!!.text = it
                dept = it
                deptCode = dept_code[it]
            }
        }
        if (!ifNmg())
            govMoney!!.setOnClickListener {
                isShowDetail = !isShowDetail
                if (isShowDetail)
                    moneyDetail!!.visibility = VISIBLE
                else moneyDetail!!.visibility = GONE
            }
        mChange!!.setOnClickListener {
            setEnable(true)
            ifChage_Click = false
            mChange!!.visibility = GONE
            mSubmit!!.visibility = GONE
            mSaveInsure!!.visibility = VISIBLE
            getLable(Utils.getText(mOrderNum), areCode, farmerNumber, crop_type!!)

        }
        toSign!!.setOnClickListener {
            val intent = Intent(this, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "投保人签名")
            intent.putExtra(Constants.Sign_Common, "")
            intent.putExtra(Constants.Type, "养殖")
            startActivityForResult(intent, Config.REQUEST_SING)

        }
        mPublicDate!!.setOnClickListener {
            TimeUtil.initTimePicker(this, -1, -1) { str: String? -> mPublicDate!!.text = str }
        }

        mInsureDate!!.setOnClickListener {
            TimeUtil.initTimePicker(this, -1, -1) { str: String? -> mInsureDate!!.text = str }
        }
        mInsureStartDate!!.setOnClickListener {
            TimeUtil.initTimePicker(this, -1, -1) { str: String? ->
                mInsureStartDate!!.text = str
                mInsureEndDate!!.text = TimeUtil.getFeatureYearOrMonth(str, 1, Calendar.YEAR)
            }
        }
        mInsureEndDate!!.setOnClickListener {
            TimeUtil.initTimePicker(this, -1, -1) { str: String? -> mInsureEndDate!!.text = str }
        }
        mSaveInsure!!.setOnClickListener {
            netTips()
            saveBill()
        }
        mEarTags!!.setOnClickListener {
            // LogUtils.e("list_tags_line  $list_tags_line")
            if (ifChage_Click) {
                if (list_tags_line.isNullOrEmpty()) {
                    showStr("暂无耳标信息")
                    return@setOnClickListener
                }
                PopuChoice(this, mEarTags, "已上传耳标清单", list_tags_line) { }
            } else {
                if (shouTagDetail_.isNullOrEmpty()) {
                    showStr("暂无耳标信息")
                    return@setOnClickListener
                }
                PopuChoice(this, mEarTags, "未上传耳标清单", shouTagDetail_) { }
            }
        }
        mIvMore!!.setOnClickListener {
//            if (ifChage_Click) {
            if (list_farmers_line.isNullOrEmpty()) {
                showStr("暂无农户信息")
                return@setOnClickListener
            }
            PopuChoice(this, mIvMore, "农户信息,共 ${list_farmers_line.size} 户", list_farmers_line) {}


        }
        mInsureTypeType.setOnClickListener {
            PopuChoice(this, mInsureTypeType, "请选择险种", insure_type) { str: String? ->
                mInsureTypeType.text = str
                for (item in cropTypeLists!!) {
                    if (str.equals(item.fCropName + "~" + item.fproductCode + "~" + item.fCropCode + "~" + item.fClauseCode)) {
                        getLable(Utils.getText(mOrderNum), areCode, farmerNumber, item)
                    }
                }
            }
        }
        mSubmit!!.setOnClickListener {
            if (isSave)
                Utils.goToNextUI(ActivityAnimalLPPublic::class.java)
            else showStr("请先保存")
        }
        mDeleteDeal!!.setOnClickListener {
            val insure_number = Utils.getText(mOrderNum)
            if (insure_number.isNullOrEmpty()) {
                return@setOnClickListener
            }
            DialogDelete(this, "\n 保单号:$insure_number \n", object : GetBool {
                override fun getBool(tf: Boolean) {
                    if (tf) {
                        RequestUtil.getInstance(this@ActivityUnderWriteDeal)!!.DeleteBill(insure_number, object : GetResult {
                            override fun getResult(result: ResultBean?) {
                                showStr(result?.msg)
                                if (result!!.code >= 0)
                                    finish()
                            }
                        })
                    }
                }
            }).show()

        }
    }

    var isSave: Boolean = false
    private fun saveBill() {
        val breed_num = Utils.getText(mBreedNum)
        if (TextUtils.isEmpty(breed_num)) {
            showStr("请输入养殖数量")
            return
        }
        val insure_num = Utils.getText(mInsureNum)
        if (TextUtils.isEmpty(insure_num)) {
            showStr("请输入承保数量")
            return
        }
        if (Integer.valueOf(insure_num) > Integer.valueOf(breed_num)) {
            showStr("承保数量不得大于养殖数量")
            return
        }
        val insure_money = Utils.getText(mInsureMoney)
        if (insure_money.isNullOrEmpty()) {
            showStr("请输入单位保额")
            return
        }
        val total_money = Utils.getText(mTotalMoney)
        if (TextUtils.isEmpty(total_money)) {
            showStr("请输入保费金额")
            return
        }
        val tvDept = Utils.getText(tvDept)
        if (tvDept.isNullOrEmpty()) {
            showStr("请选择归属单位")
            return
        }
        val address = Utils.getText(etAreaDetail)
        if (TextUtils.isEmpty(address)) {
            showStr("请选择地址")
            return
        }

        val address_ = Utils.getText(addressDeatail)
        if (TextUtils.isEmpty(address_)) {
            showStr("请输入村镇地址")
            return
        }
        if (shouTagDetail_.isNullOrEmpty()) {
            showStr("请选择耳标清单")
            return
        }
        val time = TimeUtil.getTime(Constants.yyyy_mm_dd)
        val back_id: MutableMap<String, String?> = LinkedHashMap()
        back_id["签名"] = farmer_sign.toString()
        if (NetUtil.checkNet(this)) {
            showAvi()
            UpLoadFileUtil.upLoadFile(this, TimeUtil.getTime(Constants.yyyyMMddHHmmss1), back_id) { list: LinkedList<String>? ->
                RequestUtil.getInstance(this)!!.SaveBill(
                        Utils.getText(mOrderNum), currentTime, "", Utils.getText(mInsureStartDate), Utils.getText(mInsureEndDate),
                        Utils.getText(mCardNum), breed_num, insure_num, Utils.getText(mStartTag), Utils.getText(mEndTag), insure_money.toDouble(),
                        total_money.toDouble(), list!![0], userName, time, time, buffer.toString(), Utils.getText(mInsureTypeType), address, address_, Utils.getText(govMoney), Utils.getText(selfMoney),
                        Utils.getText(mPublicDate), friskrownum, "",
                        Utils.getText(nationalMoney), Utils.getText(proviceMoney), Utils.getText(cityMoney), Utils.getText(counteyMoney), deptCode, dept
                ) { rtnCode, message ->
                    if (rtnCode >= 0) {
                        isSave = true
                        mChange!!.visibility = VISIBLE
                        mSubmit!!.visibility = VISIBLE
                        mSaveInsure!!.visibility = GONE
                        setEnable(false)
                        mDeleteDeal!!.visibility = VISIBLE
                        runOnUiThread { mOrderNum!!.text = message }
                        for (item in shouTagDetail_!!) {
                            var saveCache = AnimalSaveCache()
                            saveCache.isMakeDeal = "1"
                            saveCache.updateAll("LableNum =?", item)
                        }
                    }
                    hideAvi()
                    showStr(message)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (mLocationClient != null) mLocationClient!!.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
    }
}