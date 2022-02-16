package tramais.hnb.hhrfid.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.baidu.location.LocationClient
import com.mylhyl.acp.Acp
import com.mylhyl.acp.AcpListener
import com.mylhyl.acp.AcpOptions
import kotlinx.android.synthetic.main.activity_goto_camer.*
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.Region
import tramais.hnb.hhrfid.bean.RiskReason
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetCommonWithError
import tramais.hnb.hhrfid.listener.DetailLocationListener
import tramais.hnb.hhrfid.litePalBean.RiskReasonCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceRegion
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.*

class ActivityGoToCamera : BaseActivity() {
    private var mFarmerName: EditText? = null
    private var mIv: ImageView? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var mEarTag: EditText? = null
    private var mCamer: Button? = null
    private var mAddressDetail: TextView? = null

    var location_add: String? = null
    var mLocationClient: LocationClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goto_camer)
    }

    override fun initView() {
        setTitleText("理赔拍照")
        mFarmerName = findViewById(R.id.et_farmer_name)
        mIv = findViewById(R.id.iv)
        mEarTag = findViewById(R.id.et_ear_tag)
        mCamer = findViewById(R.id.camer)
        mAddressDetail = findViewById(R.id.tv_address_detail)
        chuxian_time = TimeUtil.getTime(Constants.yyyy_mm_dd)
        choice_time.text = chuxian_time
    }

    override fun initData() {

        val insure = PreferUtils.getString(this, Constants.camer_insure)
        if (!insure.isNullOrEmpty()) {
            insurance_type.text = insure
            insure_type = insure
            if (insure == "养殖险") {
                et_ear_tag.isEnabled = true
                et_ear_tag.setText("")
                et_ear_tag.hint = "请输入耳标号"
            } else {
                et_ear_tag.setText("")
                et_ear_tag.isEnabled = false
                et_ear_tag.hint = "种植险/林险无需输入耳标号"
            }
        }
        val insure_name = PreferUtils.getString(this, Constants.camer_insure_name)
        if (!insure_name.isNullOrEmpty()) {
            biaodi_name.text = insure_name
            biaodi_ = insure_name
        }
        getRiskReason()
        getRegion()

        if (NetUtil.checkNet(this)) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            if (mLocationClient != null) mLocationClient!!.start()

            //声明LocationClient类
            mLocationClient!!.registerLocationListener(DetailLocationListener { lat: Double, log: Double, add: String? ->
                // LogUtils.e("add  $add $lat  $log")
                if (add.isNullOrEmpty() || add.isNullOrBlank() || add.contains("null")) {
                    location_add = "无法定位"
                    latitude = 0.0
                    longitude = 0.0
                } else {
                    location_add = add
                    tv_address_detail.text = add
                    latitude = lat
                    longitude = log
                }
                mLocationClient!!.stop()
            })
        }
    }

    var riskReason: MutableList<String> = ArrayList()
    var biaodis: MutableList<String> = ArrayList()
    fun getRiskReason() {
        riskReason.clear()
        biaodis.clear()
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this)!!.getRiskReason("", object : GetCommonWithError<RiskReason> {
                override fun getCommon(t: RiskReason) {
                    val data = t.data
                    if (data != null && data.isNotEmpty()) {
                        for (item in data) {
                            val reasonName = item.reasonName
                            val fCategory = item.fCategory
                            if (!reasonName.isNullOrEmpty()) {
                                if (fCategory == "理赔出险原因" && !riskReason.contains(reasonName)) {
                                    riskReason.add(reasonName)
                                } else if (fCategory == "理赔标的" && !biaodis.contains(reasonName)) {
                                    biaodis.add(reasonName)
                                }
                            }
                        }
                    } else {
                        getReasonCache()
                    }
                }

                override fun getError() {
                    getReasonCache()
                }
            })
        } else {
            getReasonCache()
        }
    }

    fun getReasonCache() {
        val findAll = LitePal.findAll(RiskReasonCache::class.java)
        if (findAll != null && findAll.isNotEmpty()) {
            for (item in findAll) {
                val reasonName = item.reasonName
                val fCategory = item.fcategory
                if (!reasonName.isNullOrEmpty()) {
                    if (fCategory == "理赔出险原因" && !riskReason.contains(reasonName)) {
                        riskReason.add(reasonName)
                    } else if (fCategory == "理赔标的" && !biaodis.contains(reasonName)) {
                        biaodis.add(reasonName)
                    }
                }
            }
        }
    }

    var chuxian_time = ""
    var risk_reason = ""
    var biaodi_ = ""

    var insure_type = ""
    override fun initListner() {
        insurance_type.setOnClickListener {
            PopuChoice(this@ActivityGoToCamera, reason, "请选择险种", mutableListOf("养殖险", "种植险", "林险")) {
                insurance_type!!.text = it
                insure_type = it
                if (it == "养殖险") {
                    et_ear_tag.isEnabled = true
                    et_ear_tag.setText("")
                    et_ear_tag.hint = "请输入耳标号"
                } else {
                    et_ear_tag.setText("")
                    et_ear_tag.isEnabled = false
                    et_ear_tag.hint = "种植险/林险无需输入耳标号"
                }
            }

        }
        tv_address_detail!!.setOnClickListener { v: View? ->
            if (regiondata == null || regiondata!!.isEmpty()) {
                showStr("暂无集体户名可选")
                return@setOnClickListener
            }
            val dialogChoiceRegion = DialogChoiceRegion(this, regiondata) { billNumber: String, message: String ->
                if (billNumber == "数据异常") {
                    showStr(billNumber)
                    return@DialogChoiceRegion
                }
                tv_address_detail!!.text = billNumber + message

            }
            if (dialogChoiceRegion != null && !dialogChoiceRegion.isShowing) dialogChoiceRegion.show()
        }

        mCamer!!.setOnClickListener {
            check()
        }
        choice_time.setOnClickListener {
            TimeUtil.initTimePicker(this) {
                choice_time.text = it
                chuxian_time = it
            }
        }
        reason.setOnClickListener {
            if (riskReason.size == 0) {
                showStr("暂无出险原因选择")
                return@setOnClickListener
            }
            PopuChoice(this@ActivityGoToCamera, reason, "请选择出险原因", riskReason) {
                reason!!.text = it
                risk_reason = it
            }
        }
        biaodi_name.setOnClickListener {
            if (biaodis.size == 0) {
                showStr("暂无标的名称选择")
                return@setOnClickListener
            }
            PopuChoice(this@ActivityGoToCamera, biaodi_name, "请选择标的名称", biaodis) {
                biaodi_name!!.text = it
                biaodi_ = it
            }
        }
    }

    fun goToCamer() {
        if (insure_type.isEmpty()) {
            showStr("请选择险种")
            return
        }
        val farmer_name = et_farmer_name.text.toString()
        if (farmer_name.isEmpty()) {
            showStr("请输入被保险人姓名")
            return
        }

        val tag = et_ear_tag.text.toString()
        /* if (insure_type == "养殖险" && tag.isEmpty()) {
             showStr("请输入耳标号")
             return
         }*/
        if (chuxian_time.isEmpty()) {
            showStr("请选择出险时间")
            return
        }
        if (risk_reason.isEmpty()) {
            showStr("请选择报案原因")
            return
        }
        if (biaodi_.isEmpty()) {
            showStr("请选择标的名称")
            return
        }
        val detail = tv_address_detail.text.toString()
        if (detail.isEmpty()) {
            showStr("请选择村镇地址")
            return
        }
        PreferUtils.putString(this, Constants.camer_insure, insure_type)
        PreferUtils.putString(this, Constants.camer_insure_name, biaodi_)

        val fenPei = FenPei()
        fenPei.farmerName = farmer_name
        fenPei.fRemark = "only_photo"
        fenPei.createTime = chuxian_time
        fenPei.riskReason = risk_reason
        fenPei.riskQty = biaodi_
        fenPei.fCoinsFlag = insure_type
        fenPei.riskAddress = tv_address.text.toString() + detail
        var tag_ = if (tag.isNullOrBlank()) {
            "未知耳标号"
        } else {
            tag
        }
        fenPei.EarTag = tag_
        fenPei.lat = latitude
        fenPei.log = longitude
        val intent = Intent(this, CameraOnlyActivity::class.java)
        intent.putExtra("fenpei", fenPei)
        intent.putExtra("photo_num", 0)
        startActivity(intent)
    }

    private var regiondata: List<Region.DataBean>? = null
    fun getRegion() {
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this)!!.getRegionWithCache(object : GetCommonWithError<Region> {
                override fun getCommon(t: Region) {
                    if (t == null || t.data.isNullOrEmpty()) {
                        getReasonCache()
                    } else {
                        regiondata = t.data
                        tv_address!!.text = t.fProvince + t.fCity + t.fCounty
                    }

                }

                override fun getError() {
                    getRegionCache()
                }
            })
        } else {
            getRegionCache()
        }


    }

    fun getRegionCache() {
        Utils.getRegions(object : GetCommon<Region> {
            override fun getCommon(t: Region) {
                regiondata = t.data
                tv_address!!.text = t.fProvince + t.fCity + t.fCounty
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (mLocationClient != null) mLocationClient!!.stop()

    }


    override fun onDestroy() {

        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    private val permissions_ = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    private val permissions: MutableList<String> = ArrayList()
    fun check() {
        val pm: PackageManager = packageManager
        for (item in permissions_) {
            val permission = pm.checkPermission(item, this.packageName)
            if (permission != 0) {
                permissions.add(item)
            }
        }
        if (permissions.isNullOrEmpty()) {
            goToCamer()
        } else {
            Acp.getInstance(this).request(AcpOptions.Builder()
                    .setPermissions(*permissions.toTypedArray())
                    .build(),
                    object : AcpListener {
                        override fun onGranted() {
                            goToCamer()
                        }

                        override fun onDenied(permissions: List<String>) {
                            showStr("相机相册，内存读写，定位权限被拒绝")
                        }
                    })
        }

    }

}