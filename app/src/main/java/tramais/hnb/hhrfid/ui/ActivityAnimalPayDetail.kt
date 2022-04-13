package tramais.hnb.hhrfid.ui

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_pay_detail.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.SystemListBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import java.io.IOException
import java.net.URL
import java.util.*

class ActivityAnimalPayDetail : BaseActivity() {
    private var mSynResult: TextView? = null
    private var mBaoNum: TextView? = null
    private var mBaNum: TextView? = null
    private var mPayMethod: TextView? = null
    private var mPayTime: TextView? = null
    private var mPayMuch: TextView? = null
    private var mShowQrcode: Button? = null
    private var mShowPayResult: Button? = null
    private var mPayCancle: Button? = null
    private var insure_num: String? = null
    private var ivQrcode: ImageView? = null
    private var mPayStatu: TextView? = null
    var pay_methods: MutableList<String> = ArrayList()
    var map_method: MutableMap<String, String>? = HashMap()
    private var statu: String? = null
    private var type: String? = null
    private var mTips: TextView? = null
    private var llBottom: LinearLayout? = null
    private var llBu: LinearLayout? = null
    private var moneyDetail: LinearLayout? = null
    private var govMuch: TextView? = null
    private var nationalMoney: TextView? = null
    private var proviceMoney: TextView? = null
    private var cityMoney: TextView? = null
    private var counteyMoney: TextView? = null
    private var shareQrCode: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_detail)
    }

    override fun initView() {
        setTitleText("同步缴费")
        shareQrCode = findViewById(R.id.tv_share_qrcode)
        llBu = findViewById(R.id.ll_bu)
        mTips = findViewById(R.id.tips)
        llBottom = findViewById(R.id.ll_bottom)
        mSynResult = findViewById(R.id.syn_result)
        mBaoNum = findViewById(R.id.bao_num)
        mBaNum = findViewById(R.id.ba_num)
        mPayMethod = findViewById(R.id.pay_method)
        mPayMuch = findViewById(R.id.pay_much)
        mShowQrcode = findViewById(R.id.show_qrcode)
        mShowPayResult = findViewById(R.id.show_pay_result)
        ivQrcode = findViewById(R.id.iv_qrcode)
        mPayTime = findViewById(R.id.pay_time)
        mPayStatu = findViewById(R.id.pay_statu)
        mPayCancle = findViewById(R.id.pay_cancle)
        mPayCancle!!.visibility = GONE
        govMuch = findViewById(R.id.gov_much)
        moneyDetail = findViewById(R.id.money_detail)
        nationalMoney = findViewById(R.id.national_money)
        proviceMoney = findViewById(R.id.provice_money)
        cityMoney = findViewById(R.id.city_money)
        counteyMoney = findViewById(R.id.country_money)
        val transition = LayoutTransition()
        moneyDetail!!.layoutTransition = transition
        if (ifNmg())
            llBu!!.visibility = GONE
    }

    override fun initData() {
        val intent = intent
        if (intent != null) {
            insure_num = intent.getStringExtra(Constants.Ba_num)
            statu = intent.getStringExtra(Constants.Statu)
            type = intent.getStringExtra(Constants.Type)
            if (statu == "已缴费") llBottom!!.visibility = View.GONE
            // insure_num ="IN2021042700150";
        }
        //&& statu.equals("同步")
        if (!TextUtils.isEmpty(statu)) {
            showAvi()
            RequestUtil.getInstance(this)!!.WapGsIListImportServlet(type.toString(), insure_num) { json: JSONObject? ->
                hideAvi()

                var msg: String? = null
                var piccProPosalNo: String? = null
                var piccPolicyNo: String? = null
                var FPayStatus: String? = null
                var nationbdwf: String? = null //中央保费费率
                var provincedwbf: String? = null//省级保费费率
                var citydwbf: String? = null//市级保费费率
                var countydwbf: String? = null//县级保费费率
                var FSubsidies: String? = null//财政补贴
                if (json != null) {
                    if (json.containsKey("msg")) {
                        msg = json.getString("msg")
                    }
                    if (json.containsKey("piccProPosalNo")) {
                        piccProPosalNo = json.getString("piccProPosalNo")
                    }
                    if (json.containsKey("piccPolicyNo")) {
                        piccPolicyNo = json.getString("piccPolicyNo")
                    }
                    if (json.containsKey("FPayStatus")) {
                        FPayStatus = json.getString("FPayStatus")
                    }
                    if (json.containsKey("FNationbdwf")) {
                        nationbdwf = json.getString("FNationbdwf")
                    }
                    if (json.containsKey("FProvincedwbf")) {
                        provincedwbf = json.getString("FProvincedwbf")
                    }
                    if (json.containsKey("FCitydwbf")) {
                        citydwbf = json.getString("FCitydwbf")
                    }
                    if (json.containsKey("FCountydwbf")) {
                        countydwbf = json.getString("FCountydwbf")
                    }
                    if (json.containsKey("FSubsidies")) {
                        FSubsidies = json.getString("FSubsidies")
                    }
                    showStr(msg)

                    runOnUiThread {
                        mSynResult!!.text = msg
                        mBaoNum!!.text = piccProPosalNo
                        mBaNum!!.text = piccPolicyNo
                        mPayStatu!!.text = FPayStatus

                        nationalMoney!!.text = nationbdwf
                        proviceMoney!!.text = provincedwbf
                        cityMoney!!.text = citydwbf
                        counteyMoney!!.text = countydwbf
                        govMuch!!.text = FSubsidies
                    }
                }
            }
        }
        payMethod
    }

    var codeurl: String? = null
    var isShowDetail: Boolean = false
    override fun initListner() {
        shareQrCode!!.setOnClickListener {
//            var file = FileUtil.getSDPath() + Constants.sdk_qr
//            var qr = "qr"
            if (codeurl.isNullOrBlank()) return@setOnClickListener
            donwloadImg(this)
        }
        govMuch!!.setOnClickListener {
            isShowDetail = !isShowDetail
            if (isShowDetail)
                moneyDetail!!.visibility = View.VISIBLE
            else moneyDetail!!.visibility = View.GONE
        }
        mPayCancle!!.setOnClickListener { v: View? ->
            RequestUtil.getInstance(this)!!.PayBillCancel(type, insure_num) { json: JSONObject ->
                var resMessage: String? = ""
                var resCode: String? = ""
                if (json.containsKey("resCode")) {
                    resCode = json.getString("resCode")
                }
                if (json.containsKey("resMessage")) {
                    resMessage = json.getString("resMessage")
                }
//                LogUtils.e("resCode $resCode")
                if (resCode == "1") {
                    mShowQrcode!!.visibility = View.VISIBLE
                    mShowPayResult!!.visibility = View.VISIBLE
                    ivQrcode!!.setImageDrawable(null)
                    mPayTime!!.text = ""
                }
                showStr(resMessage)
            }
        }
        mShowQrcode!!.setOnClickListener { v: View? ->
            if (map_method == null || map_method!!.isEmpty()) {
                showStr("暂无支付方式")
                return@setOnClickListener
            }
            showAvi()
            RequestUtil.getInstance(this)!!.PayBill(type, insure_num, map_method!![Utils.getText(mPayMethod)]) { json: JSONObject ->
                hideAvi()
                var resMessage: String? = null
                var resCode: String? = null
                var sumfee: String? = null

                var payfailuretime: String? = null
                if (json.containsKey("resCode")) {
                    resCode = json.getString("resCode")
                }
                if (json.containsKey("resMessage")) {
                    resMessage = json.getString("resMessage")
                }
                if (json.containsKey("sumfee")) {
                    sumfee = json.getString("sumfee")
                }
                if (json.containsKey("codeurl")) {
                    codeurl = json.getString("codeurl")

                }
                if (json.containsKey("payfailuretime")) {
                    payfailuretime = json.getString("payfailuretime")
                }
                showStr(resMessage)
                if (resCode == "1") {
                    shareQrCode!!.visibility = View.VISIBLE
                    mPayCancle!!.visibility = View.VISIBLE
                }
                runOnUiThread {
                    mPayMuch!!.text = sumfee
                    Glide.with(this@ActivityAnimalPayDetail).load(codeurl).into(ivQrcode!!)
                    if ((resCode == "1")) mPayTime!!.text = "付款有效时间至：$payfailuretime"
                }
            }
        }
        mShowPayResult!!.setOnClickListener { v: View? ->
            showAvi()
            RequestUtil.getInstance(this)!!.PayBillStatus(type, insure_num) { json: JSONObject ->
                hideAvi()
                var resMessage: String? = null
                var paystatus: String? = null
                if (json.containsKey("resMessage")) {
                    resMessage = json.getString("resMessage")
                }
                if (json.containsKey("paystatus")) {
                    paystatus = json.getString("paystatus")
                }
                showStr(resMessage)

                runOnUiThread { mPayStatu!!.text = resMessage }
            }
        }
        mPayMethod!!.setOnClickListener { v: View? ->
            PopuChoice(this@ActivityAnimalPayDetail, mPayMethod, "请选择支付方式", pay_methods) { str: String ->
                if (str.contains("其它")) {
                    mShowQrcode!!.visibility = View.GONE
                } else {
                    mShowQrcode!!.visibility = View.VISIBLE
                }
                mPayMethod!!.text = str
            }
        }
    }

    private val payMethod: Unit
        private get() {
            RequestUtil.getInstance(this)!!.getSystemList("支付方式") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                if (datas != null && datas.size > 0) {
                    pay_methods.clear()
                    val touBaoBeans: List<SystemListBean>? = instant!!.parseCommonUseArr(datas, SystemListBean::class.java)
                    if (touBaoBeans != null && touBaoBeans.size > 0) for (item: SystemListBean in touBaoBeans) {
                        val values: String = item.values
                        map_method!!.put(values, item.item)
                        if (!TextUtils.isEmpty(values) && !pay_methods.contains(values)) {
                            pay_methods.add(values)
                        }
                    }
                }
            }
        }

    private var context: Context? = null

    //    private var bannerName: String? = null
    private var mBitmap: Bitmap? = null
    private var mSaveMessage = "失败"

    //    private static ProgressDialog mSaveDialog = null;
    private val messageHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                2 -> {
                    val s = msg.obj as String?
                    LogUtils.e("s  $s")
                    if (s!!.contains("成功"))
                        ShareUtils.shareWechatFriend(this@ActivityAnimalPayDetail, FileUtil.getSDPath() + Constants.sdk_qr + "qr.jpg")
                }
            }

        }
    }
    private val saveFileRunnable = Runnable {
        try {
            if (!TextUtils.isEmpty(codeurl)) { //网络图片
                // 对资源链接
                val url = URL(codeurl)
                //打开输入流
                val inputStream = url.openStream()
                //对网上资源进行下载转换位图图片
                mBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
            ImageUtils.saveBitmap(this@ActivityAnimalPayDetail, mBitmap, FileUtil.getSDPath() + Constants.sdk_qr, "qr" + ".jpg")
            mSaveMessage = "图片保存成功"
        } catch (e: IOException) {
            mSaveMessage = "图片保存失败"
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val message = Message()
        message.obj = mSaveMessage
        message.what = 2
        messageHandler!!.sendMessage(message)
    }

    fun donwloadImg(contexts: Context?) {
        context = contexts
        Thread(saveFileRunnable).start()
    }
}