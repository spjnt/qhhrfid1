package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.interfaces.GetOneString

class DialogEarTags(context: Context, var earTags: MutableList<String>, var getEarTag: GetOneString) : Dialog(context, R.style.dialog) {
    private var mLvContent: RecyclerView? = null
    private var etSearch: EditText? = null
    private var mAdapter: QuickAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_choice_eartag)
        mLvContent = findViewById(R.id.lv_content)
        etSearch = findViewById<View>(R.id.et_search) as EditText

        mAdapter = QuickAdapter()

        mLvContent!!.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ColorDrawable(Color.GRAY))
        mLvContent!!.addItemDecoration(divider)
        mLvContent!!.adapter = mAdapter
        mAdapter!!.setList(earTags)
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val filter = earTags.filter { it.contains(s.toString()) }
                if (filter == null || filter.isEmpty()) {
                    Toast.makeText(context, "暂无匹配数据", Toast.LENGTH_LONG).show()
                }
                mAdapter!!.setNewInstance(filter as MutableList<String?>)
            }
        })
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->

                val item = it.getItem(position)
                getEarTag.getString(item)
                dismiss()
            }
        }
    }

    class QuickAdapter : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_text_left) {
        override fun convert(holder: BaseViewHolder, item: String?) {
            item?.let { farmer_ ->
                holder.setText(R.id.tv, farmer_)
            }
        }
    }


}