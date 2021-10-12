package tramais.hnb.hhrfid.ui.crop.frag

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.CropFarmList
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.interfaces.GetCropFarmList
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.ActivitySaveBankInfo
import tramais.hnb.hhrfid.ui.crop.CropCheckPassActivity

class CropFarmListFrag : BaseFragment() {
    var mAdapter: BaseQuickAdapter<CropFarmList.DataDTO?, BaseViewHolder>? = null
    var lv: RecyclerView? = null


    override fun findViewById(view: View?) {
        view?.let {
            lv = it?.findViewById(R.id.lv_content)
            lv!!.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_farmlist
    }

    override fun initListener() {
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val item = it.getItem(position)
                var intent = Intent(context, ActivitySaveBankInfo::class.java)
                intent.putExtra("dto", item)
                requireActivity().startActivity(intent)
            }
        }
    }

    var cropChaKanBean: FenPei? = null
    override fun initData() {
        setAdapter()
        cropChaKanBean = CropCheckPassActivity.cropChaKanBean

    }

    override fun onResume() {
        super.onResume()
        RequestUtil.getInstance(context)!!.GetInsureFarmer(cropChaKanBean!!.insureNumber, object : GetCropFarmList {
            override fun cropFarmList(info: CropFarmList?) {
                mAdapter!!.setList(info?.data)
            }
        })
    }
    override fun initImmersionBar() {

    }

    fun setAdapter() {
        lv!!.adapter = object : BaseQuickAdapter<CropFarmList.DataDTO?, BaseViewHolder>(R.layout.item_crop_farm_list) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: CropFarmList.DataDTO?) {
                item?.let { farmer_ ->
                    holder.setText(R.id.tv_name, farmer_.name)
                    holder.setText(R.id.tv_bank_name, farmer_.bankName)
                    holder.setText(R.id.tv_bank_num, farmer_.accountNumber)
                }
            }
        }.also { mAdapter = it }
    }
}