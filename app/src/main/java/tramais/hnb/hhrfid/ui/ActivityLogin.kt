package tramais.hnb.hhrfid.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.allenliu.versionchecklib.utils.ALog
import com.allenliu.versionchecklib.utils.FileHelper
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hailong.biometricprompt.fingerprint.FingerprintCallback
import com.hailong.biometricprompt.fingerprint.FingerprintVerifyManager
import org.litepal.LitePal.findAllAsync
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.HttpBean
import tramais.hnb.hhrfid.bean.UpdateAppBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetResultJsonObject
import tramais.hnb.hhrfid.interfaces.OkResponseInterface
import tramais.hnb.hhrfid.litePalBean.RoleCache
import tramais.hnb.hhrfid.net.OkhttpUtil
import tramais.hnb.hhrfid.net.Params
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.BaseDialog
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PackageUtils
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.Utils
import java.io.File


class ActivityLogin : BaseActivity() {
    private var mEtAccount: EditText? = null
    private var mIvCleanAccount: ImageView? = null
    private var mEtPsw: EditText? = null
    private var mIvCleanPsw: ImageView? = null
    private var mIvHideShowPsw: ImageView? = null

    //    private var mIvRemPass: ImageView? = null
    private var mBtnLogin: Button? = null
    private var mChangePsw: TextView? = null
    private var mForgetPsw: TextView? = null
    private val context: Context = this@ActivityLogin
    private var logo: ImageView? = null
    private var loginContent: LinearLayout? = null
    private var isShow = false

    // private var isRem = false
    private var mLlPsw: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    override fun initView() {
        hideAllTitle()
        mLlPsw = findViewById(R.id.ll_psw)
        loginContent = findViewById(R.id.login_content)
        loginContent!!.alpha = 0.0f
        logo = findViewById(R.id.logo)
        mEtAccount = findViewById(R.id.et_account)
        mIvCleanAccount = findViewById(R.id.iv_clean_account)
        mEtPsw = findViewById(R.id.et_psw)
        mIvCleanPsw = findViewById(R.id.iv_clean_psw)
        mIvHideShowPsw = findViewById(R.id.iv_hide_show_psw)
        // mIvRemPass = findViewById(R.id.iv_rem_pass)
        mBtnLogin = findViewById(R.id.btn_login)
        mChangePsw = findViewById(R.id.change_psw)
        mForgetPsw = findViewById(R.id.forget_psw)
        val options = RequestOptions.bitmapTransform(RoundedCorners(20)) //???????????????30
        Glide.with(this).load(R.mipmap.new_logo) //????????????
                .apply(options)
                .into(logo!!)
    }

    val test: MutableList<Int> = ArrayList()
    override fun initData() {
        val account = PreferUtils.getString(context, Constants.account)
        val password = PreferUtils.getString(context, Constants.password)
        //val aBoolean = PreferUtils.getBoolean(context, Constants.isRemPsw)
        if (!TextUtils.isEmpty(account)) mEtAccount!!.setText(account)
        if (!TextUtils.isEmpty(password)) mEtPsw!!.setText(password)
        // isRem = aBoolean
        //  mIvRemPass!!.isSelected = aBoolean
        val screenOn = isScreenOn(this)
        if (!account.isNullOrEmpty() && !password.isNullOrEmpty() && screenOn) {
            addFinger()
        } else {
            alpha(loginContent)
        }
        // Utils.up()
    }


