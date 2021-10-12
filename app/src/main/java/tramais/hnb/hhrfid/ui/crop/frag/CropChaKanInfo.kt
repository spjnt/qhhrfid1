package tramais.hnb.hhrfid.ui.crop.frag

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.base.CommonAdapter
import tramais.hnb.hhrfid.bean.CropChaKanBean
import tramais.hnb.hhrfid.bean.CropCheckChanKanList
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.TouBaoBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCropCheckChanKanList
import tramais.hnb.hhrfid.lv.ViewHolder
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.CropAddNewCheckActivity
import tramais.hnb.hhrfid.ui.crop.CropCheckPassActivity
import tramais.hnb.hhrfid.util.GsonUtil

class CropChaKanInfo : BaseFragment() {
    var add_new: TextView? = null
    var lv: RecyclerView? = null
    override fun findViewById(view: View?) {
        view?.let {
            lv = it?.findViewById(R.id.lv_content)
            add_new = it?.findViewById(R.id.add_new_check)
            lv!!.layoutManager = LinearLayoutManager(context)

            setAdapter()
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_chakan
    }

    override fun initListener() {
        add_new!!.setOnClickListener {
            var intent = Intent(context, CropAddNewCheckActivity::class.java)
            intent.putExtra("statu", "3")
            intent.putExtra(Constants.farmer_name, cropChaKanBean?.farmerName)
            intent.putExtra(Constants.Ba_num, cropChaKanBean?.number)
            intent.putExtra(Constants.MaxFid, maxFid)
            intent.putExtra(Constants.FProductCode, cropChaKanBean?.FProductCode)
            startActivity(intent)
        }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val item = it.getItem(position)
                var inten = Intent(context, CropAddNewCheckActivity::class.java)
                inten.putExtra("fid", item)
                inten.putExtra(Constants.FProductCode, cropChaKanBean?.FProductCode)
                inten.putExtra(Constants.MaxFid, maxFid)
                inten.putExtra("statu", "1")
                startActivity(inten)
            }
        }
    }

    var cropChaKanBean: FenPei? = null
    override fun initData() {
        cropChaKanBean = CropCheckPassActivity.cropChaKanBean

    }

    var maxFid: String? = null
    override fun onResume() {
        super.onResume()
        RequestUtil.getInstance(context)!!.getLandChaKanDetailList(cropChaKanBean!!.number, object : GetCropCheckChanKanList {
            override fun getList(list: CropCheckChanKanList?) {
                maxFid = list!!.maxFid
                mAdapter!!.setList(list!!.data)
            }
        })
    }

    override fun initImmersionBar() {

    }

    var mAdapter: BaseQuickAdapter<CropCheckChanKanList.DataDTO?, BaseViewHolder>? = null
    fun setAdapter() {
        lv!!.adapter = object : BaseQuickAdapter<CropCheckChanKanList.DataDTO?, BaseViewHolder>(R.layout.item_crop_chakan) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: CropCheckChanKanList.DataDTO?) {

                item?.let { farmer_ ->

                    holder.setText(R.id.tv_fid, "系号: ${farmer_.fid}")
                    holder.setText(R.id.tv_stage, "${farmer_.fGrowthStage}")
                    holder.setText(R.id.tv_date, "${farmer_.fChaKanDate}")
                    holder.setText(R.id.tv_workman, "${farmer_.fCreator}")
                }
            }
        }.also { mAdapter = it }
    }

}