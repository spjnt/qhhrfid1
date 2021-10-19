package tramais.hnb.hhrfid.waterimage

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R


/**
 * 自定义水印view
 * 支持设置logo、公司名称、相关信息
 */
class WaterMaskView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        RelativeLayout(context, attrs) {

    private val recyleView: RecyclerView
    private var rlLeft: RecyclerView
    private var tvLocation: TextView
    private var llBottom: LinearLayout

    /*
    * 操作员
    * */

    private var crrators: MutableList<String> = ArrayList()

    /**
     * 背景水印
     */
    private var height_: Float = 0f
    private var width_: Float = 0f
    fun setBackData(crrators: MutableList<String>, height: Float, width: Float) {
        this.crrators = crrators
        this.height_ = height
        this.width_ = width
        setBackAdapter(height, width_)
        mAdapter!!.addData(crrators)
        mAdapter!!.notifyDataSetChanged()

    }

    private var infos: MutableList<String> = ArrayList()
    fun setLeftData(infos: MutableList<String>, height: Float) {
        this.infos = infos
        setLeftAdapter(height, infos)
        mLeftAdapter!!.addData(infos)
        mLeftAdapter!!.notifyDataSetChanged()

    }

    var location_: String = ""

    fun setLocation(location: String) {
        // LogUtils.e("add  $location")
        this.location_ = location
        if (location_.isNullOrEmpty()) {
            tvLocation.text = "无法定位"
        } else {
            var one_length = 14
            var length = location.length
            if (location.length >= 14) {
                val first = location!!.substring(0, one_length)
                val end = location!!.substring(one_length, length)
                tvLocation.text = ":$first\n$end"
            } else {
                tvLocation.text = ":$location_"
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.watermask, this, true)
        tvLocation = findViewById(R.id.tv_location)
        rlLeft = findViewById(R.id.rl_left)
        recyleView = findViewById(R.id.recyle_view)
        llBottom = findViewById(R.id.ll_bottom)
        recyleView!!.layoutManager = GridLayoutManager(context, 2)
        rlLeft.layoutManager = LinearLayoutManager(context)
    }


    var mAdapter: BaseQuickAdapter<String?, BaseViewHolder>? = null
    fun setBackAdapter(height: Float, width: Float) {
        recyleView.adapter =
                object : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_roat_text) {
                    override fun convert(holder: BaseViewHolder, item: String?) {
                        val view = holder.getView<TextView>(R.id.bac)
                        val layoutParams = view.layoutParams
                        layoutParams.height = (height / 5).toInt()
                        layoutParams.width = (width / 2).toInt()
                        view.layoutParams = layoutParams
                        view.text = "操作员：$item"
                    }
                }.also { mAdapter = it }
    }


    var mLeftAdapter: BaseQuickAdapter<String?, BaseViewHolder>? = null
    fun setLeftAdapter(height: Float, infos: MutableList<String>) {
        rlLeft.adapter =
                object : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_text_small) {
                    override fun convert(holder: BaseViewHolder, item: String?) {
                        val view = holder.getView<TextView>(R.id.tv)

//                        val layoutParams = view.layoutParams
//                        layoutParams.height = (height / (3 * infos.size)).toInt()
//
//                        view.layoutParams = layoutParams
//                        view.textSize = 3f
                        view.text = item
                    }
                }.also { mLeftAdapter = it }
    }
}