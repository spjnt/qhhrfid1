package tramais.hnb.hhrfid.ui.crop

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.baidu.location.LocationClient
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.*
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.lv.ImageAdapterNew
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.CameraOnlyActivity
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.UpLoadFileUtil.upLoadFile
import java.util.*
import kotlin.collections.ArrayList

class CropAddNewCheckActivity : BaseActivity(), ChoicePhoto {
    private var mTvBaoanName: TextView? = null
    private var mTvStandardInsureType: TextView? = null
    private var mEtAddress: EditText? = null
    private var mRiskReason: TextView? = null
    private var mEtDisasterAreaUnit: EditText? = null
    private var mEtLossArea: EditText? = null
    private var mEtGrouJieduan: TextView? = null
    private var mEtAmountDamage: EditText? = null
    private var mEtRiskJingguo: EditText? = null
    private var mEtChakanYijian: EditText? = null
    private var mTakePhoto: ImageView? = null
    private var mLvPhotos: RecyclerView? = null
    private var mSave: Button? = null
    private var mDetail: Button? = null
    var mLocationClient: LocationClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crop_add_new_check)
    }

    override fun initView() {
        setTitleText("新增查勘记录")
        mTvBaoanName = findViewById(R.id.tv_baoan_name)
        mTvStandardInsureType = findViewById(R.id.tv_standard_insure_type)
        mEtAddress = findViewById(R.id.et_address)
        mRiskReason = findViewById(R.id.risk_reason_unit)
        mEtDisasterAreaUnit = findViewById(R.id.et_disaster_area_unit)
        mEtLossArea = findViewById(R.id.et_loss_area)
        mEtGrouJieduan = findViewById(R.id.et_grou_jieduan)
        mEtAmountDamage = findViewById(R.id.et_amount_damage)
        mEtRiskJingguo = findViewById(R.id.et_risk_jingguo)
        mEtChakanYijian = findViewById(R.id.et_chakan_yijian)
        mTakePhoto = findViewById(R.id.take_photo)
        mLvPhotos = findViewById(R.id.lv_photos)
        mSave = findViewById(R.id.save)
        mDetail = findViewById(R.id.detail)
        mAdapter = ImageAdapterNew(this)
        mLvPhotos!!.layoutManager = GridLayoutManager(this, 3)
        mLvPhotos!!.adapter = mAdapter
    }

    private var pic: MutableList<String> = ArrayList()
    var farmer_name: String? = null
    var fAddress: String? = null
    var fid: String? = null
    var ba_num: String? = null
    var FProductCode: String? = null
    var maxFid: String? = null
    var statu: String? = null
    override fun initData() {
        pic.clear()
        intent?.let {
            statu = it.getStringExtra("statu")
            FProductCode = it.getStringExtra(Constants.FProductCode)
            growthStage(FProductCode.toString())
            if (statu.equals("3")) {

                maxFid = it.getStringExtra(Constants.MaxFid)
                fid = ""
                farmer_name = it.getStringExtra(Constants.farmer_name)
                ba_num = it.getStringExtra(Constants.Ba_num)
                mTvStandardInsureType!!.text = TimeUtil.getTime(Constants.yyyy_mm_dd)
            } else {
                maxFid = it.getStringExtra(Constants.MaxFid)
                val fidDetail = it.getSerializableExtra("fid") as CropCheckChanKanList.DataDTO

                if (fidDetail != null) {

                    farmer_name = fidDetail.fFarmerName
                    RequestUtil.getInstance(this)!!.getLandChaKanFidDetail(fidDetail.fBaoAnNumber, fidDetail.fid, object : GetCommon<FidDetail> {
                        override fun getCommon(t: FidDetail) {
                            runOnUiThread {
                                fid = t.fid
                                fAddress = t.fAddress
                                ba_num = t.fBaoAnNumber
                                //查勘日期
                                mTvStandardInsureType!!.text = t.fChaKanDate
                                        ?: TimeUtil.getTime(Constants.yyyy_mm_dd)
                                mEtAddress!!.setText(t.fAddress)
                                mRiskReason!!.text = t.fRiskReason
                                mEtDisasterAreaUnit!!.setText(t.fShouZaiqty.toString())
                                mEtLossArea!!.setText(t.fSunShiqty.toString())
                                mEtGrouJieduan!!.text = t.fGrowthStage
                                mEtAmountDamage!!.setText(t.fLossqty)
                                mEtRiskJingguo!!.setText(t.fRiskProcess)
                                mEtChakanYijian!!.setText(t.fOpintion)
                                val data = t.data
                                if (data != null && data.isNotEmpty())
                                    for (item in data) {
                                        val fPicture = item.fPicture
                                        if (fPicture!!.isNotEmpty() && !pic.contains(fPicture)) pic.add(fPicture)
                                    }
                                mAdapter!!.setGroupList(pic)
                            }
                        }
                    })

                }
            }


        }
        if (NetUtil.checkNet(this) && Utils.getEdit(mEtAddress).isEmpty()) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                if (!TextUtils.isEmpty(add)) {
                    mLocationClient!!.stop()
                    mEtAddress!!.setText(add)
                }
            })
        }
        insureType

    }

    var reasons: MutableList<String>? = ArrayList()
    var reason_code: MutableMap<String, String>? = HashMap()
    private var riskReasons: MutableList<RiskReason.DataDTO>? = null
    private val insureType: Unit
        private get() {
            if (riskReasons != null) riskReasons!!.clear()
            if (reasons != null) reasons!!.clear()
            if (reason_code != null) reason_code!!.clear()
            if (NetUtil.checkNet(this)) {
                RequestUtil.getInstance(this)!!.getRiskReason("种植险事故类型", object : GetCommon<RiskReason> {
                    override fun getCommon(t: RiskReason) {
                        riskReasons = t.data as MutableList<RiskReason.DataDTO>
                        if (riskReasons != null && riskReasons!!.size > 0)
                            for (item in riskReasons!!) {
                                val reasonName = item.reasonName
                                if (!TextUtils.isEmpty(reasonName) && !reasons!!.contains(reasonName)) reasons!!.add(reasonName.toString())
                                reason_code!![reasonName.toString()] = item.reasonCode.toString()
                            }
                    }

                })
            }
        }


    var growth: MutableList<String> = ArrayList()
    fun growthStage(id: String) {
        RequestUtil.getInstance(this)!!.getLandGrowthStage(id) { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->

            if (datas != null && datas.size > 0) {
                val growthBean = GsonUtil.instant?.parseCommonUseArr(datas, GrowthStageBean::class.java)
                for (item in growthBean!!) {
                    val fStage = item.fStage
                    if (!TextUtils.isEmpty(fStage) && !growth.contains(fStage)) growth.add(fStage)
                }

            }
        }
    }

    override fun initListner() {

        mEtGrouJieduan!!.setOnClickListener {
            if (growth == null || growth.size == 0) {
                showStr("暂无生长阶段可选")
                return@setOnClickListener
            }
            PopuChoice(this, mEtGrouJieduan, "请选择生长阶段", growth) { str -> mEtGrouJieduan!!.text = str }
        }
        mTvStandardInsureType!!.setOnClickListener { v: View? -> TimeUtil.initTimePicker(this) { str: String? -> mTvStandardInsureType!!.text = str } }
        mRiskReason!!.setOnClickListener { v: View? ->
            if (reasons == null || reasons!!.size == 0) {
                showStr("暂无出险原因选择")
                return@setOnClickListener
            }
            PopuChoice(this@CropAddNewCheckActivity, mRiskReason, "请选择出险原因", reasons) { str: String? -> mRiskReason!!.text = str }
        }
        mSave!!.setOnClickListener { v: View? -> save() }
        mTakePhoto!!.setOnClickListener { v: View? ->
            PopuChoicePicture(this, this).showAtLocation(mTakePhoto, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)


        }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val item = it.getItem(position)
                DialogImg(this, item).show()
            }
            it.setOnItemLongClickListener { adapter, view, position ->
                //  it.removeAt(position)
                it.deleteImage(position
                )
                true
            }
        }
        mDetail!!.setOnClickListener { v ->
            val intent = Intent(this, CheckSunShiList::class.java)
            //if (statu.equals("3")) fid = ""
            intent.putExtra(Constants.Ba_num, ba_num)
            intent.putExtra(Constants.fid, fid)
            intent.putExtra(Constants.MaxFid, maxFid)
            startActivityForResult(intent, 125)
        }
    }

    private var fileUrlLists: MutableList<String>? = ArrayList()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 124) {
                val stringArrayListExtra = data!!.getStringArrayListExtra("imgs")
                if (stringArrayListExtra != null) {
                    fileUrlLists!!.addAll(stringArrayListExtra)
                }
                fileUrlLists?.let {
                    mAdapter!!.addImages(
                            fileUrlLists
                    )
                }

            } else if (requestCode == 125) {
                mEtLossArea!!.setText(data!!.getStringExtra(Constants.loss).toString())
                mEtDisasterAreaUnit!!.setText(data!!.getStringExtra(Constants.risk).toString())
            }
        }
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

    private fun save() {
        if (mAdapter == null) return
        val data = mAdapter!!.getPics()
        if (data == null || data.size == 0) {
            showStr("请拍摄查勘照片")
            return
        }
        var growStage = Utils.getText(mEtGrouJieduan)
        if (growStage.isNullOrEmpty()) {
            showStr("请选择生长阶段")
            return
        }
        var mEtAmountDamage = Utils.getText(mEtAmountDamage)
        if (mEtAmountDamage.isNullOrEmpty()) {
            showStr("请输入估损金额")
            return
        }

        showAvi()
        Thread {
            upLoadFile(this, "查勘", "", data, GetList { list_photo ->
                if (list_photo.size == data!!.size) {
                    RequestUtil.getInstance(this@CropAddNewCheckActivity)!!.SaveLandChaKan(ba_num,
                            fid, Utils.getText(mTvStandardInsureType), userName,
                            Utils.getEdit(mEtAddress),
                            Utils.getText(mRiskReason), Utils.getEdit(mEtDisasterAreaUnit), "",
                            Utils.getText(mEtGrouJieduan), mEtAmountDamage,
                            Utils.getEdit(mEtRiskJingguo), Utils.getEdit(mEtChakanYijian), "",
                            Utils.list2String2(list_photo), maxFid, Utils.getEdit(mEtLossArea), object : GetResult {
                        override fun getResult(result: ResultBean?) {
                            showStr(result!!.msg)
                            hideAvi()
                        }
                    })
                }
            })
        }.start()


    }

    var mAdapter: ImageAdapterNew? = null


    override fun actionCamera() {
        if (pic.size == 9) {
            showStr("无需新增照片")
            return
        }
        val intent = Intent(this, CameraOnlyActivity::class.java)
        var fenPei = FenPei()
        fenPei.farmerName = farmer_name

        intent.putExtra("fenpei", fenPei)
        intent.putExtra("photo_num", pic.size)
        startActivityForResult(intent, 124)
    }

    override fun actionAlbum() {
        if (pic.size == 9) {
            showStr("无需新增照片")
            return
        }

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.MULTIPLE)
                .isCompress(true)
                .cutOutQuality(20)// 裁剪压缩质量 默认90 int
                .minimumCompressSize(90)// 小于100kb的图片不压缩
                .theme(R.style.picture_default_style)
                .maxSelectNum(9 - pic.size)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onResult(result: List<LocalMedia>) {
                        if (result.isNullOrEmpty()) return
                        var choice_pic: MutableList<String> = ArrayList()
                        choice_pic.clear()
                        for (item_pic in result) {
                            choice_pic.add(item_pic.compressPath)
                        }

                        mAdapter!!.addImages(choice_pic)

                    }

                    override fun onCancel() {}
                })
    }
}