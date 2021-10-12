package tramais.hnb.hhrfid.lv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IntRange
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.yanzhenjie.recyclerview.ExpandableAdapter
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.ChaKanLandsBean

import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.util.Utils
import java.text.DecimalFormat

import kotlin.collections.ArrayList

class ExpandedCropAdapter(context: Context?) : ExpandableAdapter<ExpandableAdapter.ViewHolder?>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mGroupList: MutableList<ChaKanLandsBean.Data1DTO?>? = ArrayList()
    fun setGroupList(groupList: MutableList<ChaKanLandsBean.Data1DTO?>?) {
        mGroupList = groupList

        notifyDataSetChanged()
    }

    open fun getData(): MutableList<ChaKanLandsBean.Data1DTO?>? {

        return mGroupList
    }

    override fun parentItemCount(): Int {
        return if (mGroupList == null) 0 else mGroupList!!.size
    }

    override fun childItemCount(parentPosition: Int): Int {
        val liPeiAnimalPicData = mGroupList!![parentPosition]!!.data2
        return if (liPeiAnimalPicData == null) 0 else liPeiAnimalPicData!!.size
    }

    override fun createParentHolder(root: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_parent_expand_crop, root, false)
        return ParentHolder(view, this)
    }

    override fun createChildHolder(root: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_child_expand_crop, root, false)
        return ChildHolder(view, this)
    }

    override fun bindParentHolder(holder: ViewHolder, position: Int) {
        (holder as ParentHolder).setData(mGroupList!![position])
    }

    override fun bindChildHolder(holder: ViewHolder, parentPosition: Int, position: Int) {
        mGroupList!![parentPosition]!!.data2?.get(position)?.let { (holder as ChildHolder).setData(it) }
    }

    internal class ParentHolder(itemView: View, adapter: ExpandableAdapter<*>?) : ExpandableAdapter.ViewHolder(itemView, adapter) {
        var mTvName: TextView = itemView.findViewById(R.id.tv_name)
        var mSauqre: TextView = itemView.findViewById(R.id.tv_total)
        var mRisk: TextView = itemView.findViewById(R.id.tv_risk)
        var mLoss: TextView = itemView.findViewById(R.id.tv_loss)
        fun setData(data: ChaKanLandsBean.Data1DTO?) {
            data?.let {
                mTvName.text = data.fName
                mSauqre.text = "承保面积: ${data.fFarmerQuantity ?: "0"}"
                mRisk.text = "受灾面积: ${data.fFarmerRiskQty ?: "0"}"
                mLoss.text = "损失面积: ${data.fFarmerLossQty ?: "0"}"
            }
        }
    }


    internal class ChildHolder(itemView: View, adapter: ExpandableAdapter<*>?) : ExpandableAdapter.ViewHolder(itemView, adapter) {
        var mImgCheck: ImageView = itemView.findViewById(R.id.item_bag)
        var mImgGis: ImageView = itemView.findViewById(R.id.img_gis)
        var mtv_LandName: TextView = itemView.findViewById(R.id.tv_lanName)
        var mtv_FSquare: TextView = itemView.findViewById(R.id.tv_FSquare)
        var tv_FRiskQty: TextView = itemView.findViewById(R.id.tv_FRiskQty)
        var tv_FLossQty: TextView = itemView.findViewById(R.id.tv_FLossQty)
        var mLlSquare: LinearLayout = itemView.findViewById(R.id.ll_square)

        // var titles: MutableList<String> = mutableListOf("承保面积", "受灾面积", "损失面积")
        fun setData(data: ChaKanLandsBean.Data1DTO.Data2DTO) {
            Glide.with(itemView).load(data.fGISPicture).into(mImgGis)
            mtv_LandName.text = "地块名: ${data.fLandName}"
            mtv_FSquare.text = "承保面积: ${data.fSquare}"
            tv_FRiskQty.text = "受灾面积: ${data.fRiskQty}"
            tv_FLossQty.text = "损失面积: ${data.fLossQty}"
            var checked = data.fIsChecked
            mImgCheck.isSelected = checked
            mImgCheck.setOnClickListener {
                checked = !checked
                mImgCheck.isSelected = checked
                data.fIsChecked = checked
            }
            mImgGis.setOnClickListener { DialogImg(itemView.context, data.fGISPicture).show() }

        }


    }

    open fun upDateData(@IntRange(from = 0) parentPosition: Int, childPosition: Int, data: ChaKanLandsBean.Data1DTO.Data2DTO?) {
        if (this.mGroupList.isNullOrEmpty()) {
            return
        }
        val data1DTO = this.mGroupList!![parentPosition]!!
        data1DTO.fInsureNumber = data1DTO.fInsureNumber
        data1DTO.fName = data1DTO.fName
        data1DTO.fNumber = data1DTO.fNumber
        val childData = data1DTO.data2?.get(childPosition)
        childData!!.fLandNumber = data!!.fLandNumber
        childData!!.fSquare = data.fSquare
        childData!!.fLossQty = data!!.fLossQty
        childData!!.fRiskQty = data.fRiskQty
        childData!!.fGISPicture = data.fGISPicture
        var total_square = 0.0
        var total_loss = 0.0
        var total_risk = 0.0
        for (index in data1DTO.data2!!.indices) {
            var fSquare: String? = null
            var fLoss: String? = null
            var fRisk: String? = null
            if (index != childPosition) {
                val data2DTO = data1DTO.data2!![index]
                fSquare = data2DTO?.fSquare
                fLoss = data2DTO?.fLossQty
                fRisk = data2DTO?.fRiskQty
            } else {
                fSquare = data!!.fSquare
                fLoss = data.fLossQty
                fRisk = data.fRiskQty
            }
            total_square += getSqure(fSquare)
            total_loss += getSqure(fLoss)
            total_risk += getSqure(fRisk)
        }
        data1DTO.fFarmerQuantity = formatDouble(total_square)
        data1DTO.fFarmerLossQty = formatDouble(total_loss)
        data1DTO.fFarmerRiskQty = formatDouble(total_risk)

        notifyDataSetChanged()
    }

    fun formatDouble(d: Double): String {
        val df = DecimalFormat("#####0.00")
        return df.format(d)
    }

    fun getSqure(s: String?): Double {
        return if (s.isNullOrEmpty() || s == "0.0")
            0.0
        else {
            if (Utils.isNumeric(s)) {
                s.toDouble()
            } else {
                0.0
            }
        }
    }
}