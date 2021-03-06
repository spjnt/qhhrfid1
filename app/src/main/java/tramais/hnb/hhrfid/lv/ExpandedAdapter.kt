package tramais.hnb.hhrfid.lv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.yanzhenjie.recyclerview.ExpandableAdapter
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.CheckDetail

open class ExpandedAdapter(context: Context?) : ExpandableAdapter<ExpandableAdapter.ViewHolder?>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mGroupList: MutableList<CheckDetail.LiPeiAnimalDataDTO?>? = ArrayList()
    fun setGroupList(groupList: MutableList<CheckDetail.LiPeiAnimalDataDTO?>?) {
        mGroupList = groupList
        notifyDataSetChanged()
    }

    override fun parentItemCount(): Int {
        return if (mGroupList == null) 0 else mGroupList!!.size
    }

    override fun childItemCount(parentPosition: Int): Int {
        val liPeiAnimalPicData = mGroupList!![parentPosition]!!.liPeiAnimalPicData
        return liPeiAnimalPicData?.size ?: 0
    }

    override fun createParentHolder(root: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_parent_expand, root, false)
        return ParentHolder(view, this)
    }

    override fun createChildHolder(root: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_img, root, false)
        return ChildHolder(view, this)
    }

    override fun bindParentHolder(holder: ViewHolder, position: Int) {
        (holder as ParentHolder).setData(mGroupList!![position], position)
    }

    override fun bindChildHolder(holder: ViewHolder, parentPosition: Int, position: Int) {
        (holder as ChildHolder).setData(mGroupList!![parentPosition]!!.liPeiAnimalPicData!![position])
    }

    internal class ParentHolder(itemView: View, adapter: ExpandableAdapter<*>?) : ExpandableAdapter.ViewHolder(itemView, adapter) {
        var mTvEarTag: TextView = itemView.findViewById(R.id.tv_ear_tag)
        var mIvStatus: TextView = itemView.findViewById(R.id.tv_detail)
        var mTvComTotal: TextView = itemView.findViewById(R.id.tv_com_total)
        var mTvComRate: TextView = itemView.findViewById(R.id.tv_com_rate)
        fun setData(data: CheckDetail.LiPeiAnimalDataDTO?, position: Int) {
            data?.let {
                val tagInfo = if (data.fEarNumber.isNullOrBlank())
                    position + 1
                else (position + 1).toString() + " -- " + data.fEarNumber
                mTvEarTag.text = "??????: $tagInfo"
                mIvStatus.isSelected = data.isExpanded
                mTvComTotal.text = "????????????:  ${data.fLossAmount ?: "0.0"}"
                mTvComRate.text = "????????????:  ${data.fRiskPre ?: "0.0"} " + "%"
            }

        }

    }

    internal class ChildHolder(itemView: View, adapter: ExpandableAdapter<*>?) : ExpandableAdapter.ViewHolder(itemView, adapter) {
        var mTvTitle: ImageView = itemView.findViewById(R.id.iv_photos)

        //        var mIvDelete: ImageView = itemView.findViewById(R.id.iv_delete)
        fun setData(data: CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO) {
            Glide.with(itemView).load(data.picUrl).into(mTvTitle)
        }

    }


    open fun removeAt(@IntRange(from = 0) position: Int) {
        if (position >= mGroupList!!.size) {
            return
        }
        this.mGroupList!!.removeAt(position)

        notifyItemRemoved(position)
        notifyDataSetChanged()
        notifyItemRangeChanged(position, this.mGroupList!!.size - position)
    }

    private fun compatibilityDataSizeChanged(size: Int) {
        if (this.mGroupList!!.size == size) {
            notifyDataSetChanged()
        }
    }

    open fun upDateData(@IntRange(from = 0) position: Int, data: CheckDetail.LiPeiAnimalDataDTO) {
      //  LogUtils.e("data  ${Gson().toJson(data)}")
        this.mGroupList!![position]!!.fRiskPre = data.fRiskPre
        this.mGroupList!![position]!!.fLossAmount = data.fLossAmount
        this.mGroupList!![position]!!.fEarNumber = data.fEarNumber
        this.mGroupList!![position]!!.liPeiAnimalPicData = data.liPeiAnimalPicData
        notifyDataSetChanged()
    }


    open fun addData(@NonNull newData: MutableList<CheckDetail.LiPeiAnimalDataDTO?>) {
        this.mGroupList!!.addAll(newData)
//        notifyItemRangeInserted(this.mGroupList!!.size - newData.size, newData.size)
//        compatibilityDataSizeChanged(newData.size)
        notifyDataSetChanged()
    }


    open fun getData(): MutableList<CheckDetail.LiPeiAnimalDataDTO?>? {
        return mGroupList
    }

}