    fun alpha(view: View?) {
        Handler(Looper.getMainLooper()).post {
            try {
                val animator: ObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f)
                animator.duration = 500
                animator.start()
            } catch (e: java.lang.Exception) {
                Log.e("e", e.message.toString())
            }
        }
    }

    var timer_: CountDownTimer? = null
    private fun countDown() {
        timer_ = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //    LogUtils.e("?????????" + millisUntilFinished / 1000 + "???")
                // txt.setText("?????????" + millisUntilFinished / 1000 + "???")
            }

            override fun onFinish() {
                is_btn_click = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        timer_?.cancel()
    }

    //"Username":"19924940597","Password":"picc2021!"
    private fun addFinger() {

        val builder = FingerprintVerifyManager.Builder(this@ActivityLogin)
        builder.callback(object : FingerprintCallback {
            //???????????????????????????????????? onHwUnavailable() ?????????
            override fun onHwUnavailable() {
                alpha(loginContent)
            }

            // ??????????????????????????????????????? onNoneEnrolled() ?????????
            override fun onNoneEnrolled() {
                alpha(loginContent)
            }


            override fun onSucceeded() {

                is_btn_click = false
                if (NetUtil.checkNet(context)) {
                    newVersionInfo
                } else {
                    getLoginCache(Utils.getEdit(mEtAccount), Utils.getEdit(mEtPsw))
                }
            }

            override fun onFailed() {
                alpha(loginContent)
            }

            //??????????????????????????????????????? onUsepwd() ?????????
            override fun onUsepwd() {
                alpha(loginContent)
            }


            override fun onCancel() {
                LogUtils.e("onCancle")
                alpha(loginContent)
            }
        }).enableAndroidP(true)//??????Android P??????????????????
                .usepwdVisible(true)//??????????????????
                .build()

    }

    fun goToForget(module_name: String) {
        val intent = Intent(context, ActivityForgetPsw::class.java)
        intent.putExtra(Constants.MODULE_NAME, module_name)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.ECLAIR_MR1)
    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager
        return pm.isScreenOn
    }

    var is_btn_click: Boolean = true
    override fun initListner() {
        mLlPsw!!.setOnClickListener { view ->
            if (!NetUtil.checkNet(this)) {
                showStr("???????????????????????????")
                return@setOnClickListener
            }
            Utils.goToNextUI(ActivityForgetPsw::class.java)
            //goToForget("????????????")
        }
        mBtnLogin!!.setOnClickListener { view: View? ->
            if (!is_btn_click) {
                showStr("?????????????????????")
                return@setOnClickListener
            }
            countDown()
            timer_!!.start()
            is_btn_click = false
            if (NetUtil.checkNet(context)) {
                newVersionInfo
            } else {
                getLoginCache(Utils.getEdit(mEtAccount), Utils.getEdit(mEtPsw))
            }
        }
        /*??????????????????*/mIvCleanAccount!!.setOnClickListener { view: View? ->
            mEtAccount!!.setText("")
            mEtPsw!!.setText("")
        }
        /*??????????????????*/mIvCleanPsw!!.setOnClickListener { view: View? -> mEtPsw!!.setText("") }
        /*??????????????????*/mIvHideShowPsw!!.setOnClickListener { view: View? ->
            isShow = !isShow
            mIvHideShowPsw!!.isSelected = isShow
            if (isShow) {
                mEtPsw!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                mEtPsw!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        /* mIvRemPass!!.setOnClickListener { view: View? ->
             isRem = !isRem
             mIvRemPass!!.isSelected = isRem
         }*/
    }


    private fun getLoginCache(userName: String, password: String) {
        if (TextUtils.isEmpty(userName)) {
            showStr("??????????????????")
            return
        }
        if (TextUtils.isEmpty(password)) {
            showStr("??????????????????")
            return
        }
        val et_account = PreferUtils.getString(context, Constants.account)
        val et_password = PreferUtils.getString(context, Constants.password)
        val isLogin = PreferUtils.getBoolean(context, Constants.isLogin)
        if (TextUtils.isEmpty(et_account) || TextUtils.isEmpty(et_password) || !isLogin) {
            showStr("?????????????????????")
            return
        }
        if (et_account != userName) {
            showStr("????????????")
            return
        }
        if (et_password != et_password) {
            showStr("????????????")
            return
        }
        findAllAsync(RoleCache::class.java).listen { list ->
            PreferUtils.putInt(this@ActivityLogin, Constants.current_index, 0)
            if (list != null && list.size > 0) PreferUtils.putString(context, Constants.Role_array, list[0].json)
            // showStr("????????????")
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            is_btn_click = true
            finish()
        }

    }

    private fun getLogin(userName: String, password: String) {
        if (TextUtils.isEmpty(userName)) {
            showStr("??????????????????")
            return
        }
        if (TextUtils.isEmpty(password)) {
            showStr("??????????????????")
            return
        }

        val params = Params.createParams()
        params.add("Username", userName)
        params.add("Password", password)
        showAvi()
        OkhttpUtil.getInstance(this).doGet(Config.checklogin,
                params,
                object : OkResponseInterface {
                    override fun onSuccess(bean: HttpBean, id: Int) {
                        if (bean.response.isEmpty()) return

                        instant?.praseAllMessage(bean.response.toString(), object : GetResultJsonObject {
                            override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: com.alibaba.fastjson.JSONObject?) {

                                is_btn_click = true
                                if (rtnCode >= 0 && datas != null) {
                                    val companyNumber = datas.getString("CompanyNumber")
                                    val UserNumber = datas.getString("UserNumber")
                                    val UserName = datas.getString("UserName")
                                    val fxzCode = datas.getString("FXZCode")
                                    PreferUtils.putString(context, Constants.FXZCode, fxzCode)
                                    PreferUtils.putString(context, Constants.companyNumber, companyNumber)
                                    PreferUtils.putString(context, Constants.userNumber, UserNumber)
                                    PreferUtils.putString(context, Constants.UserName, UserName)
                                    PreferUtils.putBoolean(context, Constants.isLogin, true)
                                    PreferUtils.putString(context, Constants.login_data, datas.toJSONString())
                                    PreferUtils.putString(context, Constants.account, userName)
                                    PreferUtils.putString(context, Constants.password, password)

                                    /*    PreferUtils.putBoolean(context, Constants.isRemPsw, isRem)
                                        if (isRem)*/

                                    RequestUtil.getInstance(this@ActivityLogin)!!.getRoler(userName) { rtnCode, message, totalNums, datas ->
                                        if (datas != null && datas.size > 0) {
                                            PreferUtils.putString(this@ActivityLogin, Constants.Role_array, datas.toJSONString())
                                            PreferUtils.putInt(this@ActivityLogin, Constants.current_index, 0)
                                            val intent = Intent(context, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            showStr("??????????????????")
                                        }
                                    }
                                } else {
                                    PreferUtils.putBoolean(context, Constants.isLogin, false)
                                    if (!message.isNullOrEmpty()) {
                                        showStr(message)
                                    } else {
                                        showStr("????????????")
                                    }
                                }
                            }
                        })
                        hideAvi()
                    }

                    override fun onError(e: Exception) {
                        alpha(loginContent)
                        hideAvi()
                    }
                })
    }

    private val newVersionInfo: Unit
        get() {
            verfiyAndDeleteAPK()
            val versionCode = PackageUtils.getVersionCode(this@ActivityLogin)

            val params = Params.createParams()
            OkhttpUtil.getInstance(this@ActivityLogin).doGet(Config.getversioninfo, params, object : OkResponseInterface {
                override fun onSuccess(bean: HttpBean, id: Int) {
                    val appBean = com.alibaba.fastjson.JSONObject.parseObject(bean.response, UpdateAppBean::class.java)

                    if (appBean.code >= 0 && appBean.data != null) {
                        val version = appBean.data!!.version
                        val updateMsg = appBean.data!!.updateMsg
                        val updateURL = appBean.data!!.updateURL
                        if (version != null && !TextUtils.isEmpty(version)) {
                            if (version.toFloat() > versionCode.toFloat()) {
                                upLoad(updateURL, updateMsg)
                            } else {
                                getLogin(Utils.getEdit(mEtAccount), Utils.getEdit(mEtPsw))
                            }
                        } else {
                            getLogin(Utils.getEdit(mEtAccount), Utils.getEdit(mEtPsw))
                        }
                    } else {
                        LogUtils.d("data  is  null")
                        getLogin(Utils.getEdit(mEtAccount), Utils.getEdit(mEtPsw))
                    }

                }

                override fun onError(e: Exception) {
                    getLogin(Utils.getEdit(mEtAccount), Utils.getEdit(mEtPsw))
                }
            })
        }

    private fun upLoad(url: String?, mes: String?) {
        val builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(crateUIData(url!!, mes!!))
        builder.customVersionDialogListener = CustomVersionDialogListener { context, versionBundle ->
            val baseDialog = BaseDialog(context, R.style.dialog_avi, R.layout.update_dialog)
            //versionBundle ??????UIData???????????????????????????????????????????????????UI???????????????
            val textView = baseDialog.findViewById<TextView>(R.id.version_msg)
            textView.text = versionBundle.content
            baseDialog
        }
        builder.executeMission(this@ActivityLogin)
    }

    private fun crateUIData(url: String, mes: String): UIData {
        val uiData = UIData.create()
        uiData.title = "????????????"
        uiData.downloadUrl = url
        uiData.content = mes
        return uiData
    }

    @SuppressLint("StringFormatInvalid")
    private fun verfiyAndDeleteAPK() {
        //??????versioncode???????????????????????????apk????????????????????????????????????
        val downloadPath = FileHelper.getDownloadApkCachePath() + applicationContext.getString(R.string.app_name, applicationContext.packageName)
        if (com.allenliu.versionchecklib.core.DownloadManager.checkAPKIsExists(applicationContext, downloadPath)) {
            try {
                ALog.e("??????????????????apk")
                File(downloadPath).delete()
            } catch (e: Exception) {
                LogUtils.e("E" + e.message)
            }
        }
    }


}