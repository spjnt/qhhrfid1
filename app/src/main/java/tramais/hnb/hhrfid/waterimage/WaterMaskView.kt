package tramais.hnb.hhrfid.waterimage

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
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
    private var ivLocation: ImageView
    private var llBottom: LinearLayout
    private var ivPicc: ImageView

    /*
    * 操作员
    * */

    private var crrators: MutableList<String> = ArrayList()

    /**
     * 背景水印
     */
    private var max_width: Float = 1080f
    private var middle_width: Float = 860f
    private var min_width: Float = 640f
    private var height_: Float = 0f
    private var width_: Float = 0f
    fun setBackData(creators: MutableList<String>, height: Float, width: Float) {
        this.crrators = creators
        this.height_ = height
        this.width_ = width
        setBackAdapter(height, width_)
        mAdapter!!.addData(creators)
        // mAdapter!!.notifyDataSetChanged()
        //  LogUtils.e("width  $width")

        if (width >= max_width) { //>=1080
            //  tvLocation.textSize = 16.0f
            setPicLogo(400)
            setLocation(50)
        } else if (width < max_width && width >= middle_width) {//  >=860 <1080
            setPicLogo(300)
            setLocation(40)
            // tvLocation.textSize = 12.0f
        } else if (width < middle_width && width >= min_width) { // >=640 <860
            setPicLogo(200)
            setLocation(30)
            // tvLocation.textSize = 8.0f
        } else {  //<640
            setPicLogo(100)
            setLocation(20)
            // tvLocation.textSize = 6.0f
        }

    }

    /*PICClogo大小设置*/
    private fun setPicLogo(width: Int) {
        val picc_layoutParams = ivPicc.layoutParams
        picc_layoutParams.width = width
        picc_layoutParams.height = width / 4
        ivPicc.layoutParams = picc_layoutParams
    }

    /*定位图片设置*/
    private fun setLocation(width: Int) {
        val layoutParams = ivLocation.layoutParams
        layoutParams.width = (width*0.6).toInt()
        layoutParams.height = (width*0.6).toInt()
        ivLocation.layoutParams = layoutParams
    }


    private var infos: MutableList<String> = ArrayList()
    fun setLeftData(infos: MutableList<String>, width: Float) {
        this.infos = infos
        setLeftAdapter(width)
        mLeftAdapter!!.addData(infos)
    }

    var location_: String? = null

    @SuppressLint("SetTextI18n")
    fun setLocation(location: String?, width: Int) {
        this.location_ = location
        if (location_.isNullOrEmpty()) {
            tvLocation.text = "无法定位"
        } else {
            tvLocation.text = ":${location.toString()}"
            val layoutParams = tvLocation.layoutParams
            layoutParams.width = (width*0.95).toInt()
            tvLocation.layoutParams = layoutParams
            /*  val rep_len = 15
              val loc_buff = StringBuffer(location_!!)
              val len = location_!!.length
              var insert: StringBuffer? = null
              if (len > rep_len) {
                  for (item in 1..(len / rep_len)) {
                      insert = loc_buff.insert(rep_len * item, "\n")
                  }
              } else {
                  insert = StringBuffer()
                  insert.append(location_)
              }
              tvLocation.text = ":${insert.toString()}"*/
        }

    }

    init {
        LayoutInflater.from(context).inflate(R.layout.watermask, this, true)
        ivPicc = findViewById(R.id.iv_picc)
        tvLocation = findViewById(R.id.tv_location)
        rlLeft = findViewById(R.id.rl_left)
        recyleView = findViewById(R.id.recyle_view)
        llBottom = findViewById(R.id.ll_bottom)
        ivLocation = findViewById(R.id.iv_location)
        recyleView.layoutManager = GridLayoutManager(context, 2)
        rlLeft.layoutManager = LinearLayoutManager(context)
    }


    var mAdapter: BaseQuickAdapter<String?, BaseViewHolder>? = null
    private fun setBackAdapter(height: Float, width: Float) {
        recyleView.adapter =
                object : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_roat_text) {
                    override fun convert(holder: BaseViewHolder, item: String?) {
                        val view = holder.getView<TextView>(R.id.bac)
                        val layoutParams = view.layoutParams
                        layoutParams.height = (height / 5).toInt()
                        layoutParams.width = (width / 2).toInt()
                        //  LogUtils.e("width  $width")
                        // if (width >= max_width) { //>1080
                        // view.textSize = 12.0f
                        // } else if (width < max_width && width >= middle_width) {//  >860 <1080
                        // view.textSize = 10.0f
                        // } else if (width < middle_width && width >= min_width) { // >640 <860
                        // view.textSize = 8.0f
                        // } else {  //<640
                        // view.textSize = 4.0f
                        // }
                        view.layoutParams = layoutParams
                        view.text = "操作员：$item"
                    }
                }.also { mAdapter = it }
    }


    var mLeftAdapter: BaseQuickAdapter<String?, BaseViewHolder>? = null
    fun setLeftAdapter(width: Float) {
        val layoutParams = rlLeft.layoutParams
        layoutParams.width = (width*0.95).toInt()
        rlLeft.layoutParams = layoutParams
        rlLeft.adapter = object : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_text_small) {
            override fun convert(holder: BaseViewHolder, item: String?) {
                val view = holder.getView<TextView>(R.id.tv)
                /* if (width >= max_width) { //>1080
                     view.textSize = 16.0f
                 } else if (width <= max_width && width > middle_width) {//  >860 <1080
                     view.textSize = 12.0f
                 } else if (width <= middle_width && width > min_width) { // >640 <860
                     view.textSize = 8.0f
                 } else {
                     view.textSize = 6.0f
                 }*/
                view.text = item
            }
        }.also { mLeftAdapter = it }
    }
}