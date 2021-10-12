package tramais.hnb.hhrfid.ui.fragment

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.*
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
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.ChoicePhoto
import tramais.hnb.hhrfid.interfaces.GetBankDetail
import tramais.hnb.hhrfid.interfaces.GetBankInfo
import tramais.hnb.hhrfid.interfaces.GetResultJsonObject
import tramais.hnb.hhrfid.litePalBean.BankInfoCache
import tramais.hnb.hhrfid.litePalBean.ChaKanDetailListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.net.SaveAllCache
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.ui.ActivitySign
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceBank
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.Utils.getEdit

class FragmentSign : BaseFragment(), ChoicePhoto {
    lateinit var rootView: View
    private lateinit var mCleanSign: Button
    private lateinit var mIvTakeBankPhoto: ImageView
    private lateinit var mDelBank: ImageView
    private lateinit var mEtCardBank: TextView
    private lateinit var mEtCardAcc: TextView
    private lateinit var mEtCardNum: EditText
    private lateinit var mSave: Button
    lateinit var fenPei: FenPei
    private lateinit var mIvSign: ImageView
    private lateinit var mIvUploadCard: TextView
    override fun initImmersionBar() {

    }

    override fun findViewById(view: View?) {

        view?.let {
            mCleanSign = it.findViewById(R.id.to_sign)
            mSave = it.findViewById(R.id.save)
            mIvSign = it.findViewById(R.id.iv_sign)

            mIvTakeBankPhoto = it.findViewById(R.id.iv_take_bank_photo)
            mDelBank = it.findViewById(R.id.del_bank)
            mEtCardBank = it.findViewById(R.id.et_card_bank)
            mEtCardAcc = it.findViewById(R.id.et_card_acc)
            mEtCardNum = it.findViewById(R.id.et_card_num)
            mIvUploadCard = it.findViewById(R.id.iv_upload_card)

            // getBasic()

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_sign
    }

    var hasBackPic: Boolean = false
    var FBankCode: String? = null
    var FBankRelatedCode: String? = null
    var AccountName: String? = null
    override fun initListener() {
        mEtCardBank.setOnClickListener {
            if (getBankResulData == null || getBankResulData!!.isEmpty()) {
                "暂无银行卡数据".showStr()
                return@setOnClickListener
            }
            /*
            * public string FBankCode { get; set; } //如  银行英文缩写，如ICBC
        public string FBankRelatedCode { get; set; } // 银行行联号，每个支行唯一，如105338003002
    public string AccountName { get; set; }   //如 中国农业银行股份有限公司西宁市七一路支行*/
            DialogChoiceBank(requireContext(), getBankResulData, object : GetBankDetail {
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
            Glide.with(requireActivity()).load(R.mipmap.bank_cark).into(mIvTakeBankPhoto)
            //  mIvTakeBankPhoto!!.setBackgroundResource(0)
            hasBackPic = false
        }
        mCleanSign.setOnClickListener {
            var intent = Intent(context, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "持卡人签名")
            intent.putExtra(Constants.Sign_Common, fenPei.farmerNumber)
            intent.putExtra(Constants.Type, "养殖")
            startActivityForResult(intent, Config.REQUEST_SING)

        }
        mSave.setOnClickListener { regist() }
        /*银行卡拍照*/

        mIvTakeBankPhoto.setOnClickListener { view: View? ->
            if (!hasBackPic)
                PopuChoicePicture(activity, this).showAtLocation(mIvTakeBankPhoto, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            else {
                val dialogImg = DialogImg(requireContext(), backCard_path)
                if (!dialogImg.isShowing) dialogImg.show()
            }
        }
        mIvUploadCard.setOnClickListener { view: View? ->
            if (NetUtil.checkNet(context)) {
                if (!backCard_path.isNullOrEmpty())
                    RequestUtil.getInstance(context)!!.getBankInfo(ImageUtils.getStream(backCard_path), object : GetResultJsonObject {
                        override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {
                            if (datas != null) {
                                val BankAccount = datas.getString("BankAccount")
                                val BankName = datas.getString("BankName")
                                val BankCardType = datas.getString("BankCardType")
                                val msg = datas.getString("Msg")
                                backCard_path = datas.getString("PicUrl")
                                msg.showStr()
                                activity!!.runOnUiThread {
                                    mEtCardNum.setText(BankAccount)
                                 //   mEtCardBank.text = BankName
                                }
                            }
                        }
                    })
            } else {
                "请确保网络连接".showStr()
            }
        }
    }

    override fun initData() {
        fenPei = ActivityFeedCheck.fenPei
        getChaKanDetail(fenPei.number)
        getBankInfo("")
    }

    var getBankResulData: MutableList<BankInfoDetail.GetBankResulDataDTO?>? = ArrayList()
    fun getBankInfo(bankCode: String) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getBankInfoDetail(bankCode, object : GetBankInfo {
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
            "请签名".showStr()
            return
        }
        val cardBank = Utils.getText(mEtCardBank)//开户行
        if (cardBank.isNullOrEmpty()) {
            "请输入开户行".showStr()
            return
        }
        val card_acc = Utils.getText(mEtCardAcc)
        if (card_acc.isNullOrEmpty()) {
            "请输入开户名".showStr()
            return
        }
        val card_num = getEdit(mEtCardNum)

        if (card_num.isNullOrEmpty()) {
            "请输入银行卡号".showStr()
            return
        }
        /*if (!path.isNullOrEmpty()) {*/

        if (NetUtil.checkNet(context)) {
            showAvi()

            sing.clear()
            signature.toString().also { sing["签名"] = it }
            sing["银行卡"] = backCard_path.toString()
            UpLoadFileUtil.upLoadFile(context, fenPei.number, sing) { list ->
                var sing_: String? = null
                var bank_: String? = null
                if (list.size == sing.size) {
                    for (item in list) {
                        if (item.contains("sign"))
                            sing_ = item
                        else bank_ = item
                    }
                    RequestUtil.getInstance(context)!!.saveChakanSign(fenPei.number, sing_, Utils.getText(mEtCardBank),
                            Utils.getText(mEtCardAcc), getEdit(mEtCardNum), bank_, FBankCode, FBankRelatedCode, fenPei.farmerNumber) { rtn, str ->
                        str.showStr()
                        hideAvi()
                    }
                }


            }

        } else {

            SaveAllCache.SAVE_ALL_CACHE.saveCheck_sign(fenPei, signature.toString(),
                    Utils.getText(mEtCardBank), Utils.getText(mEtCardAcc), getEdit(mEtCardNum), backCard_path.toString()) { str -> str.showStr() }


        }

    }

    fun getChaKanDetail(number: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getChaKanDetail(number) { detail ->

                detail?.let {

                    FBankRelatedCode = it.fBankRelatedCode ?: fenPei.FBankRelatedCode
                    FBankCode = it.fBankCode ?: fenPei.FBankCode
                    requireActivity()!!.runOnUiThread {
                        if (detail.bankName.isNullOrEmpty())
                            mEtCardBank.text = fenPei.fBank
                        else
                            mEtCardBank.text = detail.bankName

                        if (detail.bankAccount.isNullOrEmpty())
                            mEtCardAcc.text = fenPei.farmerName
                        else
                            mEtCardAcc.text = detail.accountName

                        if (detail.accountName.isNullOrEmpty())
                            mEtCardNum.setText(fenPei.fAccountNo)
                        else
                            mEtCardNum.setText(detail.bankAccount)
                        if (detail.fFarmerSign.isNullOrEmpty())
                            signature = fenPei?.FSignPicture ?: ""
                        else
                            signature = detail.fFarmerSign

                        backCard_path = detail.bankPicture
                        if (!backCard_path.isNullOrEmpty()) {
                            hasBackPic = true
                            mDelBank.visibility = View.VISIBLE
                            context?.let { Glide.with(it).load(backCard_path).into(mIvTakeBankPhoto) }
                        }

                        if (!signature.isNullOrEmpty()) {
                            context?.let { Glide.with(it).load(signature).into(mIvSign) }
                        }
                    }
                }

            }
        } else {
            LitePal.where("BaoAnNumber =?", fenPei.number).findAsync(ChaKanDetailListCache::class.java).listen { list ->
                if (list != null && list.size > 0) {
                    val chaKanCache = list[0]
                    mEtCardBank.text = chaKanCache.bankName
                    mEtCardAcc.text = chaKanCache.bankAccount
                    mEtCardNum.setText(chaKanCache.accountName)
                    signature = chaKanCache.employeeSign
                    backCard_path = chaKanCache.bankPicture
                    if (!backCard_path.isNullOrEmpty()) {
                        mDelBank.visibility = View.VISIBLE
                        context?.let { Glide.with(it).load(backCard_path).into(mIvTakeBankPhoto) }
                    }

                    if (signature != null) {

                        context?.let { Glide.with(it).load(signature).into(mIvSign) }
                    }
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
                            backCard_path = ImageUtils.saveBitmap(context, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, fenPei.farmerName + "/" + fenPei.farmerName + "_" + "bankCard" + ".jpg")
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
                .selectionMode(PictureConfig.SINGLE)
                .maxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) return
                        val realPath = result[0].realPath
                        val getimage = ImageUtils.getimage(realPath)
                        //压缩图片并显示

                        if (getimage != null) {
                            backCard_path = ImageUtils.saveBitmap(context, getimage, FileUtil.getSDPath() + Constants.sdk_middle_path, fenPei.farmerName + "/" + fenPei.farmerName + "_" + "bankCard" + ".jpg")
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
                Glide.with(requireContext()).load(_sign).into(mIvSign)
            }
        }
    }
}