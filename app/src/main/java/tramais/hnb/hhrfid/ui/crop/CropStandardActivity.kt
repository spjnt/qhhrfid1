package tramais.hnb.hhrfid.ui.crop

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.GsonUtils
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.CropTypeList
import tramais.hnb.hhrfid.bean.DkInfoDetail
import tramais.hnb.hhrfid.bean.DkInfoDetail.GisdkDTO
import tramais.hnb.hhrfid.bean.ParmNew
import tramais.hnb.hhrfid.bean.Region
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceRegion
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils

/*
* 验标
* */
class CropStandardActivity : BaseActivity() {
    private val mContext: Activity = this@CropStandardActivity
    private var etAreaName: TextView? = null
    private var etAreaDetail: TextView? = null
    private var ivChoice: ImageView? = null
    private var etNf: TextView? = null
    private var tvLogin: Button? = null
    private val areaCode: String? = null
    private val area: String? = null
    private var data: List<Region.DataBean>? = null
    private var dkbm: String? = null
    private var ybmj: Double? = null
    private var dkzc = 0.0
    private var bdmc: String? = null
    private var bdmccode: String? = null
    private var bxsl = 0.0
    private var gisdk: String? = null
    private var qszh: String? = null
    private var qspiclist: String? = null
    private var xczp: String? = null
    private var ltzp: String? = null
    private var signpiclist: String? = null
    private var fypiclist: String? = null
    private var qyxz: String? = null
    private var qyxzCode: String? = null
    private var dkmj = 0.0
    private var zjhm: String? = null
    private var fhbbxr: String? = null
    private var fRegionNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_standard)
    }

    override fun initView() {
        setTitleText("承保验标")
        etAreaDetail = findViewById(R.id.et_areaName_detail)
        etAreaName = findViewById(R.id.et_areaName)
        etNf = findViewById(R.id.et_nf)
        tvLogin = findViewById(R.id.only_save_farm)
        ivChoice = findViewById<View>(R.id.iv_choice) as ImageView
    }

    private var cropTypeLists: List<CropTypeList>? = ArrayList()
    private val insure_type: MutableList<String>? = ArrayList()
    override fun initData() {
        getInstance(this)!!.getRegion(object : GetCommon<Region> {
            override fun getCommon(t: Region) {
                if (t.code >= 0) {
                    data = t.data
                    etAreaName!!.text = t.fProvince + t.fCity + t.fCounty
                }else{
                    showStr(t.msg)
                }
            }
        })
        getInstance(this)!!.getCrop("种植险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
            if (datas != null && datas.size > 0) {
                cropTypeLists = instant!!.parseCommonUseArr(datas, CropTypeList::class.java)
                if (cropTypeLists != null && cropTypeLists!!.isNotEmpty()) {
                    for (item: CropTypeList in cropTypeLists!!) {
                        if (item.fCropName != null && !TextUtils.isEmpty(item.fCropName)) {
                            if (!insure_type!!.contains(item.fCropName + "~" + item.fproductCode)) insure_type.add(item.fCropName + "~" + item.fproductCode)
                        }
                    }
                }
            }
            if (insure_type != null && insure_type.size > 0) etNf!!.text = insure_type.get(0)
        }
    }

    private var address: String? = null
    override fun initListner() {
        etNf!!.setOnClickListener { v: View? -> PopuChoice(this@CropStandardActivity, etNf, "请选择", insure_type, { str: String? -> etNf!!.text = str }) }
        ivChoice!!.setOnClickListener { v: View? ->
            if (data == null || data!!.isEmpty()) return@setOnClickListener
            val dialogChoiceRegion: DialogChoiceRegion = DialogChoiceRegion(this, data) { billNumber, message ->

                if (billNumber == "数据异常") {
                    showStr(billNumber)
                    return@DialogChoiceRegion
                }
                address = billNumber + message
                etAreaDetail!!.text = billNumber + message
                for (item: Region.DataBean in data!!) {
                    if (((item.fTown + item.fVillage) == address)) {
                        fRegionNumber = item.fNumber_GY_SA
                    }
                }
            }
            if (dialogChoiceRegion != null && !isFinishing) dialogChoiceRegion.show()
        }
        tvLogin!!.setOnClickListener { v: View? ->
            if (!NetUtil.checkNet(this)) {
                showStr("请在联网环境下操作")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(address)) {
                showStr("请选择地址")
                return@setOnClickListener
            }
            val text = Utils.getText(etNf)
            if (TextUtils.isEmpty(text)) {
                showStr("请选择标")
                return@setOnClickListener
            }
            val intent = Intent()
            intent.putExtra("key", Config.KEY) //国源提供的key
            intent.putExtra("secret", Config.SECRET) //国源提供的secret
            val paramIn = ParmNew()
            paramIn.areaCode = fRegionNumber //必填 县代码
            paramIn.areaName = address //必填 区划名
            paramIn.module = "验标"
            paramIn.zwdm = text.split("~").toTypedArray().get(1)
            paramIn.zwmc = text.split("~").toTypedArray().get(0)
            LogUtils.e("ParamIn" + GsonUtils.toJson(paramIn))
            intent.putExtra("ParamIn", GsonUtils.toJson(paramIn))
            val cn = ComponentName(mContext, "com.grandtech.standard.MainActivity") //YourActivity（全类名）
            intent.component = cn
            startActivityForResult(intent, 1024)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) { //获取系统照片上传
            if (requestCode == 1024 && data != null) {
                val paramout = data.getStringExtra("paramout")
                showAvi()

                getInstance(this)!!.getGuoYuan(paramout) { str: String? ->
                    val jsonObject = JSONObject.parseObject(str)
                    LogUtils.e("jsonObject  $jsonObject")
                    if (jsonObject.containsKey("data")) {

                        val data1 = jsonObject.getJSONObject("data")
                        if (data1 != null) {
                            // showAvi();
                            val values: Collection<Any> = data1.values
                            for (item: Any in values) {
                                val dkInfoDetail = JSON.parseObject(item.toString(), DkInfoDetail::class.java)
                                zjhm = dkInfoDetail?.zjhm ?: ""
                                fhbbxr = dkInfoDetail?.fhbbxr ?: ""
                                bdmc = dkInfoDetail?.bdmc ?: ""//作物名称
                                //作物code
                                bdmccode = dkInfoDetail?.bdmccode ?: ""
                                //验标面积
                                bxsl = dkInfoDetail?.bxsl ?: 0.0
                                //地块信息
                                signpiclist = dkInfoDetail?.signpiclist ?: ""
                                //权属证号
                                qszh = dkInfoDetail?.qszh ?: ""
                                qspiclist = dkInfoDetail?.qspiclist ?: ""
                                fypiclist = dkInfoDetail?.fypiclist ?: ""
                                qyxz = dkInfoDetail.qyxz ?: ""
                                qyxzCode = dkInfoDetail.qyxzCode ?: ""
                                val sfzjPicList = dkInfoDetail?.sfzjPicList ?: ""
                                val yhkPicList = dkInfoDetail?.yhkPicList ?: ""
                                var zjPic: String? = null
                                var zjBackPic: String? = null
                                var yhkPic: String? = null
                                var yhkBackPic: String? = null
                                if (!TextUtils.isEmpty(sfzjPicList) && (sfzjPicList != null) && (sfzjPicList.length > 2)) {
                                    val sfz_str = sfzjPicList.substring(1, sfzjPicList.length - 1)
                                    if (sfz_str.contains(",")) {
                                        val split = sfz_str.split(",").toTypedArray()
                                        zjPic = split[0].substring(1, split[0].length - 1)
                                        zjBackPic = split[1].substring(1, split[1].length - 1)
                                    } else {
                                        zjPic = sfz_str.substring(1, sfz_str.length - 1)
                                    }
                                }
                                if (!TextUtils.isEmpty(yhkPicList) && (yhkPicList != null) && (yhkPicList.length > 2)) {
                                    val yhk_str = yhkPicList.substring(1, yhkPicList.length - 1)
                                    if (yhk_str.contains(",")) {
                                        val split = yhk_str.split(",").toTypedArray()
                                        yhkPic = split[0].substring(1, split[0].length - 1)
                                        yhkBackPic = split[1].substring(1, split[1].length - 1)
                                    } else {
                                        yhkPic = yhk_str.substring(1, yhk_str.length - 1)
                                    }
                                }
                                val gisdk_ = dkInfoDetail.gisdk

                                if (gisdk_ == null || gisdk_.size == 0) {
                                    hideAvi()
                                    return@getGuoYuan
                                }

                                val account = PreferUtils.getString(mContext, Constants.account)
                                getInstance(this)!!.saveFarmer2("", dkInfoDetail.fhbbxr, account, "", dkInfoDetail.jdlkhh,
                                        dkInfoDetail.areacode, dkInfoDetail.areaname, dkInfoDetail.zjlx, dkInfoDetail.zjhm, zjPic,
                                        zjBackPic, address, dkInfoDetail.sjh, dkInfoDetail.sfpkh, Utils.getText(etAreaName),
                                        dkInfoDetail.zhmc, dkInfoDetail.yhzh, dkInfoDetail.zjmc, yhkPic, yhkBackPic, "",
                                        TimeUtil.getTime(Constants.yyyy_mm_dd), TimeUtil.getTime(Constants.yyyy_mm_dd), dkInfoDetail.zjhzq, dkInfoDetail.zjhqq, signpiclist, dkInfoDetail.jc, dkInfoDetail.lhh, qyxz, qyxzCode
                                ) { rtnCode, message ->


                                    if (rtnCode == 0) {
                                        val array = JSONArray()
                                        for (item_: GisdkDTO in gisdk_) {
                                            dkbm = item_.dkbm
                                            dkzc = item_.dkzc //地块周长
                                            gisdk = item_.gisdk
                                            dkmj = item_.dkmj
                                            xczp = item_.xczp
                                            ltzp = item_.ltzp
                                            ybmj = item_.ybmj

                                            val `object` = JSONObject(true)
                                            `object`["CompanyNumber"] = companyNum
                                            `object`["FarmerNumber"] = zjhm
                                            `object`["Number"] = dkbm
                                            `object`["Name"] = fhbbxr
                                            `object`["Square"] = dkmj.toString() + ""
                                            `object`["CirLength"] = dkzc.toString() + ""
                                            `object`["CropCode"] = bdmccode
                                            `object`["CropName"] = bdmc
                                            `object`["CheckSquare"] = ybmj.toString() + ""
                                            `object`["OwnerNo"] = qszh
                                            `object`["OwnerPicture"] = qspiclist
                                            `object`["GIS"] = gisdk
                                            `object`["GISPicture"] = ltzp
                                            `object`["LivePicture"] = xczp
                                            // object.put("FarmerSignature", signpiclist);
                                            `object`["CheckSignature"] = fypiclist
                                            `object`["CreateTime"] = TimeUtil.getTime(Constants.yyyy_mm_dd)
                                            `object`["UpdateTime"] = TimeUtil.getTime(Constants.yyyy_mm_dd)
                                            array.add(`object`)
                                        }
                                        getInstance(mContext)!!.saveLand(array) { rtnCode1: Int, message1: String? ->
                                            showStr(message1)
                                            hideAvi()
                                        }
                                    } else {
                                        showStr(message)
                                        hideAvi()
                                    }
                                }
                            }
                        } else {
                            showStr("数据为空")
                            hideAvi()
                        }
                    } else {
                        showStr("数据为空")
                        hideAvi()
                    }
                }
                hideAvi()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}