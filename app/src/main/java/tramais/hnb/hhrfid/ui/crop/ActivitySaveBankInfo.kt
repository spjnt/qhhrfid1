package tramais.hnb.hhrfid.ui.crop

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.CropFarmList
import tramais.hnb.hhrfid.bean.ResultBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.*
import tramais.hnb.hhrfid.litePalBean.BankInfoCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivitySign
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceBank
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.util.*

class ActivitySaveBankInfo : BaseActivity(), ChoicePhoto {
    private lateinit var mCleanSign: Button
    private lateinit var mIvTakeBankPhoto: ImageView
    private lateinit var mDelBank: ImageView
    private lateinit var mEtCardBank: TextView
    private lateinit var mEtCardAcc: TextView
    private lateinit var mEtCardNum: EditText
    private lateinit var mSave: Button

    private lateinit var mIvSign: ImageView
    private lateinit var mIvUploadCard: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sign)
    }

    override fun initView() {
        setTitleText("银行卡信息")
        mCleanSign = findViewById(R.id.to_sign)
        mSave = findViewById(R.id.save)
        mIvSign = findViewById(R.id.iv_sign)

        mIvTakeBankPhoto = findViewById(R.id.iv_take_bank_photo)
        mDelBank = findViewById(R.id.del_bank)
        mEtCardBank = findViewById(R.id.et_card_bank)
        mEtCardAcc = findViewById(R.id.et_card_acc)
        mEtCardNum = findViewById(R.id.et_card_num)
        mIvUploadCard = findViewById(R.id.iv_upload_card)
    }


    var hasBackPic: Boolean = false
    var FBankCode: String? = null
    var FBankRelatedCode: String? = null
    var AccountName: String? = null
    override fun initListner() {
        mEtCardBank.setOnClickListener {
            if (getBankResulData == null || getBankResulData!!.isEmpty()) {
                showStr("暂无银行卡数据")
                return@setOnClickListener
            }
            /*
            * public string FBankCode { get; set; } //如  银行英文缩写，如ICBC
        public string FBankRelatedCode { get; set; } // 银行行联号，每个支行唯一，如105338003002
    public string AccountName { get; set; }   //如 中国农业银行股份有限公司西宁市七一路支行*/
            DialogChoiceBank(this, getBankResulData, object : GetBankDetail {
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
        mDelBank.setOnClickListener {
            mDelBank.visibility = View.GONE
            backCard_path = ""
            Glide.with(this).load(R.mipmap.bank_cark).into(mIvTakeBankPhoto)
            //  mIvTakeBankPhoto!!.setBackgroundResource(0)
            hasBackPic = false
        }
        mCleanSign.setOnClickListener {
            var intent = Intent(this, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "持卡人签名")
            intent.putExtra(Constants.Sign_Common, dataDTO?.number)
            intent.putExtra(Constants.Type, "养殖")
            startActivityForResult(intent, Config.REQUEST_SING)

        }
        mSave.setOnClickListener { regist() }
        /*银行卡拍照*/

        mIvTakeBankPhoto.setOnClickListener { view: View? ->
            if (!hasBackPic)
                PopuChoicePicture(this, this).showAtLocation(mIvTakeBankPhoto, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            else {
                val dialogImg = DialogImg(this, backCard_path)
                if (!dialogImg.isShowing) dialogImg.show()
            }
        }
        mIvUploadCard.setOnClickListener { view: View? ->
            if (NetUtil.checkNet(this)) {
                if (!backCard_path.isNullOrEmpty())
                    RequestUtil.getInstance(this)!!.getBankInfo(ImageUtils.getStream(backCard_path), object : GetResultJsonObject {
                        override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                            if (datas != null) {
                                val BankAccount = datas.getString("BankAccount")
                                val BankName = datas.getString("BankName")
                                val BankCardType = datas.getString("BankCardType")
                                val msg = datas.getString("Msg")
                                backCard_path = datas.getString("PicUrl")
                                showStr(msg)
                                runOnUiThread {
                                    mEtCardNum.setText(BankAccount)
                                   // mEtCardBank.text = BankName
                                }
                            }
                        }
                    })
            } else {
                showStr("请确保网络连接")
            }
        }
    }

    var dataDTO: CropFarmList.DataDTO? = null
    override fun initData() {
        dataDTO = intent.getSerializableExtra("dto") as CropFarmList.DataDTO
        runOnUiThread {
            /*{"CompanyNumber":"63222100","Number":"632221196906231913","Name":"包来有","Mobile":"13619702956",
            "BankName":"工商","AccountNumber":"6222021702057518995","BankPicture":"",
            "SignPicture":"http://111.44.133.34:6050/photo/2021-07-06/sign632221196906231913-17-02-01-1.jpg",
            "FValidate":null,"BankRelatedCode":"","IsEffective":"0"}*/
            dataDTO?.let {
                mEtCardAcc.text = it.name
                mEtCardBank.text = it.bankName
                mEtCardNum.setText(it.accountNumber)
                backCard_path = it.bankPicture
                FBankRelatedCode = it.bankRelatedCode
                if (!backCard_path.isNullOrEmpty()) {
                    hasBackPic = true
                    mDelBank.visibility = View.VISIBLE
                    Glide.with(this).load(backCard_path).into(mIvTakeBankPhoto)
                }
                signature = it.signPicture
                if (!signature.isNullOrEmpty()) {
                    Glide.with(this).load(signature).into(mIvSign)
                }
            }


        }
        getBankInfo("")
    }


    var getBankResulData: MutableList<BankInfoDetail.GetBankResulDataDTO?>? = ArrayList()
    fun getBankInfo(bankCode: String) {
        if (NetUtil.checkNet(this)) {
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

    val sing: MutableMap<String, String?> = HashMap()
    fun regist() {
        if (signature.isNullOrEmpty()) {
            showStr("请签名")
            return
        }
        val cardBank = Utils.getText(mEtCardBank)//开户行
        if (cardBank.isNullOrEmpty()) {
            showStr("请输入开户行")
            return
        }
        val card_acc = Utils.getText(mEtCardAcc)
        if (card_acc.isNullOrEmpty()) {
            showStr("请输入开户名")
            return
        }
        val card_num = Utils.getEdit(mEtCardNum)

        if (card_num.isNullOrEmpty()) {
            showStr("请输入银行卡号")
            return
        }
        /*if (!path.isNullOrEmpty()) {*/

        if (NetUtil.checkNet(this)) {
            showAvi()

            sing.clear()
            signature.toString().also { sing["签名"] = it }
            sing["银行卡"] = backCard_path.toString()
            UpLoadFileUtil.upLoadFile(this, "", sing) { list ->
                var sing_: String? = null
                var bank_: String? = null
                if (list.size == sing.size) {
                    for (item in list) {
                        if (item.contains("sign"))
                            sing_ = item
                        else bank_ = item
                    }
                    RequestUtil.getInstance(this)!!.saveCropBankInfo(dataDTO?.number, card_acc, dataDTO?.mobile, cardBank,
                            card_num, bank_, sing_, FBankRelatedCode, dataDTO?.isEffective, "", object : GetResult {
                        override fun getResult(result: ResultBean?) {
                            showStr(result?.msg)
                            hideAvi()
                        }
                    })
                    /*  RequestUtil.getInstance(this)!!.saveChakanSign("fenPei.number", sing_, Utils.getText(mEtCardBank),
                              Utils.getText(mEtCardAcc), Utils.getEdit(mEtCardNum), bank_, FBankCode, FBankRelatedCode, dataDTO?.number) { rtn, str ->
                          showStr(str)
                          hideAvi()
                      }*/
                }


            }

        }

    }


    var signature: String? = null

    var backCard_path: String? = null


    override fun actionAlbum() {

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
                        if (getimage != null) {
                            mIvTakeBankPhoto.setImageBitmap(null)
                            mIvTakeBankPhoto.setImageBitmap(getimage)
                            backCard_path = ImageUtils.saveBitmap(this@ActivitySaveBankInfo, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, dataDTO?.name + "/" + dataDTO?.name + "_" + "bankCard" + ".jpg")
                            mDelBank.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancel() {}
                })


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
                        //压缩图片并显示

                        if (getimage != null) {
                            backCard_path = ImageUtils.saveBitmap(this@ActivitySaveBankInfo, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, dataDTO?.name + "/" + dataDTO?.name + "_" + "bankCard" + ".jpg")
                            mIvTakeBankPhoto.setImageBitmap(null)
                            mIvTakeBankPhoto.setImageBitmap(getimage)
                            mDelBank.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancel() {
                    }
                })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == Config.REQUEST_SING) {
            var _sign = data!!.getStringExtra(Constants.Sign_Path)
            if (!_sign.isNullOrEmpty()) {
                //  hasBackPic = true
                signature = _sign
                Glide.with(this@ActivitySaveBankInfo).load(_sign).into(mIvSign)
            }
        }
    }
}