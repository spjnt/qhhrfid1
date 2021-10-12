package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.interfaces.GetHashMap
import tramais.hnb.hhrfid.lv.DialogEditAdapter
import tramais.hnb.hhrfid.ui.view.CustemTitle
import java.util.*

class DialogEdit(context: Context, var title_: String, var names: MutableList<String>, var getList: GetHashMap) : Dialog(context, R.style.dialog) {
    private var title: TextView? = null
    private var titleHead: TextView? = null
    private var lvContent: ListView? = null
    private var cancle: TextView? = null
    private var confim: TextView? = null
    private val values = LinkedList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit)
        title = findViewById<View>(R.id.title) as TextView
        titleHead = findViewById<View>(R.id.title_head) as TextView
        lvContent = findViewById<View>(R.id.lv_content) as ListView
        cancle = findViewById(R.id.cancle)
        confim = findViewById(R.id.confim)
        names.remove("减产率(%)")
        setAdapter(names)
        title!!.text = title_
        if (title_ == "修改参数") {
            titleHead!!.text = ""
        }
        confim!!.setOnClickListener { v: View? ->
            val getcontents = adapter!!.getcontents()
            getList.getMap(getcontents)
            dismiss()

        }
        cancle!!.setOnClickListener { dismiss() }
    }

    var adapter: DialogEditAdapter? = null

    private fun setAdapter(names: List<String>) {
        if (names.isEmpty()) return

        lvContent!!.adapter = DialogEditAdapter(context, names).also { adapter = it }
    }

}