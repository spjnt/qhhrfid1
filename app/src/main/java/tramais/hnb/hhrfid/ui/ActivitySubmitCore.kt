package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.itextpdf.awt.AsianFontMapper
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BadPdfFormatException
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.*
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCore
import tramais.hnb.hhrfid.interfaces.GetEmployee
import tramais.hnb.hhrfid.interfaces.GetEmployeeList
import tramais.hnb.hhrfid.interfaces.GetResult
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogCheckInfo
import tramais.hnb.hhrfid.ui.dialog.DialogChoiceEmployee
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.ShareUtils.shareWechatFriend
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ActivitySubmitCore : BaseActivity() {
    private var mNmber: TextView? = null
    private var mFarmer: TextView? = null
    private var mChecker: TextView? = null
    private var mSumitResult: TextView? = null
    private var msubmit: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_core)

    }

    var module_type: String? = null
    override fun initView() {
        fenPei = intent.getSerializableExtra("FenPei") as FenPei
        module_type = fenPei?.Tag ?: ""
        setTitleText("提交核心")
        mSumitResult = findViewById(R.id.submit_result)
        mChecker = findViewById(R.id.et_checker)
        mFarmer = findViewById(R.id.et_farmer)
        mNmber = findViewById(R.id.et_number)
        msubmit = findViewById(R.id.submit)

        mNmber?.text = fenPei?.number ?: ""
        mFarmer?.text = fenPei?.farmerName ?: ""
    }

    var fenPei: FenPei? = null
    override fun initData() {

        getChecker()
    }

    fun save(employeeNo: String) {
        if (employeeNo.isNullOrEmpty()) {
            showStr("请选择核保人")
            return
        }
        RequestUtil.getInstance(this)!!.getNXLPAppServlet(Utils.getText(mNmber), employeeNo, object : GetCore {
            override fun getCore(core: SubmitCoreBean?) {

                core?.let {
                    showStr(it.msg)

                    if (it.resCode < 0) {
                        mSumitResult!!.text = "同步结果: ${it.msg ?: ""}"+ "\n\n" + "失败原因: ${it.fErrorMsg ?: ""}"
                    } else {
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
                override fun getString(data_: EmployeeListBean.DataDTO?) {
                    data_?.let {
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
    fun getChecker() {
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