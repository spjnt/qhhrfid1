package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.CommonAdapter
import tramais.hnb.hhrfid.bean.EmployeeListBean
import tramais.hnb.hhrfid.interfaces.GetEmployee
import tramais.hnb.hhrfid.lv.ViewHolder
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant

class DialogChoiceEmployee(context: Context, var employees: MutableList<EmployeeListBean.DataDTO?>?, var getEmployee: GetEmployee) : Dialog(context, R.style.dialog) {
    private var mLvContent: ListView? = null
    private var etSearch: EditText? = null
    private var allAreas: MutableList<EmployeeListBean.DataDTO?>? = ArrayList()
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    allAreas = msg.obj as MutableList<EmployeeListBean.DataDTO?>
                    setAdapter(allAreas)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_choice_gy)
        mLvContent = findViewById(R.id.lv_content)
        etSearch = findViewById<View>(R.id.et_search) as EditText
        etSearch!!.hint = "请输入员工姓名或工号"
        employees?.let { allAreas!!.addAll(it) }
        setAdapter(allAreas)
        //   getEmployee("")
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                getEmployee(s.toString())
            }
        })
        mLvContent!!.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            if (getEmployee != null) {
                if (allAreas?.get(position) != null
                ) {
                    getEmployee.getString(allAreas!![position])
                } else {
                    LogUtils.e("bean is  null")
                }
            } else {
                LogUtils.e("interface is  null")
            }

            dismiss()
        }
    }

    private fun getEmployee(filter: String) {
        val filter1 = employees!!.filter { it!!.employeeName!!.contains(filter)||it!!.employeeNo!!.contains(filter) }
        if (filter1 != null && filter1.isNotEmpty()) {
            val msg = Message()
            msg.obj = filter1
            msg.what = 0
            handler.sendMessage(msg)
        } else {
            Toast.makeText(context, "暂无数据展示", Toast.LENGTH_LONG).show()
            mLvContent!!.adapter = null
        }

    }

    private fun setAdapter(areas: MutableList<EmployeeListBean.DataDTO?>?) {
        if (areas!!.isEmpty()) return
        mLvContent!!.adapter = object : CommonAdapter<EmployeeListBean.DataDTO?>(context, areas, R.layout.item_text) {
            override fun convert(helper: ViewHolder, item: EmployeeListBean.DataDTO?) {
                val textView = helper.getView<TextView>(R.id.tv)
                textView.text = item!!.employeeName+"("+item.employeeNo+")"
            }
        }
    }
}