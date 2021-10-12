package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.CheckDetail
import tramais.hnb.hhrfid.interfaces.GetHashMap
import tramais.hnb.hhrfid.lv.DialogEditCheckAdapter
import tramais.hnb.hhrfid.ui.view.CustemTitle
import java.util.*

class DialogEditCheck(context: Context, var title_: String, var index:Int,var names: MutableList<String>, var liPeiAnimalDataDTO: CheckDetail.LiPeiAnimalDataDTO?, var getList: GetHashMap) : Dialog(context, R.style.dialog) {
    private var title: TextView? = null
    private var titleHead: TextView? = null
    private var lvContent: ListView? = null
    private var cancle: TextView? = null
    private var confim: TextView? = null
    private val values = LinkedList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit)
        title = findViewById(R.id.title)
        titleHead = findViewById(R.id.title_head)
        lvContent = findViewById(R.id.lv_content)
        cancle = findViewById(R.id.cancle)
        confim = findViewById(R.id.confim)
        setAdapter(names)
        title!!.text = title_
        titleHead!!.text = "序号:${index+1}"
        confim!!.setOnClickListener { v: View? ->
            val getcontents = adapter!!.getcontents()
            getList.getMap(getcontents)
            dismiss()

        }
        cancle!!.setOnClickListener { dismiss() }
    }

    var adapter: DialogEditCheckAdapter? = null

    private fun setAdapter(names: List<String>) {
        if (names.isEmpty()) return

        lvContent!!.adapter = DialogEditCheckAdapter(context, liPeiAnimalDataDTO, names).also { adapter = it }
    }

}