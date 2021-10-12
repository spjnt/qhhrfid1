package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.ChaKanLandsBean
import tramais.hnb.hhrfid.interfaces.GetMutableMap
import tramais.hnb.hhrfid.lv.DialogEditSunShiAdapter
import kotlin.collections.HashMap

class DialogEditSunShi(context: Context, var names: MutableList<String>, var bean: ChaKanLandsBean.Data1DTO.Data2DTO, var getLinkList: GetMutableMap) : Dialog(context, R.style.dialog) {
    private var title: TextView? = null
    private var titleHead: TextView? = null
    private var lvContent: ListView? = null
    private var cancle: TextView? = null
    private var confim: TextView? = null
    private val values:MutableMap<String,String> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit)
        title = findViewById<View>(R.id.title) as TextView
        titleHead = findViewById<View>(R.id.title_head) as TextView
        lvContent = findViewById<View>(R.id.lv_content) as ListView
        cancle = findViewById(R.id.cancle)
        confim = findViewById(R.id.confim)
        values[names[0]]=bean?.fSquare?: "0.0"
        values[names[1]]=bean?.fRiskQty?: "0.0"
        values[names[2]]=bean?.fLossQty?: "0.0"



        setAdapter(names, values)
        title!!.text = "损失程度"
        titleHead!!.text = bean.fLandName
        cancle!!.setOnClickListener { v -> dismiss() }
        confim!!.setOnClickListener { v: View? ->
            val getcontents = adapter!!.getcontents()
            getLinkList.getList(getcontents)
            dismiss()

        }
    }

    var adapter: DialogEditSunShiAdapter? = null

    private fun setAdapter(names: List<String>, values:MutableMap<String,String> ) {
        if (names.isEmpty()) return

        lvContent!!.adapter = DialogEditSunShiAdapter(context, names, values).also { adapter = it }
    }

}