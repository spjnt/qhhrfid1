package tramais.hnb.hhrfid.lv

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.CheckDetail
import java.util.*
import kotlin.collections.ArrayList

class DialogEditCheckAdapter(protected var mContext: Context, var liPeiAnimalDataDTO: CheckDetail.LiPeiAnimalDataDTO?, protected var mDatas: List<String>?) : BaseAdapter() {
    var contents_four = HashMap<String, String>()
    protected var mInflater: LayoutInflater
    protected var values: MutableList<String?> = ArrayList()
    private var selectedEditTextPosition = -1
    override fun getCount(): Int {
        return mDatas!!.size
    }

    override fun getItem(position: Int): String? {
        return if (mDatas != null && position < mDatas!!.size) {
            mDatas!![position]
        } else {
            null
        }
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View? {
        var view = view
        var holder: ViewHolder? = null
        if (view == null) {
            view = mInflater.inflate(R.layout.item_edit, null)
            holder = ViewHolder()
            holder.tv_key = view.findViewById(R.id.tv_key)
            holder.et_value = view.findViewById(R.id.et_value)
            with(holder) { et_value!!.addTextChangedListener(FourChangedListener(this, contents_four)) }
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }


        //输入框  复选框 设置key
        holder.et_value!!.tag = position
        val item = mDatas!![position]
        if (item != null) {
            holder!!.tv_key!!.text = item
        }
        holder!!.et_value!!.setText(values[position])
        /* when(position){
             1-> holder!!.et_value!!.setText(liPeiAnimalDataDTO?.fLossAmount)
             2-> holder!!.et_value!!.setText(liPeiAnimalDataDTO?.fRiskPre)
             3-> holder!!.et_value!!.setText(liPeiAnimalDataDTO?.fEarNumber)
         }*/
        //输入框选中
        holder!!.et_value!!.setOnTouchListener { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_UP) {
                selectedEditTextPosition = position
            }
            false
        }
        //输入框获取焦点
        holder.et_value!!.clearFocus()
        if (selectedEditTextPosition != -1 && selectedEditTextPosition == position) {
            //强制加上焦点
            holder.et_value!!.requestFocus()
            //设置光标显示到编辑框尾部
            holder.et_value!!.setSelection(holder.et_value!!.text.length)
            //重置
            selectedEditTextPosition = -1
        }
        return view
    }

    fun getcontents(): HashMap<String, String> {
        return contents_four
    }

    class ViewHolder {
        var tv_key: TextView? = null
        var et_value: EditText? = null
    }

    inner class FourChangedListener(var holder: ViewHolder?, var contents: HashMap<String, String>?) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (holder != null && contents != null) {
                val position = holder!!.et_value!!.tag as Int
                when (position) {
                    2 -> holder!!.et_value!!.inputType = InputType.TYPE_CLASS_NUMBER
                    0,
                    1,
                    -> {
                        holder!!.et_value!!.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    }
                }
                contents!![holder!!.tv_key!!.text.toString()] = editable.toString()
            }
        }
    }

    init {
        mInflater = LayoutInflater.from(mContext)
        contents_four.containsKey("")
        values = mutableListOf(liPeiAnimalDataDTO?.fLossAmount
                ?: "", liPeiAnimalDataDTO?.fRiskPre ?: "", liPeiAnimalDataDTO?.fEarNumber
                ?: "")

    }
}