package tramais.hnb.hhrfid.ui.popu

import android.annotation.SuppressLint
import android.app.Activity
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.ColorChoiceBean
import tramais.hnb.hhrfid.interfaces.GetCommon

class PopColorChoice(val context: Activity,val rootView: View, private val title: String,val data: List<ColorChoiceBean>,val getRoom: GetCommon<ColorChoiceBean>) : PopupWindow(), View.OnClickListener {
    private var listView: RecyclerView? = null
    private var inflate: View? = null
    private var tv_confim: TextView? = null
    private var tv_title: TextView? = null
    private var lp: WindowManager.LayoutParams? = null
    private fun initView(context: Activity, data: List<ColorChoiceBean>) {
        val inflater = LayoutInflater.from(context)
        inflate = inflater.inflate(R.layout.color_choice, null)
        listView = inflate!!.findViewById(R.id.color_choice)
        listView!!.layoutManager = LinearLayoutManager(context)
        initAdapter()
        mAdapter!!.setList(data)
        // listView!!.setAdapter(ArrayAdapter(context, R.layout.item_room, data))
        tv_confim = inflate!!.findViewById(R.id.tv_confim)
        tv_title = inflate!!.findViewById(R.id.tv_title)
        tv_title!!.text = title
        tv_confim!!.setOnClickListener(this)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            getRoom.getCommon(mAdapter!!.getItem(position)!!)
            dismiss()
        }
    }

    var mAdapter: BaseQuickAdapter<ColorChoiceBean?, BaseViewHolder>? = null
    fun initAdapter() {
        listView!!.adapter = object : BaseQuickAdapter<ColorChoiceBean?, BaseViewHolder>(R.layout.item_color) {
            override fun convert(holder: BaseViewHolder, item: ColorChoiceBean?) {
                holder.setText(R.id.tv_color, item!!.colorStr)
                val colorBg = holder.getView<TextView>(R.id.tv_color_bg)
                colorBg.setBackgroundColor(context.resources.getColor(item.colorInt))
            }
        }.also { mAdapter = it }
    }

    /**
     * ???????????????????????????
     */
    @SuppressLint("InlinedApi", "ClickableViewAccessibility")
    private fun setPopupWindow(context: Activity) {
        this.contentView = inflate // ??????View
        lp = context.window.attributes
        lp!!.alpha = 0.4f
        context.window.attributes = lp
        val height_ = context.resources.displayMetrics.heightPixels
        this.width = ViewGroup.LayoutParams.MATCH_PARENT // ????????????????????????
        this.height = height_ / 2 // ????????????????????????
        // ??????SelectPicPopupWindow?????????????????????
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.isTouchable = true
        this.animationStyle = R.style.PopMenuAnimation // ????????????
        showAtLocation(rootView, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        inflate!!.setOnTouchListener { v, event ->
            // ??????????????????????????????????????????
            val height = inflate!!.findViewById<View>(R.id.id_pop_layout).top
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_confim -> dismiss()
        }
    }

    private fun disMiss() {
        lp!!.alpha = 1f
        context.window.attributes = lp
    }

    override fun dismiss() {
        super.dismiss()
        disMiss()
    }

    init {
        initView(context, data)
        setPopupWindow(context)
    }
}