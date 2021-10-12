package tramais.hnb.hhrfid.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.Utils
import java.util.regex.Pattern

class ActivityForgetPsw : BaseActivity() {
    private var mTvGetVerCode: TextView? = null
    private var mTvPhone: TextView? = null
    private var mTvVerCode: TextView? = null
    private var mRlVerCode: RelativeLayout? = null
    private var mEtVerCode: EditText? = null
    private var mEtPhoneNum: EditText? = null
    private var mEtNewPsw: EditText? = null
    private var mEtNewPswConfim: EditText? = null
    private var mBtnChange: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_psw)
    }

    override fun initView() {
        setTitleText("重置密码")
        mTvPhone = findViewById(R.id.tv_phone)
        mTvVerCode = findViewById(R.id.tv_ver_code)
        mRlVerCode = findViewById(R.id.rl_ver_code)
        mTvGetVerCode = findViewById(R.id.tv_get_ver_code)
        mEtPhoneNum = findViewById(R.id.et_phone_num)
        mEtVerCode = findViewById(R.id.et_ver_code)
        mEtNewPswConfim = findViewById(R.id.et_new_psw_confim)
        mEtNewPsw = findViewById(R.id.et_new_psw)
        mBtnChange = findViewById(R.id.btn_change)
    }

    // var module_name: String? = null
    override fun initData() {

        //  module_name = intent.getStringExtra(Constants.MODULE_NAME)
        //  setTitleText(module_name)
        /*if (module_name == "修改密码") {
            mTvPhone!!.text = this@ActivityForgetPsw.getText(R.string.old_psw)
            mEtPhoneNum!!.hint = this@ActivityForgetPsw.getText(R.string.old_psw_hint)
            mTvVerCode!!.visibility = View.GONE
            mRlVerCode!!.visibility = View.GONE
        } else {
            mTvPhone!!.text = this@ActivityForgetPsw.getText(R.string.phone_num)
            mEtPhoneNum!!.hint = this@ActivityForgetPsw.getText(R.string.phone_num_hint)
            mTvVerCode!!.visibility = View.VISIBLE
            mRlVerCode!!.visibility = View.VISIBLE
            countDown()
        }*/
    }

    override fun initListner() {
        mTvGetVerCode!!.setOnClickListener {
            if (!NetUtil.checkNet(this)) {
                showStr("请在联网环境下操作")
                return@setOnClickListener
            }

            if (isClick) {
                timer_?.start()

                if (checkPhoneNumber(Utils.getEdit(mEtPhoneNum)))
                    RequestUtil.getInstance(this)!!.getVerCode(Utils.getEdit(mEtPhoneNum)) {
                        showStr(it.getString("Msg"))
                    }
                else {
                    showStr("请输入正确的手机号码")
                    return@setOnClickListener
                }
            }


        }
        mBtnChange!!.setOnClickListener {
            if (!NetUtil.checkNet(this)) {
                showStr("请在联网环境下操作")
                return@setOnClickListener
            }
            var phone_number = Utils.getEdit(mEtPhoneNum)
            var ver_code = Utils.getEdit(mEtVerCode)
            if (ver_code.isEmpty()) {
                showStr("请输入验证码")
                return@setOnClickListener
            }
            if (ver_code.length < 6) {
                showStr("请输入六位数字验证码")
                return@setOnClickListener
            }
            if (checkPhoneNumber(phone_number)) {
                RequestUtil.getInstance(this)!!.judgeVerCode(phone_number, Utils.getEdit(mEtVerCode)) {
                    val code = it.getInteger("Code")
                    if (code >= 0) {
                        resetPsw(phone_number, Utils.getEdit(mEtNewPsw), Utils.getEdit(mEtNewPswConfim))
                    } else {
                        showStr(it.getString("Msg"))
                    }
                }
            } else {
                showStr("请输入正确的手机号码")
                return@setOnClickListener
            }
        }
    }

    fun checkPhoneNumber(phone_number: String): Boolean {
        return phone_number.isNotEmpty() && phone_number.length == 11
    }

    fun resetPsw(phone_number: String, newPsw: String, newPswConfim: String) {
        if (newPsw.isEmpty()) {
            showStr("请输入新密码")
            return
        }
        if (newPswConfim.isEmpty()) {
            showStr("请确认密码")
            return
        }
        if (newPsw != newPswConfim) {
            showStr("两次输入密码不一致")
            return
        }
        //190664
        if (checkPhoneNumber(phone_number))
            if (checkPassWord(newPsw)) {
                RequestUtil.getInstance(this)!!.resetPsw(phone_number, newPsw) {
                    showStr(it.getString("Msg"))
                }
            } else {
                showStr("请输入正确的手机号码")
                return
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer_ != null) {
            timer_?.cancel()
            timer_ = null
        }

    }

    var isClick = true
    var timer_: CountDownTimer? = null
    fun countDown() {
        timer_ = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread { mTvGetVerCode?.text = "${(millisUntilFinished / 1000)}s 后重新获取" }
                isClick = false
            }

            override fun onFinish() {
                isClick = true
                mTvGetVerCode?.text = this@ActivityForgetPsw.getText(R.string.get_verification_code_again)
            }
        }
    }

    fun checkPassWord(psw: String?): Boolean {
        //数字
        var REG_NUMBER: String = ".*\\d+.*"
        //小写字母
        var REG_UPPERCASE: String = ".*[A-Za-z]+.*"
        //大写字母
        // var REG_LOWERCASE: String = ".*[a-z]+.*"
        //特殊字符
        var REG_SYMBOL: String = ".*[~!@#$%^&*()_+|<>,.?/:;]+.*"

        var UN_CON: String = ".*['\\[\\]{}\"]+.*"
        if (psw!!.isEmpty() || psw.length < 8 || psw.length > 16) {
            showStr("请输入8-16位长度的密码")
            return false
        }
        if (psw.matches(Regex(UN_CON))) {
            showStr("密码不可包含引号空格等特殊字符")
            return false
        }
        var i = 0
        if (psw.matches(Regex(REG_NUMBER)))
            i++
        //|| psw.matches(Regex(REG_LOWERCASE))
        if (psw.matches(Regex(REG_UPPERCASE)))
            i++
        if (psw.matches(Regex(REG_SYMBOL)))
            i++
        return if (i >= 2)
            true
        else {
            showStr("至少包含特殊字符或数字或字母的两种")
            false
        }

    }

    /**
     * 判断是否包含特殊字符
     * @return  false:未包含 true：包含
     */
    fun inputJudge(editText: String?): Boolean {
        val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
        // val pattern: Pattern = Pattern.compile(speChat)
        // val matcher: Matcher = pattern.matcher(editText)
        return Pattern.compile(speChat).matcher(editText).find()
    }

}
