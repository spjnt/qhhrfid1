package tramais.hnb.hhrfid.ui.crop

import android.animation.LayoutTransition
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetCropChengBaoDetail
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.listener.DetailLocationListener
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivitySign
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.ui.popu.PopuDk
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.UpLoadFileUtil.upLoadFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CropMakeDealActivity : BaseActivity() {
    var mLocationClient: LocationClient? = null

    var farmer_sign: String? = null
    var pdf_address: String? = null
    private var billNo: TextView? = null
    private var insuranceDate: TextView? = null
    private var insuranceMethod: TextView? = null
    private var startingDateOfPublicity: TextView? = null
    private var startingDateOfInsurance: TextView? = null
    private var endingDateOfInsurance: TextView? = null
    private var typeOfInsurance: TextView? = null
    private var unitCoverage: TextView? = null
    private var premiumAmount: TextView? = null
    private var collectiveInsuranceConfirmationForm: Button? = null
    private var save: Button? = null
    private var submitForReview: Button? = null
    private var underwritingPublicity: Button? = null
    private var submitCoreSystem: Button? = null
    private var submitResult: TextView? = null

    //    private var tvInsureSum: TextView? = null
//    private var tvCheckSum: TextView? = null
//    private var dkSum: TextView? = null
    private var dkDetail: TextView? = null
    private var financialSubsidies: TextView? = null
    private var selfPaymentByFarmers: TextView? = null
    private val insure_type: MutableList<String>? = ArrayList()
    private val insure_method: MutableList<String> = ArrayList()
    private var cropTypeLists: List<CropTypeList>? = null
    private var lvLandDetail: ListView? = null

    private var cleanSign: Button? = null
    private var change: Button? = null

    private var linePathView: ImageView? = null
    private var address: TextView? = null
    private var address_detail: EditText? = null
    private val handler = Handler { msg ->
        when (msg.what) {
            PDF_SAVE_START -> showAvi()
            PDF_SAVE_RESULT -> {
                hideAvi()
                ShareUtils.shareWechatFriend(this@CropMakeDealActivity, pdf_address)
            }
        }
        false
    }
    private var landDetails: MutableList<LandDetail>? = ArrayList()
    private var insure_path_stream: String? = null
    private var insure_path = ""

    /*
     * 公示日期+7天，生成保险日期
     * 保险止期不得小于保险起期
     * */
    private var dwbe: String? = null
    private var crop_name: String? = null
    private var crop_code: String? = null
    private var farm_number = ""
    private var product_Code: String? = null
    private var mUri: Uri? = null
    private var cropTypeList_: CropTypeList? = null
    private var llBottom: LinearLayout? = null

    private var moneyDetail: LinearLayout? = null

    private var nationalMoney: TextView? = null
    private var proviceMoney: TextView? = null
    private var cityMoney: TextView? = null
    private var counteyMoney: TextView? = null
    private var tvDept: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_make_deal)

    }

    override fun initView() {
        setTitleText("农户承保")
        showRootSetting()
        tvDept = findViewById(R.id.insure_dept)
        llBottom = findViewById(R.id.ll_bottom)
        dkDetail = findViewById(R.id.dk_detail)
//        dkSum = findViewById(R.id.dk_sum)
//        tvInsureSum = findViewById(R.id.tv_insure_sum)
//        tvCheckSum = findViewById(R.id.tv_check_sum)
        billNo = findViewById(R.id.bill_No)
        insuranceDate = findViewById(R.id.insurance_date)
        insuranceMethod = findViewById(R.id.insurance_method)
        startingDateOfPublicity = findViewById(R.id.starting_date_of_publicity)
        startingDateOfInsurance = findViewById(R.id.starting_date_of_insurance)
        endingDateOfInsurance = findViewById(R.id.ending_date_of_insurance)
        typeOfInsurance = findViewById(R.id.type_of_insurance)
        unitCoverage = findViewById(R.id.unit_coverage)
        premiumAmount = findViewById(R.id.premium_amount)
        collectiveInsuranceConfirmationForm = findViewById(R.id.collective_insurance_confirmation_form) //集体投保确认单
        //  uploadConfirmationPhoto = (Button) findViewById(R.id.upload_confirmation_photo);
        //  confimImg = (ImageView) findViewById(R.id.confim_img);
        save = findViewById(R.id.save)
        submitForReview = findViewById(R.id.submit_for_review)
        underwritingPublicity = findViewById(R.id.underwriting_publicity)
        submitCoreSystem = findViewById(R.id.submit_core_system)
        submitResult = findViewById(R.id.submit_result)
        financialSubsidies = findViewById(R.id.financial_subsidies) //财政补贴
        selfPaymentByFarmers = findViewById(R.id.self_payment_by_farmers)  //农户自缴
        address = findViewById(R.id.address)
        address_detail = findViewById(R.id.et_detail_address)
        val current_time = TimeUtil.getTime(Constants.yyyy_mm_dd)
        insuranceDate!!.text = current_time
        startingDateOfPublicity!!.text = TimeUtil.getFeatDay(1)
        startingDateOfInsurance!!.text = TimeUtil.getFeatDay(8)
        endingDateOfInsurance!!.text = TimeUtil.getFeatureYearOrMonth(TimeUtil.getFeatDay(7), 6, Calendar.MONTH)
        lvLandDetail = findViewById(R.id.lv_land_detail)
        cleanSign = findViewById(R.id.to_sign)
        linePathView = findViewById(R.id.iv_sign)
        change = findViewById(R.id.change)



        moneyDetail = findViewById(R.id.money_detail)
        nationalMoney = findViewById(R.id.national_money)
        proviceMoney = findViewById(R.id.provice_money)
        cityMoney = findViewById(R.id.city_money)
        counteyMoney = findViewById(R.id.country_money)
        val transition = LayoutTransition()
        moneyDetail!!.layoutTransition = transition
        if (ifNmg())
            financialSubsidies!!.setCompoundDrawables(null, null, null, null)


    }

    private fun setEndable(isChange: Boolean) {
        insuranceDate!!.isEnabled = isChange
        startingDateOfInsurance!!.isEnabled = isChange
        startingDateOfPublicity!!.isEnabled = isChange
        endingDateOfInsurance!!.isEnabled = isChange
        typeOfInsurance!!.isEnabled = isChange
        cleanSign!!.isEnabled = isChange
        address!!.isEnabled = isChange
    }

    override fun onResume() {
        super.onResume()
        if (mLocationClient != null) mLocationClient!!.start()
    }

    override fun onPause() {
        super.onPause()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    var finumber = ""
    override fun initData() {
        insure_method.add("团体投保")
        insure_method.add("个人投保")
        val underWrites = intent.getSerializableExtra("underWrites")
        val type = intent.getStringExtra("type")
        LogUtils.e("typetype  $type")
        //  setEndable(false)
        if (!TextUtils.isEmpty(type))
            if (type == "Content") {
                setEndable(false)
                save!!.visibility = View.GONE
                submitForReview!!.visibility = View.GONE
                change!!.visibility = View.VISIBLE
                val farmList = underWrites as LandInsureBillList
                if (farmList != null) {
                    finumber = farmList.fNumber

                    farm_number = farmList.fFarmerNumber

                    getDetail(farmList.fNumber)

                }
            } else if (type == "list") {
                setEndable(false)
                llBottom!!.visibility = View.GONE
                val farmList = underWrites as LandInsureBillList
                finumber = farmList.fNumber

                farm_number = farmList.fFarmerNumber
                getDetail(farmList.fNumber)
            } else if (type == "Content1") {
                setEndable(true)
                save!!.visibility = View.VISIBLE
                submitForReview!!.visibility = View.VISIBLE
                change!!.visibility = View.GONE
                val farmList = underWrites as FarmList
                if (farmList != null) {
                    farm_number = farmList.zjNumber
                    //  insuranceMethod.setText(farmList.getCategory());
                }
            } else if (type == "farmerlist") {
                setEndable(true)
                save!!.visibility = View.VISIBLE
                submitForReview!!.visibility = View.VISIBLE
                change!!.visibility = View.GONE
            }
        getcrop()
        getDept()
        getAddress()
        if (NetUtil.checkNet(this) && (type == "farmerlist" || type == "Content1")) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(DetailLocationListener { lat: Double, log: Double, add: String? ->
                if (!TextUtils.isEmpty(add) && !add!!.contains("null")) {
                    mLocationClient!!.stop()
                    if (!add!!.contains("null")) {
                        address_detail?.setText(add)
                    }


                }
            })
        }
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

    private fun getcrop() {
        RequestUtil.getInstance(this)!!.getCrop("种植险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
            if (datas != null && datas.size > 0) {
                cropTypeLists = instant!!.parseCommonUseArr(datas, CropTypeList::class.java)
                cropTypeLists?.let {
                    for (item: CropTypeList in it) {
                        if (item.fCropName != null && !TextUtils.isEmpty(item.fCropName)) {
                            if (!insure_type!!.contains(item.fCropName)) insure_type.add(item.fCropName + "~" + item.fproductCode)
                        }
                    }
                    dwbe = it!!.get(0).dwbe
                    crop_name = it.get(0).fCropName
                    crop_code = it.get(0).fCropCode
                    product_Code = it.get(0).fproductCode
                    cropTypeList_ = it.get(0)
                    //  getLandDetail(farm_number, product_Code, crop_name, Utils.getText(insuranceMethod), cropTypeList_);
                }

            }
            if (insure_type != null && insure_type.size > 0) {
                //  typeOfInsurance.setText(insure_type.get(0));
            }
        }
    }

    private fun getDetail(fnum: String) {
        RequestUtil.getInstance(this)!!.getLandBillDetail(fnum, object : GetCropChengBaoDetail {
            override fun getBillDetail(chanKanDetailBean: CropCheckChengBaoBean?) {
                farmer_sign = chanKanDetailBean?.fSignature.toString()
                if (!TextUtils.isEmpty(farmer_sign)) linePathView?.let { Glide.with(this@CropMakeDealActivity).load(farmer_sign).into(it) }
                insure_path = chanKanDetailBean?.fSealPicture.toString()
                billNo!!.text = chanKanDetailBean?.fNumber
                startingDateOfPublicity!!.text = chanKanDetailBean?.fShowTime
                startingDateOfInsurance!!.text = chanKanDetailBean?.fBeginDate
                endingDateOfInsurance!!.text = chanKanDetailBean?.fEndDate
                typeOfInsurance!!.text = chanKanDetailBean?.fLandCategory + "~" + chanKanDetailBean?.fproductCode
                address!!.text = (chanKanDetailBean?.fAddress1)
                address_detail!!.setText(chanKanDetailBean?.fAddress2)
                unitCoverage!!.text = chanKanDetailBean?.fUnitAmount
                //    val fSubsidies = chanKanDetailBean?.fSubsidies
                // val fOwnAmount = chanKanDetailBean?.fOwnAmount
                financialSubsidies!!.text = chanKanDetailBean?.fSubsidies//补贴
                selfPaymentByFarmers!!.text = chanKanDetailBean?.fOwnAmount //农户自交
                premiumAmount!!.text = chanKanDetailBean?.fSumAmount
                //LogUtils.e(" chanKanDetailBean?.fOwnAmount"+ chanKanDetailBean?.fOwnAmount)
                nationalMoney!!.text = chanKanDetailBean?.fNationbdwf ?: "0.00"
                proviceMoney!!.text = chanKanDetailBean?.fProvincedwbf ?: "0.00"
                cityMoney!!.text = chanKanDetailBean?.fCitydwbf ?: "0.00"
                counteyMoney!!.text = chanKanDetailBean?.fCountydwbf ?: "0.00"
                tvDept!!.text = chanKanDetailBean?.fCompanyDept ?: ""
                dept = chanKanDetailBean?.fCompanyDept
                deptCode = chanKanDetailBean?.fCompanyDeptCode
                total_squre_ = (chanKanDetailBean?.sum_FSquare ?: "0.00").toDouble()
                check_squre_ = (chanKanDetailBean?.sum_FCheckSquare ?: "0.00").toDouble()
                val getAllLandList = chanKanDetailBean?.getAllLandList
                if (getAllLandList != null && getAllLandList.size > 0) {
                    for (item in getAllLandList) {
                        val landDetail = LandDetail()
                        landDetail.fArea = item.fArea
                        landDetail.fCheckSquare = item.fCheckSquare
                        landDetail.fCompanyNumber = item.fCompanyNumber
                        landDetail.fCropCode = item.fCropCode
                        landDetail.fCropName = item.fCropName
                        landDetail.fLandName = item.fLandName
                        landDetail.fLandNumber = item.fLandNumber
                        landDetail.fName = item.fName
                        landDetail.fRiskCategory = item.fRiskCategory
                        landDetail.fsfzNumber = item.fsfzNumber
                        landDetail.fSquare = item.fSquare
                        landDetail.block_count = item.block_count
                        landDetail.sum_FCheckSquare = item.sum_FCheckSquare
                        landDetail.sum_FSquare = item.sum_FSquare
                        //   LogUtils.e("sum_FSquare" + item.getSum_FCheckSquare() + " " + item.getSum_FSquare());
                        landDetails!!.add(landDetail)
                        total_squre_ += if (item.fSquare != null) item.fSquare.toDouble() else 0.0
                        check_squre_ += if (item.fCheckSquare != null) item.fCheckSquare.toDouble() else 0.0
                    }
                }
                //setLandDetailAdapter(landDetails)
            }
        })

    }

    private var total_squre_ = 0.0
    private var check_squre_ = 0.0
    private fun getLandDetail(farm_number: String, crop_code: String?, CropName: String?, InsureType: String, cropTypeList: CropTypeList?) {
        unitCoverage!!.text = cropTypeList!!.dwbe
        RequestUtil.getInstance(this)!!.getAllLandDetail(farm_number, crop_code, CropName, InsureType) { rtnCode1: Int, message1: String?, totalNums1: Int, datas1: JSONArray? ->
            var total_squre = 0.0
            var check_squre = 0.0
            landDetails!!.clear()
            if (datas1 != null && datas1.size > 0) {
                landDetails = instant!!.parseCommonUseArr(datas1, LandDetail::class.java)
                if (landDetails != null && landDetails!!.size > 0) {
                    for (item: LandDetail in landDetails!!) {
                        val fSquare = item.fSquare
                        if (!TextUtils.isEmpty(fSquare)) {
                            total_squre += if (Utils.isNumeric(fSquare)) {
                                fSquare.toDouble()
                            } else {
                                0.0
                            }
                        }
                        val fCheckSquare = item.fCheckSquare
                        if (!TextUtils.isEmpty(fCheckSquare)) {
                            check_squre += if (Utils.isNumeric(fCheckSquare)) {
                                fCheckSquare.toDouble()
                            } else {
                                0.0
                            }
                        }
                    }
                    check_squre_ = check_squre
                    total_squre_ = total_squre
                }

                if (cropTypeList != null) {
                    val dwbe = cropTypeList.dwbe
                    val grdwbf = cropTypeList.grdwbf
                    unitCoverage!!.text = dwbe + ""
                    var total = check_squre * prase(dwbe) * prase(cropTypeList.bxfl)
                    financialSubsidies!!.text = Utils.formatDouble(total * (1 - prase(grdwbf)))
                    selfPaymentByFarmers!!.text = Utils.formatDouble(total * prase(grdwbf))
                    premiumAmount!!.text = Utils.formatDouble(total)
                    nationalMoney!!.text = Utils.formatDouble(total * ((cropTypeList?.nationbdwf
                            ?: "0.00").toDouble()))
                    proviceMoney!!.text = Utils.formatDouble(total * ((cropTypeList?.provincedwbf
                            ?: "0.00").toDouble()))
                    cityMoney!!.text = Utils.formatDouble(total * ((cropTypeList?.citydwbf
                            ?: "0.00").toDouble()))
                    counteyMoney!!.text = Utils.formatDouble(total * ((cropTypeList?.countydwbf
                            ?: "0.00").toDouble()))
                }

            } else {
                unitCoverage!!.text = "0.00"
                var total = check_squre * prase(dwbe) * prase(cropTypeList.bxfl)
                financialSubsidies!!.text = "0.00"
                selfPaymentByFarmers!!.text = "0.00"
                premiumAmount!!.text = "0.00"
                nationalMoney!!.text = "0.00"
                proviceMoney!!.text = "0.00"
                cityMoney!!.text = "0.00"
                counteyMoney!!.text = "0.00"
            }
        }
    }

    private fun prase(source: String?): Double {
        return if (source.isNullOrEmpty()) {
            0.0
        } else {
            source.toDouble()
        }
    }

    fun String?.prase1() = if (this?.isEmpty() != false) 0.0 else toDouble()

    /* private fun setLandDetailAdapter(landDetails: List<LandDetail>?) {
         if (landDetails == null || landDetails.isEmpty()) return
         lvLandDetail!!.adapter = object : CommonAdapter<LandDetail?>(this, landDetails, R.layout.item_land_detail) {
             override fun convert(helper: ViewHolder, item: LandDetail?) {
                 val farmer_name = helper.getView<TextView>(R.id.farmer_name)
                 val farmer_dk = helper.getView<TextView>(R.id.dk)
                 val insure_dk = helper.getView<TextView>(R.id.insure_dk)
                 val check_dk = helper.getView<TextView>(R.id.check_dk)
                 farmer_name.text = item!!.fName
                 farmer_dk.text = setNumber(item.fLandNumber)
                 insure_dk.text = item.fSquare
                 check_dk.text = item.fCheckSquare
             }
         }
     }*/

    private fun setNumber(number: String): String {
        return if (TextUtils.isEmpty(number) || number.length <= 4) number else "****" + number.substring(number.length - 4, number.length)
    }

    var isShowDetail: Boolean = false
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
            financialSubsidies!!.setOnClickListener {
                isShowDetail = !isShowDetail
                if (isShowDetail)
                    moneyDetail!!.visibility = View.VISIBLE
                else moneyDetail!!.visibility = View.GONE
            }
        submitForReview!!.setOnClickListener { v: View? -> Utils.goToNextUI(CropInsureCheckPublic::class.java) }
        change!!.setOnClickListener { v: View? ->
            setEndable(true)
            change!!.visibility = View.GONE
            save!!.visibility = View.VISIBLE
            submitForReview!!.visibility = View.VISIBLE
        }
        dkDetail!!.setOnClickListener { v: View? ->
            if (landDetails == null || landDetails!!.size == 0) {
                showStr("暂无数据展示")
                return@setOnClickListener
            }
            PopuDk(this@CropMakeDealActivity, dkDetail, "查看地块详情", landDetails)
        }
        insuranceDate!!.setOnClickListener { v: View? ->
            TimeUtil.initTimePicker(this, -1, -1
            ) { str: String? -> insuranceDate!!.text = str }
        }
        startingDateOfPublicity!!.setOnClickListener { v: View? ->
            TimeUtil.initTimePicker(this, -1, -1
            ) { str: String? -> startingDateOfPublicity!!.text = str }
        }
        startingDateOfInsurance!!.setOnClickListener { v: View? ->
            TimeUtil.initTimePicker(this, -1, -1) { str ->
                startingDateOfInsurance!!.text = str
                //  endingDateOfInsurance.setText(TimeUtil.getSpecifiedDayFeature(str, 83));
            }
        }
        endingDateOfInsurance!!.setOnClickListener { v: View? ->
            TimeUtil.initTimePicker(this, -1, -1, object : GetOneString {
                override fun getString(str: String) {
                    endingDateOfInsurance!!.text = str
                }
            })
        }
        //集体投保确认单
        collectiveInsuranceConfirmationForm!!.setOnClickListener { v: View? ->
            if (TextUtils.isEmpty(Utils.getText(billNo))) {
                showStr("请先保存数据")
                return@setOnClickListener
            }
            //            ShareUtils.shareWechatFriend(this, "/storage/emulated/0/MyPdf/PDF_20201221_144447.pdf");
            //  getLandBillPDFDetail(finumber)
        }
        typeOfInsurance!!.setOnClickListener { v: View? ->
            PopuChoice(this, typeOfInsurance, "请选择险种", insure_type) { str: String ->
                typeOfInsurance!!.text = str
                if (str.contains("~")) {
                    val split: Array<String> = str.split("~").toTypedArray()
                    product_Code = split.get(1)
                    crop_name = split.get(0)
                    for (item in cropTypeLists!!) {
                        if ((item.fCropName == crop_name) && (item.fproductCode == product_Code)) {
                            cropTypeList_ = item
                            crop_code = item.fCropCode
                            getLandDetail(farm_number, product_Code, crop_name, Utils.getText(insuranceMethod), cropTypeList_)
                        }
                    }
                }
            }
        }
        //        insuranceMethod.setOnClickListener(v -> {
//            new PopuChoice(this, insuranceMethod, "请选择投保方式", insure_method, str -> {
//                insuranceMethod.setText(str);
//                //  getLandDetail(farm_number, product_Code, crop_name, str, cropTypeList_);
//            });
//        });
        cleanSign!!.setOnClickListener { v: View? ->
            val intent = Intent(this, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "投保人签名")
            intent.putExtra(Constants.Sign_Common, "")
            intent.putExtra(Constants.Type, "种植")
            startActivityForResult(intent, Config.REQUEST_SING)
        }
        save!!.setOnClickListener { v: View? -> saveLand() }
        //        confimImg.setOnClickListener(v -> {
//            takePhoto(TimeUtil.getTime(Constants.yyyyMMddHHmmss1), REQUEST_TAKE_CROP_INSURE);
//        });
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.REQUEST_SING) {
                farmer_sign = data!!.getStringExtra(Constants.Sign_Path)
                if (!TextUtils.isEmpty(farmer_sign)) {
                    linePathView?.let { Glide.with(this).load(farmer_sign).into(it) }
                }
            } else if (requestCode == REQUEST_TAKE_CROP_INSURE) {
                try {
                    val bitmapFormUri = ImageUtils.getBitmapFormUri(this, mUri)
                    insure_path = ImageUtils.saveBitmap(this, bitmapFormUri, FileUtil.getSDPath() + Constants.sdk_middle_path, "张三" + "/" + "张三" + "_" + "crop_grop" + ".jpg")
                    val getimage = ImageUtils.getimage(insure_path)
                    //   confimImg.setBackgroundResource(0);
                    //   confimImg.setImageBitmap(getimage);
                    insure_path_stream = ImageUtils.getStream(insure_path)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var isSave = false
    private fun saveLand() {
        showAvi()
        val back_id: MutableMap<String, String?> = LinkedHashMap()
        val tvDept = Utils.getText(tvDept)
        if (tvDept.isNullOrEmpty()) {
            showStr("请选择归属单位")
            return
        }
        val edit = Utils.getEdit(address_detail)
        if (edit.isNullOrEmpty()) {
            showStr("请输入村镇地址")
            return
        }
        back_id["种植险农户签名"] = farmer_sign.toString()
        back_id["集体投保确认盖章"] = insure_path.toString()
        upLoadFile(this, TimeUtil.getTime(Constants.yyyyMMddHHmmss1), back_id) { list: LinkedList<String>? ->
            var fa_sign: String? = ""
            var pic: String? = ""
            if (list != null && list.size > 0) for (item: String in list) {
                if (!TextUtils.isEmpty(item) && item.contains("tbds")) {
                    fa_sign = item
                } else {
                    pic = item
                }
            }
            RequestUtil.getInstance(this)!!.saveLandBill(
                    Utils.getText(billNo), Utils.getText(insuranceDate), farm_number, Utils.getText(startingDateOfInsurance),
                    Utils.getText(address), edit, Utils.getText(endingDateOfInsurance), total_squre_.toString() + "", check_squre_.toString() + "", Utils.getText(unitCoverage), Utils.getText(premiumAmount), crop_name, landDetails, crop_code.toString(),
                    fa_sign, Utils.getText(financialSubsidies), Utils.getText(selfPaymentByFarmers), pic, userName, userName, TimeUtil.getTime(Constants.yyyy_mm_dd), TimeUtil.getTime(Constants.yyyy_mm_dd), crop_code, product_Code, Utils.getText(startingDateOfPublicity), Utils.getText(insuranceMethod),
                    Utils.getText(nationalMoney), Utils.getText(proviceMoney), Utils.getText(cityMoney), Utils.getText(counteyMoney), deptCode, dept,
            ) { rtnCode, message ->
                if (rtnCode >= 0) {
                    isSave = true
                    setEndable(false)
                    billNo!!.text = message
                    submitForReview!!.visibility = View.VISIBLE
                    save!!.visibility = View.GONE
                    change!!.visibility = View.VISIBLE
                }
                showStr(message)
                hideAvi()
            }
        }
    }

    fun getAddress() {
        RequestUtil.getInstance(this)!!.address(object : GetCommon<Address> {
            override fun getCommon(t: Address) {

                address!!.text = t.companyAddress
            }
        })
    }

    private fun getLandBillPDFDetail(billNo: String) {
        RequestUtil.getInstance(this)!!.getLandBillPDFDetail(billNo, object : GetCommon<PDFDetailBean> {
            override fun getCommon(t: PDFDetailBean) {
                turnToPdf(t)
            }
        }
        )
    }

    private fun turnToPdf(pdfDetailBean: PDFDetailBean) {
        val ADDRESS = Environment.getExternalStorageDirectory().absolutePath + File.separator + "投保确认单"
        val file = File(ADDRESS)
        if (!file.exists()) file.mkdirs()
        pdf_address = (ADDRESS + File.separator + pdfDetailBean.fnumber + "_"
                + TimeUtil.getTime(Constants.yyyyMMddHHmmss) + ".pdf")
        val rectangle = Rectangle(PageSize.A4)
        rectangle.backgroundColor = BaseColor.WHITE
        rectangle.border = Rectangle.BOX
        rectangle.borderColor = BaseColor.LIGHT_GRAY
        rectangle.borderWidth = 5f
        val document = Document(rectangle)
        Thread {
            try {
                handler.sendEmptyMessage(PDF_SAVE_START)
                PdfWriter.getInstance(document, FileOutputStream(pdf_address))
                document.addTitle("标题")
                document.addAuthor("作者")
                document.addCreationDate()
                document.addCreator("创建者")
                document.open()
                //图片
//                val image = Image.getInstance(Config.PDF_LOGO_URL)
//                image.indentationLeft = 10f
//                document.add(image)

                //头部字体  String name, String encoding, boolean embedded
                val baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED)
                val font: Font = Font(baseFont, 18f, Font.BOLD)
                val fontsmall: Font = Font(baseFont, 12f, Font.NORMAL)
                val chunkFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, 12f, Font.UNDERLINE)

                //头
                val paragraph = Paragraph("种植业保险分户标的投保清单", font)
                paragraph.alignment = Element.ALIGN_CENTER
                document.add(paragraph)
                val paragraph1 = Paragraph()
                val phrase = Phrase("本分户得投保清单为  ", fontsmall)
                val chunk1 = Chunk(pdfDetailBean.fnumber, chunkFont)
                val phrase1 = Phrase("  号投保单得组成部分，投保人应如实，详细填写，并报纸自己清晰，纸面整洁。", fontsmall)
                paragraph1.add(phrase)
                paragraph1.add(chunk1)
                paragraph1.add(phrase1)
                document.add(paragraph1)
                //投保险种   标得名称
                val paragraph2 = Paragraph()
                val phrase2 = Phrase("投保险种：  ", fontsmall)
                val chunk2 = Chunk(pdfDetailBean.fLandCategory, chunkFont)
                val paragraph8 = Paragraph()
                val phrase3 = Phrase(200f, "标得名称：  ", fontsmall)
                val chunk3 = Chunk(pdfDetailBean.fLandCategory, chunkFont)
                phrase3.multipliedLeading = 200f
                paragraph2.add(phrase2)
                paragraph2.add(chunk2)
                paragraph8.add(phrase3)
                paragraph8.add(chunk3)
                document.add(paragraph2)
                document.add(paragraph8)


                // 标得种植地点   单位保费险额
                val paragraph3 = Paragraph()
                val phrase4 = Phrase(200f, "单位保费险额：  ", fontsmall)
                val chunk4 = Chunk(pdfDetailBean.dwbxje, chunkFont)
                val paragraph4 = Paragraph()
                val phrase5 = Phrase("标得种植地点：  ", fontsmall)
                val chunk5 = Chunk(pdfDetailBean.fLandAddress, chunkFont)
                paragraph3.add(phrase4)
                paragraph3.add(chunk4)
                paragraph4.add(phrase5)
                paragraph4.add(chunk5)
                document.add(paragraph3)
                document.add(paragraph4)


                // 标得种植地点   单位保费险额
                val paragraph5 = Paragraph()
                val phrase6 = Phrase("保费险率：  ", fontsmall)
                //String fontname, float size, int style
                val chunk6 = Chunk(pdfDetailBean.bxfl, chunkFont)
                val paragraph6 = Paragraph()
                val phrase7 = Phrase(200f, "单位保费：  ", fontsmall)
                val chunk7 = Chunk(pdfDetailBean.bxfl, chunkFont)
                paragraph5.add(phrase6)
                paragraph5.add(chunk6)
                paragraph6.add(phrase7)
                paragraph6.add(chunk7)
                document.add(paragraph5)
                document.add(paragraph6)
                //加空行
                document.add(Paragraph(" ", fontsmall))
                val table = PdfPTable(9)
                table.addCell(Phrase("序号", fontsmall))
                table.addCell(Phrase("农户身份证号码", fontsmall))
                table.addCell(Phrase("农户姓名", fontsmall))
                table.addCell(Phrase("保险数量(亩/株)", fontsmall))
                table.addCell(Phrase("农户联系方式", fontsmall))
                table.addCell(Phrase("银行卡号", fontsmall))
                table.addCell(Phrase("农户开户行", fontsmall))
                table.addCell(Phrase("总保险费", fontsmall))
                table.addCell(Phrase("农户自交保险费(元)", fontsmall))
                var index = 1
                val data = pdfDetailBean.data
                for (item in data) {
                    table.addCell(index++.toString())
                    table.addCell(item.fFarmerNumber)
                    table.addCell(item.fname)
                    table.addCell(item.fSquare)
                    table.addCell(item.fContactWay)
                    table.addCell(item.fBankAccount)
                    table.addCell(item.fBankName)
                    table.addCell(item.zbxf)
                    table.addCell(item.farmerBX)
                }
                table.horizontalAlignment = Element.ALIGN_LEFT
                document.add(table)
                document.add(Paragraph("填写说明:同一份清单应填写相同类型保险标得，相同种植地点(如同村)," +
                        "相同保险金额,相同保险费率得分户标得信息，否则应分开填写。", fontsmall))
                //制表人
                val paragraph_creater = Paragraph()
                val paragraph_creater1 = Phrase("制表人：  ", fontsmall)
                val chunk_creater = Chunk(pdfDetailBean.fCreator, chunkFont)
                paragraph_creater.add(paragraph_creater1)
                paragraph_creater.add(chunk_creater)
                document.add(paragraph_creater)


                //制表时间
                val paragraph_creater_time = Paragraph()
                val creater_time = Phrase("联系电话：  ", fontsmall)
                val time = Chunk(pdfDetailBean.fmobile, chunkFont)
                paragraph_creater_time.add(creater_time)
                paragraph_creater_time.add(time)
                document.add(paragraph_creater_time)
                document.close()
                handler.sendEmptyMessage(PDF_SAVE_RESULT)
            } catch (e: DocumentException) {
                Log.e("DocumentException", e.message.toString())
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                Log.e("FileNot", e.message.toString())
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e("IOException", e.message.toString())
                e.printStackTrace()
            }
        }.start()
    }

    companion object {
        private const val PDF_SAVE_START = 1 shl 19
        private const val PDF_SAVE_RESULT = 1 shl 20
        private const val REQUEST_TAKE_CROP_INSURE = 1 shl 1
    }
}