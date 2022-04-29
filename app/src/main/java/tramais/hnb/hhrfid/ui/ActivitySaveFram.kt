package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import org.litepal.LitePal
import org.litepal.LitePal.findAllAsync
import org.litepal.LitePal.where
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.FarmList
import tramais.hnb.hhrfid.bean.Naturean
import tramais.hnb.hhrfid.bean.Region
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.*
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.litePalBean.BankInfoCache
import tramais.hnb.hhrfid.litePalBean.FarmListCache
import tramais.hnb.hhrfid.litePalBean.IdCategoryCache
import tramais.hnb.hhrfid.litePalBean.NatureanCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceBank
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceRegion
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.UpLoadFileUtil.upLoadFile


class ActivitySaveFram : BaseActivity(), ChoicePhoto {
    var mLocationClient: LocationClient? = null
    var pathImgId = ""
    var pathImgIdBack = ""
    var pathImgBank = ""
    var idCard_path = ""
    var idCard_path_back = ""
    var backCard_path = ""
    var idCard_url = ""
    var backCard_url = ""
    private var mIvUpIdPh: TextView? = null
    private var mEtName: EditText? = null
    private var mEtUnderSer: EditText? = null
    private var mEtUnderIdNums: EditText? = null
    private var mEtUnderIdAddress: EditText? = null
    private var mIvUploadCard: TextView? = null
    private var mEtCardBank: TextView? = null
    private var mEtCardAcc: EditText? = null
    private var mEtCardNum: EditText? = null
    private var mEtPhoneNum: EditText? = null
    private var mEtUnderAddress: EditText? = null
    private var mCbSelf: CheckBox? = null
    private var mCbTogether: CheckBox? = null
    private var mEtTogetherName: TextView? = null
    private var mTvCategory: TextView? = null
    private var mDelBank: ImageView? = null
    private var isTogether = false
    private var isSelf = false
    private var mLlTogether: RelativeLayout? = null
    private val mContext: Context = this@ActivitySaveFram
    private var mSaveFarm: Button? = null
    private var mChage: Button? = null
    private var mIvTakeIdPhoto: ImageView? = null
    private var mIvTakeBankPhoto: ImageView? = null
    private var mInputMeasure: EditText? = null
    private var mInputInfo: EditText? = null
    private var mBtnOnlySave: Button? = null
    private var mIvMore: ImageView? = null
    private var mIsPoorTrue: AppCompatRadioButton? = null
    private var mIsPoorFalse: AppCompatRadioButton? = null
    private var mIvDelFont: ImageView? = null
    private var mIvDelBack: ImageView? = null
    private var mIvTakeIdPhotoBack: ImageView? = null
    private var isPoor = 0
    private val list_indefiy: MutableList<String>? = ArrayList()
    private val map_indefiy: MutableMap<String, String>? = HashMap()
    private var hasZjPic = false
    private var hasZjPicBack = false
    private var hasBankPic = false
    private var clickImg = 0
    private var alreadyInPut: CharSequence = ""
    private val maxInpuLength = 500
    private var isGoToNextUi = false
    private var regiondata: List<Region.DataBean>? = null
    private var overdueTime: String? = null
    private var startTime: String? = null
    private var mOverTime: TextView? = null
    private var mRlBack: RelativeLayout? = null
    private var mLlOverTime: LinearLayout? = null
    private var mLlStartTime: LinearLayout? = null
    private var mStartTime: TextView? = null
    private var fRegionNumber: String? = null
    private var toSign: Button? = null
    private var ivSign: ImageView? = null
    private var mNature: TextView? = null
    private var mName: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_farm)
    }

    override fun initView() {
        setTitleText("创建投保人信息")
        mName = findViewById(R.id.tv_name)
        mNature = findViewById(R.id.tv_nature)
        toSign = findViewById(R.id.to_sign)
        ivSign = findViewById(R.id.iv_sign)
        mIvUpIdPh = findViewById(R.id.iv_up_idPh)
        mEtName = findViewById(R.id.et_name)
        mEtUnderSer = findViewById(R.id.et_under_ser)
        mEtUnderIdNums = findViewById(R.id.et_under_id_nums)
        mEtUnderIdAddress = findViewById(R.id.et_under_id_address)
        mIvUploadCard = findViewById(R.id.iv_upload_card)
        mEtCardBank = findViewById(R.id.et_card_bank)
        mEtCardAcc = findViewById(R.id.et_card_acc)
        mEtCardNum = findViewById(R.id.et_card_num)
        mEtPhoneNum = findViewById(R.id.et_phone_num)
        mEtUnderAddress = findViewById(R.id.et_under_address)
        mCbSelf = findViewById(R.id.cb_self)
        mCbTogether = findViewById(R.id.cb_together)
        mEtTogetherName = findViewById(R.id.et_together_name)
        mBtnOnlySave = findViewById(R.id.only_save_farm)
        mTvCategory = findViewById(R.id.tv_category)
        mDelBank = findViewById(R.id.del_bank)
        mLlTogether = findViewById(R.id.ll_together)

        mSaveFarm = findViewById(R.id.save_farm)
        mIvTakeIdPhoto = findViewById(R.id.iv_take_id_photo)
        mIvTakeBankPhoto = findViewById(R.id.iv_take_bank_photo)
        mInputMeasure = findViewById(R.id.input_measure)
        mInputInfo = findViewById(R.id.input_info)
        mIvMore = findViewById(R.id.iv_more)
        mIsPoorTrue = findViewById(R.id.is_poor_true)
        mIsPoorFalse = findViewById(R.id.is_poor_false)
        mIsPoorFalse!!.isChecked = true
        mIvDelFont = findViewById(R.id.iv_del_font)
        mIvDelBack = findViewById(R.id.iv_del_back)
        mIvTakeIdPhotoBack = findViewById(R.id.iv_take_id_photo_back)
        mOverTime = findViewById(R.id.tv_overTime)
        mRlBack = findViewById(R.id.rl_back)
        mLlOverTime = findViewById(R.id.ll_overTime)
        mChage = findViewById(R.id.change)
        mLlStartTime = findViewById(R.id.ll_startTime)
        mStartTime = findViewById(R.id.tv_startTime)
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

    var raiseAddress: String? = null
    override fun initData() {
        val intent = intent
        if (intent != null) {
            val underWrites = intent.getSerializableExtra("underWrites")
            val type = intent.getStringExtra("type")
           // LogUtils.e("type  $type")
            if (type == "Content") {
                setChange(false)
                if (underWrites != null) {
                    underWrites as FarmList
                    fRegionNumber = underWrites.areaCode
                    //   LogUtils.e("zjCategory  ${underWrites.zjCategory}  ${underWrites.category}")
                    currentIdCate = underWrites.zjCategory
                    mTvCategory!!.text = currentIdCate
                    mIvTakeIdPhoto!!.setBackgroundResource(0)
                    if (currentIdCate == "营业执照") {
                        mName!!.text = "证件名称"
                        mNature!!.isEnabled = true
                        mNature!!.text = underWrites.fEnterpriseNatureName
                        mRlBack!!.visibility = View.GONE
                        mLlOverTime!!.visibility = View.GONE
                        mLlStartTime!!.visibility = View.GONE
                        mIvTakeIdPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.yingyezhizhao))
                    } else {
                        mNature!!.text = "无"
                        mNature!!.isEnabled = false
                        mName!!.text = getString(R.string.name)
                        mIvTakeIdPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.idcard_font))
                        mRlBack!!.visibility = View.VISIBLE
                        mLlOverTime!!.visibility = View.VISIBLE
                        mLlStartTime!!.visibility = View.VISIBLE
                    }

                    natureName = underWrites.fEnterpriseNatureName
                    natureCode = underWrites.fEnterpriseNature
                    FBankCode = underWrites.fBankCode
                    FBankRelatedCode = underWrites.fBankRelatedCode
                    mOverTime!!.text = underWrites.fValidate
                    mStartTime!!.text = underWrites.fStartTime
                    //     LogUtils.e("underWrites.fStartTime  ${underWrites.fStartTime}")
                    mEtName!!.setText(underWrites.name)
                    mEtUnderSer!!.setText(underWrites.number)
                    mEtUnderIdNums!!.setText(underWrites.zjNumber)
                    mEtUnderIdAddress!!.setText(underWrites.sfzAddress)
                    mEtCardBank!!.text = underWrites.bankName
                    mEtCardAcc!!.setText(underWrites.accountName)
                    mEtCardNum!!.setText(underWrites.accountNumber)
                    mEtPhoneNum!!.setText(underWrites.mobile)
                    mInputInfo!!.setText(underWrites.remark)
                    val isPoor = underWrites.isPoor
                    idCard_path = underWrites?.zjPicture ?: ""
                    idCard_path_back = underWrites?.zjBackPicture ?: ""
                    backCard_path = underWrites?.bankPicture ?: ""
                    farmer_sign = underWrites?.signPicture ?: ""
                    //  LogUtils.e("farmer_sign  $farmer_sign")
                    if (!TextUtils.isEmpty(idCard_path)) {
                        hasZjPic = true
                        Glide.with(this).load(idCard_path).into(mIvTakeIdPhoto!!)
                    }
                    if (!TextUtils.isEmpty(idCard_path_back)) {
                        hasZjPicBack = true
                        mIvTakeIdPhotoBack?.let { Glide.with(this).load(idCard_path_back).into(it) }
                    }
                    if (!TextUtils.isEmpty(backCard_path)) {
                        hasBankPic = true
                        mIvTakeBankPhoto?.let { Glide.with(this).load(backCard_path).into(it) }
                    }
                    if (farmer_sign!!.isNotEmpty()) {
                        ivSign?.let { Glide.with(this).load(farmer_sign).into(it) }
                    }

                    if (isPoor == 0) {
                        mIsPoorFalse!!.isChecked = true
                        mIsPoorTrue!!.isChecked = false
                    } else {
                        mIsPoorTrue!!.isChecked = true
                        mIsPoorFalse!!.isChecked = false
                    }
                    raiseAddress = underWrites?.raiseAddress ?: ""
                    mEtUnderAddress!!.setText(raiseAddress)
                    mEtTogetherName!!.text = underWrites.area
                    val category = underWrites.category
                    if (category == "自行投保") {
                        mCbSelf!!.isChecked = true
                        isSelf = true
                        mLlTogether!!.visibility = View.GONE
                    }
                    if (category == "集体投保") {
                        mCbTogether!!.isChecked = true
                        isTogether = true
                        mLlTogether!!.visibility = View.VISIBLE
                    }
                } else {
                    LogUtils.e("under  is  null")
                }
            } else {
                currentIdCate = "身份证"
                mCbTogether!!.isChecked = true
                isTogether = true
                setChange(true)
                mBtnOnlySave!!.visibility = View.VISIBLE
                mSaveFarm!!.visibility = View.VISIBLE
                mChage!!.visibility = View.GONE
                mNature!!.text = "无"
                mNature!!.isEnabled = false
                mName!!.text = getString(R.string.name)
            }

        }
        //mEtUnderAddressU
        if (NetUtil.checkNet(this) && TextUtils.isEmpty(raiseAddress)) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                if (!TextUtils.isEmpty(add)) {
                    mLocationClient!!.stop()
                    mEtUnderAddress!!.setText(add)
                }
            })
        } else {
            mEtUnderAddress!!.setText(raiseAddress)
        }
        list_indefiy?.clear()
        map_indefiy?.clear()
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this@ActivitySaveFram)!!.getIndefiyCategory { list: List<String>?, map: Map<String, String>? ->
                list_indefiy!!.addAll((list)!!)
                map_indefiy!!.putAll((map)!!)
            }
        } else {
            findAllAsync(IdCategoryCache::class.java).listen { list: List<IdCategoryCache> ->
                list_indefiy?.clear()
                map_indefiy?.clear()
                for (item: IdCategoryCache in list) {
                    if (item.identifyName != null && !list_indefiy!!.contains(item.identifyName)) list_indefiy.add(item.identifyName)
                    if (item.identifyName != null && !map_indefiy!!.containsKey(item.identifyName)) map_indefiy[item.identifyName] = item.identifyCode
                }
            }
        }
        if (NetUtil.checkNet(this))
            RequestUtil.getInstance(this)!!.getRegion(object : GetCommon<Region> {
                override fun getCommon(t: Region) {
                    if (t.code >= 0)
                        regiondata = t.data
                }
            })
        else
            Utils.getRegions(object : GetCommon<Region> {
                override fun getCommon(t: Region) {
                    regiondata = t.data
                }
            })
        getBankInfo("")
        getNature()
    }

    var getBankResulData: MutableList<BankInfoDetail.GetBankResulDataDTO?>? = ArrayList()
    fun getBankInfo(bankCode: String) {
        if (NetUtil.checkNet(mContext)) {
            RequestUtil.getInstance(this)!!.getBankInfoDetail(bankCode, object : GetBankInfo {
                override fun bankInfo(info: BankInfoDetail?) {
                    info?.let {
                        getBankResulData = it.getGetBankResulData() as MutableList<BankInfoDetail.GetBankResulDataDTO?>?

                    }
                }
            })
        } else {
            val findAll = LitePal.findAll(BankInfoCache::class.java)
            if (findAll != null && findAll.isNotEmpty()) {
                val jsonString = findAll[0].jsonString
                if (jsonString.isNullOrBlank()) return
                val parseObject = JSONObject.parseObject(jsonString, BankInfoDetail::class.java)
                getBankResulData = parseObject.getGetBankResulData() as MutableList<BankInfoDetail.GetBankResulDataDTO?>?

            }
        }

    }

    fun setChange(z: Boolean) {
        mIvUpIdPh!!.isEnabled = z
        mEtName!!.isEnabled = z
        mEtUnderSer!!.isEnabled = z
        mEtUnderIdNums!!.isEnabled = z
        mEtUnderIdAddress!!.isEnabled = z
        mIvUploadCard!!.isEnabled = z
        mEtCardBank!!.isEnabled = z
        mEtCardAcc!!.isEnabled = z
        mEtCardNum!!.isEnabled = z
        mEtPhoneNum!!.isEnabled = z
        mEtUnderAddress!!.isEnabled = z
        mCbSelf!!.isEnabled = z
        mCbTogether!!.isEnabled = z
        mTvCategory!!.isEnabled = z
        mLlTogether!!.isEnabled = z
        mLlTogether!!.isEnabled = z
        mSaveFarm!!.isEnabled = z
        // mIvTakeIdPhoto!!.isEnabled = z
        //  mIvTakeBankPhoto!!.isEnabled = z
        mInputMeasure!!.isEnabled = z
        mInputInfo!!.isEnabled = z
        mIvMore!!.isEnabled = z
        mIsPoorTrue!!.isEnabled = z
        mIsPoorFalse!!.isEnabled = z
        mOverTime!!.isEnabled = z
        toSign!!.isEnabled = z

        if (z) {
            mBtnOnlySave!!.visibility = View.VISIBLE
            mSaveFarm!!.visibility = View.VISIBLE
            mChage!!.visibility = View.GONE
            if (hasBankPic)
                mDelBank!!.visibility = View.VISIBLE
        } else {
            mBtnOnlySave!!.visibility = View.GONE
            mSaveFarm!!.visibility = View.GONE
            mChage!!.visibility = View.VISIBLE
            mDelBank!!.visibility = View.GONE
        }


    }

    var natures: MutableList<String> = ArrayList()
    var natures_name_code: MutableMap<String, String> = HashMap()
    fun getNature() {
        natures?.clear()
        natures_name_code.clear()
        if (NetUtil.checkNet(this)) {
            RequestUtil.getInstance(this)!!.getEnterpriseNature(object : GetNature {
                override fun getNature(nature: Naturean?) {
                    nature?.let {
                        val dATA = it.dATA
                        for (item in dATA!!) {
                            val fName = item.fName
                            if (fName!!.isNotBlank() && fName.isNotEmpty() && !natures.contains(fName)) natures.add(fName)
                            natures_name_code[fName] = item.fCode.toString()
                        }
                    }
                }
            })
        } else {
            val findAll = LitePal.findAll(NatureanCache::class.java)
            findAll?.let {

                for (item in findAll) {
                    val fName = item.fName
                    if (fName.isNotBlank() && fName.isNotEmpty() && !natures.contains(fName)) natures.add(fName)
                    natures_name_code[fName] = item.fCode
                }
            }
        }

    }

    var farmer_sign: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Config.REQUEST_SING) {
            var _sign = data!!.getStringExtra(Constants.Sign_Path)
            if (!_sign.isNullOrEmpty()) {
                farmer_sign = _sign
                ivSign?.let { Glide.with(this).load(_sign).into(it) }
            }
        }
    }

    var FBankCode: String? = null
    var FBankRelatedCode: String? = null
    var AccountName: String? = null
    var natureCode: String? = null
    var natureName: String? = null
    var currentIdCate: String? = "身份证"
    override fun initListner() {
        mEtCardBank!!.setOnClickListener {
            if (getBankResulData == null || getBankResulData!!.isEmpty()) {
                showStr("暂无银行卡数据")
                return@setOnClickListener
            }
            DialogChoiceBank(mContext, getBankResulData, object : GetBankDetail {
                override fun getBankInfo(infoDetail: BankInfoDetail.GetBankResulDataDTO.GetBankResultDetaiDataDTO?) {
                    infoDetail?.let {
                        mEtCardBank!!.text = it.fBankDetailName
                        FBankCode = it.fBankCode
                        FBankRelatedCode = it.fDetailCode
                        AccountName = it.fBankDetailName
                    }
                }
            }).show()
        }
        toSign!!.setOnClickListener {
            val intent = Intent(this, ActivitySign::class.java)
            intent.putExtra("For_Sign", "创建投保人信息")
            intent.putExtra("Sign_Common", "")
            intent.putExtra("Type", "养殖")
            startActivityForResult(intent, Config.REQUEST_SING)
        }
        mChage!!.setOnClickListener { v: View? ->
            setChange(true)
        }
        mIsPoorTrue!!.setOnClickListener { v: View? ->
            isPoor = 1
            mIsPoorTrue!!.isChecked = true
            mIsPoorFalse!!.isChecked = false
        }
        mIsPoorFalse!!.setOnClickListener { v: View? ->
            isPoor = 0
            mIsPoorFalse!!.isChecked = true
            mIsPoorTrue!!.isChecked = false
        }
        mInputInfo!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                alreadyInPut = s
            }

            override fun afterTextChanged(s: Editable) {
                runOnUiThread {
                    mInputMeasure!!.hint = (maxInpuLength - s.length).toString() + "/" + maxInpuLength
                    val selectionStart = mInputInfo!!.selectionStart
                    val selectionEnd = mInputInfo!!.selectionEnd
                    if (alreadyInPut.length > maxInpuLength) {
                        s.delete(selectionStart - 1, selectionEnd)
                        mInputInfo!!.text = s
                        mInputInfo!!.setSelection(alreadyInPut.length)
                        showStr("已超出最高字数限制")
                    }
                }
            }
        })
        mCbSelf!!.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            isSelf = b
            if (isSelf) {
                isTogether = false
                mCbTogether!!.isChecked = false
                mLlTogether!!.visibility = View.GONE
            }
        }
        mCbTogether!!.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            isTogether = b
            if (isTogether) {
                mLlTogether!!.visibility = View.VISIBLE
                isSelf = false
                mCbSelf!!.isChecked = false
            }
        }
        mNature!!.setOnClickListener {
            if (natures.isEmpty()) {
                showStr("暂无企业性质数据可选")
                return@setOnClickListener
            }
            PopuChoice(this@ActivitySaveFram, mNature, "请选择证件类型", natures) { str: String ->
                mNature!!.text = str
                natureName = str
                natureCode = natures_name_code[str]
            }
        }
        mTvCategory!!.setOnClickListener {
            if (list_indefiy != null && list_indefiy.size > 0)
                PopuChoice(this@ActivitySaveFram, mTvCategory, "请选择证件类型", list_indefiy) { str: String ->
                    if (!currentIdCate.isNullOrEmpty() && currentIdCate == str) return@PopuChoice
                    currentIdCate = str
                    mTvCategory!!.text = str
                    idCard_path = ""
                    idCard_path_back = ""
                    hasZjPic = false
                    hasZjPicBack = false
                    pathImgId = ""
                    pathImgIdBack = ""
                    mIvTakeIdPhoto!!.setBackgroundResource(0)
                    mIvTakeIdPhotoBack!!.setBackgroundColor(0)
                    if ((str == "营业执照")) {
                        mIvTakeIdPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.yingyezhizhao))
                        mRlBack!!.visibility = View.GONE
                        mLlOverTime!!.visibility = View.GONE
                        if (mIvDelFont!!.visibility == View.VISIBLE) mIvDelFont!!.visibility = View.GONE
                        mNature!!.isEnabled = true
                        mNature!!.text = ""
                        natureName = ""
                        mLlStartTime!!.visibility = View.GONE
                        mName!!.text = "证件名称"
                    } else {
                        mIvTakeIdPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.idcard_font))
                        mIvTakeIdPhotoBack!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.idcard_back))
                        mRlBack!!.visibility = View.VISIBLE
                        mLlOverTime!!.visibility = View.VISIBLE
                        if (mIvDelFont!!.visibility == View.VISIBLE) mIvDelFont!!.visibility = View.GONE
                        if (mIvDelBack!!.visibility == View.VISIBLE) mIvDelBack!!.visibility = View.GONE
                        mNature!!.isEnabled = false
                        mName!!.text = getString(R.string.name)
                        mNature!!.text = "无"
                        natureName = ""
                        mLlStartTime!!.visibility = View.VISIBLE
                    }
                }
        }
        /*  身份证反面*/
        mIvTakeIdPhotoBack!!.setOnClickListener { v: View? ->

            if (!hasZjPicBack) {
                clickImg = 3
                PopuChoicePicture(this@ActivitySaveFram, this@ActivitySaveFram).showAtLocation(mIvTakeIdPhoto, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            } else {
                if (idCard_path_back.isNullOrEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, idCard_path_back)
                if (!dialogImg.isShowing) dialogImg.show()
            }
        }
        /*身份证正面*/
        mIvTakeIdPhoto!!.setOnClickListener { view: View? ->

            if (!hasZjPic) {
                clickImg = 1
                PopuChoicePicture(this@ActivitySaveFram, this@ActivitySaveFram).showAtLocation(mIvTakeIdPhoto, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)

            } else {
                LogUtils.e("idCardPath  $idCard_path")
                if (idCard_path.isNullOrEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, idCard_path)
                if (!dialogImg.isShowing) dialogImg.show()
            }
        }
        /*银行卡*/
        mIvTakeBankPhoto!!.setOnClickListener { view: View? ->
            //    LogUtils.e("backCard_path$backCard_path")
            if (!hasBankPic) {
                clickImg = 2
                PopuChoicePicture(this@ActivitySaveFram, this@ActivitySaveFram).showAtLocation(mIvTakeBankPhoto, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            } else {
                if (backCard_path.isNullOrEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, backCard_path)
                if (!dialogImg.isShowing) dialogImg.show()
            }
        }
        /*上传身份证照片识别*/
        mIvUpIdPh!!.setOnClickListener { view: View? ->
            if (!NetUtil.checkNet(mContext)) {
                showStr("请确保网络连接")
                return@setOnClickListener
            }
            showAvi()
            if ((currentIdCate == "身份证")) {
                RequestUtil.getInstance(this@ActivitySaveFram)!!.getIdCardsInfo(Config.indentifyocr, map_indefiy!![currentIdCate], pathImgId, (object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        hideAvi()
                        if (datas != null) {
                            val address = datas.getString("Address")
                            val Name = datas.getString("Name")
                            val SfzNumber = datas.getString("SfzNumber")
                            val msg = datas.getString("Msg")
                            idCard_url = datas?.getString("PicUrl")
                            runOnUiThread {
                                mEtName!!.setText(Name)
                                mEtUnderSer!!.setText(SfzNumber)
                                mEtUnderIdNums!!.setText(SfzNumber)
                                mEtUnderIdAddress!!.setText(address)
                                mEtCardAcc!!.setText(Name)
                                showStr(msg)
                            }
                        }
                    }
                }))
                if (pathImgIdBack.isNullOrEmpty()) return@setOnClickListener
                RequestUtil.getInstance(this@ActivitySaveFram)!!.getIdCardsInfo(Config.IdentifyBackOcr, map_indefiy[currentIdCate], pathImgIdBack, (object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        hideAvi()
                        if (datas != null) {
                            overdueTime = datas.getString("OverdueTime")
                            mOverTime!!.text = overdueTime
                            startTime = datas.getString("StartTime")
                            mStartTime!!.text = startTime
                        }
                    }
                }))
            } else {
                RequestUtil.getInstance(this@ActivitySaveFram)!!.getIdCardsInfo(Config.IdentifyBusinessLicense, map_indefiy!![currentIdCate], pathImgId, (object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        hideAvi()
                        if (datas != null) {
                            val address = datas.getString("CompanyAddr")
                            val Name = datas.getString("CompanyName")
                            val SfzNumber = datas.getString("CompanyNum")
                            val msg = datas.getString("Msg")
                            idCard_url = datas.getString("PicUrl")
                            runOnUiThread {
                                mEtName!!.setText(Name)
                                mEtUnderSer!!.setText(SfzNumber)
                                mEtUnderIdNums!!.setText(SfzNumber)
                                mEtUnderIdAddress!!.setText(address)
                                mEtCardAcc!!.setText(Name)
                                showStr(msg)
                            }
                        }
                    }
                }))
            }

        }
        mIvUploadCard!!.setOnClickListener { view: View? ->
            if (NetUtil.checkNet(mContext)) {
                showAvi()
                RequestUtil.getInstance(this@ActivitySaveFram)!!.getBankInfo(pathImgBank, (object : GetResultJsonObject {
                    override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                        hideAvi()
                        if (datas != null) {
                            val BankAccount = datas.getString("BankAccount")
//                            val BankName = datas.getString("BankName")
                            val BankCardType = datas.getString("BankCardType")
                            val msg = datas.getString("Msg")
                            backCard_url = datas.getString("PicUrl")
                            showStr(msg)
                            runOnUiThread {
                                mEtCardNum!!.setText(BankAccount)
//                                mEtCardBank!!.text = BankName
                            }
                        }
                    }
                }))
            } else {
                showStr("请确保网络连接")
            }
        }
        mSaveFarm!!.setOnClickListener { view: View? ->
            isGoToNextUi = true
            saveFramers()
        }
        mBtnOnlySave!!.setOnClickListener { v: View? ->
            isGoToNextUi = false
            saveFramers()
        }
        mIvMore!!.setOnClickListener { v: View? ->
            if (regiondata == null || regiondata!!.isEmpty()) {
                showStr("暂无集体户名可选")
                return@setOnClickListener
            }
            val dialogChoiceRegion = DialogChoiceRegion(this, regiondata) { billNumber: String, message: String ->
                if (billNumber == "数据异常") {
                    showStr(billNumber)
                    return@DialogChoiceRegion
                }
                mEtTogetherName!!.text = billNumber + message
                for (item: Region.DataBean in regiondata!!) {
                    if ((item.fTown == billNumber) && (item.fVillage == message)) {
                        fRegionNumber = item.fRegionNumber
                    }
                }
            }
            if (dialogChoiceRegion != null && !dialogChoiceRegion.isShowing) dialogChoiceRegion.show()
        }
        mIvDelFont!!.setOnClickListener { v: View? ->
            hasZjPic = false
            mIvDelFont!!.visibility = View.GONE
            pathImgId = ""
            idCard_path = ""
            mIvTakeIdPhoto!!.setBackgroundResource(0)
            if ((currentIdCate == "身份证")) mIvTakeIdPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.idcard_font)) else mIvTakeIdPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.yingyezhizhao))
        }
        mIvDelBack!!.setOnClickListener { v: View? ->
            hasZjPicBack = false

            pathImgIdBack = ""
            mIvDelBack!!.visibility = View.GONE
            idCard_path_back = ""
            mIvTakeIdPhotoBack!!.setBackgroundResource(0)
            mIvTakeIdPhotoBack!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.idcard_back))
        }
        mDelBank!!.setOnClickListener { v: View? ->
            hasBankPic = false
            mDelBank!!.visibility = View.GONE
            pathImgBank = ""
            mIvTakeBankPhoto!!.setBackgroundResource(0)
            mIvTakeBankPhoto!!.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.bank_cark))
        }
        mOverTime!!.setOnClickListener { v: View? -> TimeUtil.initTimePicker(this@ActivitySaveFram, -1, -1) { str: String? -> mOverTime!!.text = str } }
        mStartTime!!.setOnClickListener { v: View? -> TimeUtil.initTimePicker(this@ActivitySaveFram, -1, -1) { str: String? -> mStartTime!!.text = str } }

    }

    private fun saveFramers() {
        if (!isSelf && !isTogether) {
            showStr("请选择投保方式")
            return
        }
        val Name = Utils.getEdit(mEtName)
        if (TextUtils.isEmpty(Name)) {
            showStr("请输入投保户姓名")
            return
        }
        val EtUnderIdNums = Utils.getEdit(mEtUnderIdNums)
        if (TextUtils.isEmpty(EtUnderIdNums)) {
            showStr("请输入投保户证件号码")
            return
        }
        val category = Utils.getText(mTvCategory)

        val mStartTime = Utils.getText(mStartTime)
        val overTime = Utils.getText(mOverTime)
        val current_day = TimeUtil.getTime(Constants.yyyy_mm_dd)
        if (category == "身份证") {
            if (EtUnderIdNums.length == 18) {
                if (!IdCardUtil.isIDCard(EtUnderIdNums)) {
                    showStr("请输入正确的证件号码")
                    return
                }
            } else {
                showStr("请输入18位证件号码")
                return
            }
            if (mStartTime.isBlank()) {
                showStr("请选择证件证件签发日期")
                return
            }
            if (overTime.isBlank()) {
                showStr("请选择证件有效期结束时间")
                return
            }
            val start_time_com = TimeUtil.getTimeCompareSize(mStartTime, current_day)
            if (!start_time_com) {
                showStr("证件签发日期不得大于当前时间")
                return
            }
            val end_time_com = TimeUtil.getTimeCompareSize(overTime, current_day)
            if (end_time_com) {
                showStr("证件有效期结束时间不得小于当前时间")
                return
            }

            natureName = ""
        } else {
            if (natureName.isNullOrBlank()) {
                showStr("请选择企业性质")
                return
            }
        }


        val EtUnderIdAddress = Utils.getEdit(mEtUnderIdAddress)
        val EtCardBank = Utils.getText(mEtCardBank)
        val EtCardAcc = Utils.getEdit(mEtCardAcc)
        val EtCardNum = Utils.getEdit(mEtCardNum)
        val EtPhoneNum = Utils.getEdit(mEtPhoneNum)
        if (TextUtils.isEmpty(EtPhoneNum)) {
            showStr("请输入手机号码")
            return
        }
        if (!Utils.isPhoneNum(EtPhoneNum)) {
            showStr("请输入正确的手机号码")
            return
        }
        val EtUnderAddress = Utils.getEdit(mEtUnderAddress)
        if (TextUtils.isEmpty(EtUnderAddress)) {
            showStr("请输入养殖地址")
            return
        }
        if (!isSelf && !isTogether) {
            showStr("请选择投保方式")
            return
        }
        val EtTogetherName = Utils.getText(mEtTogetherName)
        if (isTogether && TextUtils.isEmpty(EtTogetherName)) {
            showStr("请输入集体投保名称")
            return
        }
        var catetory = ""
        var togetherName = ""
        if (isSelf) {
            catetory = "自行投保"
            togetherName = ""
        } else {
            catetory = "集体投保"
            togetherName = EtTogetherName
        }
        if (EtCardBank.isNullOrEmpty()) {
            showStr("请选择开户行")
            return
        }
        if (farmer_sign.isNullOrEmpty()) {
            showStr("请签名")
            return
        }
        val back_id: MutableMap<String, String?> = HashMap()
        val finalCatetory = catetory
        if (NetUtil.checkNet(mContext)) {
            showAvi()
            if (idCard_path.isNotEmpty())
                back_id["身份证"] = idCard_path
            if (idCard_path_back.isNotEmpty())
                back_id["身份证反面"] = idCard_path_back
            if (backCard_path.isNotEmpty())
                back_id["银行卡"] = backCard_path
            back_id["签名"] = farmer_sign.toString()
            val finalCatetory1 = catetory
            val finalTogetherName = togetherName
            val finalCatetory2 = catetory
            upLoadFile(this, EtUnderIdNums, back_id) { list ->
                var id_path: String? = null
                var id_back_path: String? = null
                var bank_path: String? = null
                var farmer_sign: String? = null
                for (item in list) {
                    if (!TextUtils.isEmpty(item) && item.contains("sfz") && !item.contains("back")) {
                        id_path = item
                    } else if (!TextUtils.isEmpty(item) && item.contains("sfzback")) {
                        id_back_path = item
                    } else if (!TextUtils.isEmpty(item) && item.contains("bank")) {
                        bank_path = item
                    } else if (!TextUtils.isEmpty(item) && item.contains("sign")) {
                        farmer_sign = item
                    }
                }
//                val empNumber = PreferUtils.getString(this, Constants.userNumber)
//                val comNumber = PreferUtils.getString(this, Constants.companyNumber)
                RequestUtil.getInstance(mContext)!!.saveFarmer(fRegionNumber, Name, category, EtUnderIdNums, EtUnderIdNums, EtUnderIdAddress, EtCardBank, EtCardAcc, EtCardNum, EtPhoneNum, finalTogetherName,
                        EtUnderAddress, finalCatetory1, TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss), id_path, bank_path, Utils.getEdit(mInputInfo),
                        TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss), id_back_path, isPoor.toString(), overTime, farmer_sign, FBankCode, FBankRelatedCode, mStartTime, natureCode, natureName, companyNum, userNum) { rtnCode, message ->
                    hideAvi()
                    showStr(message)
                    if (rtnCode >= 0) {
                        saveFarmerCache(fRegionNumber, isSelf, Name, category,
                                EtUnderIdNums, EtUnderIdNums, EtUnderIdAddress, EtCardBank, EtCardAcc,
                                EtCardNum, EtPhoneNum, togetherName, EtUnderAddress, finalCatetory, TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss), idCard_path, backCard_path, Utils.getEdit(mInputInfo), idCard_path_back, isPoor, overTime,
                                farmer_sign.toString(), FBankCode, FBankRelatedCode, mStartTime, natureCode, natureName, "1")
                        if (isGoToNextUi) {
                            if (category == "营业执照" && isTogether) {
                                showStr("村委不可验标")
                                return@saveFarmer
                            }
                            goToCamer(Name, EtUnderIdNums, EtPhoneNum, EtTogetherName, finalCatetory2, EtUnderAddress, message, "")
                        }
                    }

                }
            }
        } else {
            saveFarmerCache(fRegionNumber, isSelf, Name, category,
                    EtUnderIdNums, EtUnderIdNums, EtUnderIdAddress, EtCardBank, EtCardAcc,
                    EtCardNum, EtPhoneNum, togetherName, EtUnderAddress, finalCatetory, TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss), idCard_path, backCard_path, Utils.getEdit(mInputInfo), idCard_path_back, isPoor, overTime,
                    farmer_sign.toString(), FBankCode, FBankRelatedCode, mStartTime, natureCode, natureName, "0")
        }
    }

    /* cache.empNumber = PreferUtils.getString(this, Constants.userNumber)
                cache.comNumber = PreferUtils.getString(this, Constants.companyNumber)*/
    private fun saveFarmerCache(
            fRegionNumber: String?, isSelf: Boolean, name: String, zjCategory: String,
            Number: String, ZjNumber: String, SFZAddress: String, BankName: String, AccountName: String,
            AccountNumber: String, Mobile: String, Area: String, RaiseAddress: String,
            Category: String, CreateTime: String, ZjPicture: String, BankPicture: String, remark: String, backID: String, isPoor: Int,
            overTime: String, farmer_sign: String, FBankCode: String?, FBankRelatedCode: String?, FstartTime: String?, FEnterpriseNature: String?,
            natureName: String?, isUpLoad: String
    ) {
        where("zjNumber =?", ZjNumber).findAsync(FarmListCache::class.java).listen { list: List<FarmListCache?>? ->
            val cache = FarmListCache()
            cache.fRegionNumber = fRegionNumber
            cache.natureName = natureName
            cache.natureCode = FEnterpriseNature
            cache.isUpLoad = isUpLoad
            cache.accountName = AccountName
            cache.accountNumber = AccountNumber
            cache.raiseAddress = RaiseAddress
            cache.bankName = BankName
            cache.zjCategory = zjCategory
            cache.mobile = Mobile
            cache.name = name
            cache.number = Number
            cache.idPath = ZjPicture
            cache.bankPath = BankPicture
            cache.creatTime = CreateTime
            cache.remark = remark
            cache.zjBackPicture = backID
            cache.isPoor = isPoor.toString()
            cache.overdueTime = overTime
            cache.FBankCode = FBankCode
            cache.FBankRelatedCode = FBankRelatedCode
            cache.fStartime = FstartTime
            cache.empNumber = userNum
            cache.comNumber = companyNum

            cache.singTime = TimeUtil.getTime(Constants.yyyy_mm_dd)
            if (isSelf) {
                cache.category = Category
                cache.area = ""
            } else {
                cache.category = Category
                cache.area = Area
            }
            cache.singPic = farmer_sign
            cache.sFZAddress = SFZAddress
            if (list == null || list.isEmpty()) {
                cache.zjNumber = ZjNumber
                val save = cache.save()
                LogUtils.e("save  $save")
                if (NetUtil.checkNet(this)) return@listen
                if (save) {
                    showStr("保存成功")
                    if (isGoToNextUi) {
                        if (zjCategory == "营业执照" && isTogether) {
                            showStr("村委不可验标")
                            return@listen
                        }
                        goToCamer(name, ZjNumber, Mobile, Area, Category, RaiseAddress, Number, "")
                    }
                } else {
                    showStr("保存失败")
                }
            } else {
                // LogUtils.e("huan  cun")
                cache.upDateTime = TimeUtil.getTime(Constants.yyyy__MM__dd)

                val i = cache.updateAll("zjNumber = ?", ZjNumber)
                if (NetUtil.checkNet(this)) return@listen
                if (i > 0) {
                    showStr("保存成功")
                    if (isGoToNextUi) {
                        if (zjCategory == "营业执照" && isTogether) {
                            showStr("村委不可验标")
                            return@listen
                        }
                        goToCamer(name, ZjNumber, Mobile, Area, Category, RaiseAddress, Number, "")
                    }
                } else {
                    showStr("保存失败")
                }
            }
        }
    }

    fun goToCamer(
            Name: String?, EtUnderIdNums: String?, EtPhoneNum: String?, EtTogetherName: String?,
            finalCatetory2: String?, EtUnderAddress: String?,
            message: String?, eatTag: String?,
    ) {
        val intent = Intent(mContext, CameraActivity::class.java)
        intent.putExtra(Constants.farmer_name, Name) //养殖户i姓名
        intent.putExtra(Constants.farmer_id_nums, EtUnderIdNums)//证件号
        intent.putExtra(Constants.farmer_tel, EtPhoneNum)//电话号码
        //  intent.putExtra(Constants.category_name, EtTogetherName)//集体户名
        intent.putExtra(Constants.category, finalCatetory2) //集体投保  个人投保
        intent.putExtra(Constants.farmer_zjCategory, currentIdCate)//证件类型
        intent.putExtra(Constants.farmer_area, EtTogetherName)//集体户名
        intent.putExtra(Constants.farmer_address, EtUnderAddress)//证件地址
        intent.putExtra(Constants.Ba_num, message)//报案号
        intent.putExtra(Constants.ear_tag, eatTag)
        startActivity(intent)
    }

    override fun actionCamera() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .isCompress(true)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) return
                        val realPath = result[0].realPath
                        val getimage = ImageUtils.getimage(realPath)
                        setImage(clickImg, getimage)
                    }

                    override fun onCancel() {
                    }
                })
    }

    private fun openAlbum() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)
                .maxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: List<LocalMedia>) {
                        if (result.isNullOrEmpty()) return
                        val realPath = result[0].realPath
                        val getimage = ImageUtils.getimage(realPath)
                        setImage(clickImg, getimage)
                    }

                    override fun onCancel() {}
                })
    }

    /*打开相册*/
    override fun actionAlbum() {
        openAlbum()
    }

    fun setImage(clickImg: Int, getimage: Bitmap?) {
        if (getimage == null) return
        val name = Utils.getEdit(mEtName)
        val photoName = name.ifEmpty {
            TimeUtil.getTime(Constants.yyyy__MM__dd)
        }
        val photoPath = name.ifEmpty {
            TimeUtil.getTime(Constants.yyyyMMddHHmmss)
        }
        when (clickImg) {
            1 -> {
                hasZjPic = true
                mIvDelFont!!.visibility = View.VISIBLE
                if (getimage != null) {
                    mIvTakeIdPhoto!!.setBackgroundResource(0)
                    mIvTakeIdPhoto!!.setImageBitmap(getimage)
                    idCard_path = ImageUtils.saveBitmap(mContext, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, photoName + "/" + photoPath + "_" + "idCard" + ".jpg")
                    pathImgId = ImageUtils.getStream(idCard_path!!)
                }
            }
            2 -> {
                if (getimage != null) {
                    hasBankPic = true
                    mIvTakeBankPhoto!!.setBackgroundResource(0)
                    mIvTakeBankPhoto!!.setImageBitmap(getimage)
                    backCard_path = ImageUtils.saveBitmap(mContext, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, photoName + "/" + photoPath + "_" + "bankCard" + ".jpg")
                    pathImgBank = ImageUtils.getStream(backCard_path)
                    mDelBank!!.visibility = View.VISIBLE
                }
            }
            3 -> {
                if (getimage != null) {
                    hasZjPicBack = true
                    idCard_path_back = ImageUtils.saveBitmap(mContext, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, photoName + "/" + photoPath + "_" + "idCard_back" + ".jpg")
                    mIvTakeIdPhotoBack!!.setBackgroundResource(0)
                    mIvTakeIdPhotoBack!!.setImageBitmap(getimage)
                    pathImgIdBack = ImageUtils.getStream(idCard_path_back)
                    mIvDelBack!!.visibility = View.VISIBLE
                }
            }
        }

    }
}