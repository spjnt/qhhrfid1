package tramais.hnb.hhrfid.ui

import android.annotation.SuppressLint
import android.os.*
import android.view.View
import android.widget.*
import com.itextpdf.text.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.interfaces.GetCore
import tramais.hnb.hhrfid.interfaces.GetEmployee
import tramais.hnb.hhrfid.interfaces.GetEmployeeList
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceEmployee
import tramais.hnb.hhrfid.util.*
import java.util.*

class ActivitySubmitCore : BaseActivity() {
    private var mNmber: TextView? = null
    private var mFarmer: TextView? = null
    private var mChecker: TextView? = null
    private var mSumitResult: TextView? = null
    private var mLlchecker: LinearLayout? = null
    private var mComNumber: TextView? = null
    private var mClaimNum: TextView? = null
    private var msubmit: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_core)
    }

    var module_type: String? = null
    var status: String? = null

    override fun initView() {
        fenPei = intent.getSerializableExtra("FenPei") as FenPei
        module_type = fenPei?.Tag ?: ""
        setTitleText("提交核心")
        status = fenPei?.status
        mLlchecker= findViewById(R.id.ll_checker)
        mSumitResult = findViewById(R.id.submit_result)
        mChecker = findViewById(R.id.et_checker)
        mFarmer = findViewById(R.id.et_farmer)
        mNmber = findViewById(R.id.et_number)
        msubmit = findViewById(R.id.submit)
        mComNumber = findViewById(R.id.tv_com_num)
        mClaimNum = findViewById(R.id.tv_claim_no)
        mNmber?.text = fenPei?.number ?: ""
        mFarmer?.text = fenPei?.farmerName ?: ""
        mComNumber?.text = fenPei?.FCompensateNo ?: "待提交"
        mClaimNum?.text = fenPei?.FClaimNo ?: "待提交"
        if (status == "同步") {
            mLlchecker!!.visibility = View.GONE
            msubmit!!.visibility = View.GONE
            mSumitResult!!.visibility = View.GONE
        }
    }

    var fenPei: FenPei? = null
    override fun initData() {
        getChecker()
    }

    fun save(employeeNo: String) {
        if (employeeNo.isEmpty()) {
            showStr("请选择核保人")
            return
        }
        RequestUtil.getInstance(this)!!.getNXLPAppServlet(Utils.getText(mNmber), employeeNo, object : GetCore {
            @SuppressLint("SetTextI18n")
            override fun getCore(core: SubmitCoreBean?) {
                core?.let {
                    showStr(it.msg)
                    if (it.resCode < 0) {
                        mSumitResult!!.text = "同步结果: ${it.msg ?: ""}" + "\n\n" + "失败原因: ${it.fErrorMsg ?: ""}"
                        mComNumber?.text = "同步失败"
                        mClaimNum?.text = "同步失败"
                    } else {
                        mComNumber?.text = it.fCompensateNo
                        mClaimNum?.text = it.fClaimNo
                        mSumitResult!!.text = "同步结果: ${it.msg}" + "\n\n" + "立案号: ${it.fClaimNo ?: ""}" + "\n\n" + "计算书号: ${it.fCompensateNo ?: ""}" + "\n\n" + "批单号: ${it.fEndorseNo ?: ""}"
                    }
                }
            }
        })
    }

    var userName_: String = ""
    var userNumber: String = ""
    override fun initListner() {
        mChecker!!.setOnClickListener {
            if (employeeListBeans == null || employeeListBeans!!.size == 0) {
                showStr("暂无查勘员可选")
                return@setOnClickListener
            }
            val dialogChoiceEmployee = DialogChoiceEmployee(this, employeeListBeans, object : GetEmployee {
                override fun getString(str: EmployeeListBean.DataDTO?) {
                    str?.let {
                        userName_ = it.employeeName.toString()
                        userNumber = it.employeeNo.toString()
                        runOnUiThread { mChecker!!.text = userName_ }
                    }
                }
            })
            if (!dialogChoiceEmployee.isShowing && !isFinishing) dialogChoiceEmployee.show()
        }
        msubmit!!.setOnClickListener { save(userNumber) }
    }

    private var employeeListBeans: MutableList<EmployeeListBean.DataDTO?>? = ArrayList()
    private fun getChecker() {
        employeeListBeans!!.clear()
        RequestUtil.getInstance(this)!!.getEmplyeListnNew("", "核保", object : GetEmployeeList {
            override fun getEmployee(infoDetail: EmployeeListBean?) {
                infoDetail?.let {
                    if (it.data != null && it.data!!.isNotEmpty()) {
                        employeeListBeans!!.addAll(it.data!!)
                    }
                }
            }
        })
    }
}