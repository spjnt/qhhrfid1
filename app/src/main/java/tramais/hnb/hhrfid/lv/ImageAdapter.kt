package tramais.hnb.hhrfid.lv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tramais.hnb.hhrfid.R

/*
 * ...
 * */
class ImageAdapter(private val mContext: Context, private val mDatas: MutableList<String>) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>(), View.OnClickListener,View.OnLongClickListener {
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var mItemClickListener: OnItemClickListener? = null
    private var mLongClickListener: OnLongClickListener? = null
    //创建ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.item_img, parent, false)
        val viewHolder: MyViewHolder = MyViewHolder(view)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return viewHolder
    }

    //绑定ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(mContext).load(mDatas[position]).into(holder.iv_photos)
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        //Log.i("TAG", "mDatas "+mDatas);
        return mDatas.size
    }

    //新增item
    fun addData(pos: Int) {
        notifyItemInserted(pos)
    }

    //移除item
    fun deleateData(pos: Int) {
        mDatas.removeAt(pos)
      notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        if (mItemClickListener != null) {
            mItemClickListener!!.onItemClick(v!!.tag as Int)
        }
    }
    override fun onLongClick(v: View?): Boolean {
        if (mLongClickListener != null) {
            mLongClickListener!!.onItemClick(v!!.tag as Int)
        }
      return false
    }
    fun setItemClickListener(itemClickListener: OnItemClickListener?) {
        mItemClickListener = itemClickListener
    }

    fun setOnLongClickListener(mLongClickListener: OnLongClickListener?) {
        this.mLongClickListener = mLongClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    interface OnLongClickListener {
        fun onItemClick(position: Int)
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_photos: ImageView = itemView.findViewById(R.id.iv_photos)

    }


}