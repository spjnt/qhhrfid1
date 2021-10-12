package tramais.hnb.hhrfid.lv

import android.content.Context
import android.widget.ImageView
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R

class ImageAdapterNew(var mContext: Context) : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_img) {
    private var pics: MutableList<String>? = ArrayList()
    fun setGroupList(pics: MutableList<String>?) {
        this.pics = pics

        setNewInstance(pics as MutableList<String?>)
        notifyDataSetChanged()
    }

    open fun getPics(): MutableList<String>? {
        return pics
    }

    fun parentItemCount(): Int {
        return if (pics == null) 0 else pics!!.size
    }

    override fun convert(holder: BaseViewHolder, item: String?) {
        val view = holder.getView<ImageView>(R.id.iv_photos)
        Glide.with(mContext).load(item).into(view)
    }

    open fun deleteImage(position: Int) {
        pics!!.removeAt(position)
        notifyDataSetChanged()
    }

    open fun addImages(pic: MutableList<String>?) {
        if (pic != null) {
            pics?.addAll(pic)
            setNewInstance(pics as MutableList<String?>)
            notifyDataSetChanged()
        }
    }
}