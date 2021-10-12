package tramais.hnb.hhrfid.lv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yanzhenjie.recyclerview.ExpandableAdapter
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.bean.CheckDetail

class ExpandedBankAdapter(context: Context?) : ExpandableAdapter<ExpandableAdapter.ViewHolder?>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mGroupList: MutableList<BankInfoDetail.GetBankResulDataDTO?>? = ArrayList()
    fun setGroupList(groupList: MutableList<BankInfoDetail.GetBankResulDataDTO?>?) {

        mGroupList = groupList
        notifyDataSetChanged()
    }
    open fun getData(): MutableList<BankInfoDetail.GetBankResulDataDTO?>? {
        return mGroupList
    }
    override fun parentItemCount(): Int {
        return if (mGroupList == null) 0 else mGroupList!!.size
    }

    override fun childItemCount(parentPosition: Int): Int {
        val liPeiAnimalPicData = mGroupList!![parentPosition]!!.getBankResultDetaiData
        return if (liPeiAnimalPicData == null) 0 else liPeiAnimalPicData!!.size
    }

    override fun createParentHolder(root: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_parent_expand_bank, root, false)
        return ParentHolder(view, this)
    }

    override fun createChildHolder(root: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_text_left, root, false)
        return ChildHolder(view, this)
    }

    override fun bindParentHolder(holder: ViewHolder, position: Int) {
        (holder as ParentHolder).setData(mGroupList!![position])
    }

    override fun bindChildHolder(holder: ViewHolder, parentPosition: Int, position: Int) {
        mGroupList!![parentPosition]!!.getBankResultDetaiData?.get(position)?.let { (holder as ChildHolder).setData(it) }
    }

    internal class ParentHolder(itemView: View, adapter: ExpandableAdapter<*>?) : ExpandableAdapter.ViewHolder(itemView, adapter) {
        var mTvEarTag: TextView = itemView.findViewById(R.id.tv_title)

        fun setData(data: BankInfoDetail.GetBankResulDataDTO?) {
            data?.let {
                mTvEarTag.text = data.fBackName
            }
        }
    }

    internal class ChildHolder(itemView: View, adapter: ExpandableAdapter<*>?) : ExpandableAdapter.ViewHolder(itemView, adapter) {
        var mTvTitle: TextView = itemView.findViewById(R.id.tv)
        fun setData(data: BankInfoDetail.GetBankResulDataDTO.GetBankResultDetaiDataDTO) {
            mTvTitle.text = data.fBankDetailName
        }
    }
}