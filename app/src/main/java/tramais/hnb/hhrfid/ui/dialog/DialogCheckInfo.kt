package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.FidDetail
import tramais.hnb.hhrfid.bean.InsureBillDetail
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetInsureBillDetail
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivityFarmList
import tramais.hnb.hhrfid.util.TimeUtil

class DialogCheckInfo(context_: Context, private val module_type: String, private val fenPei: FenPei) : Dialog(context_, R.style.dialog) {
    private var mIvImg: RecyclerView? = null
    private var mTitle: TextView? = null
    private var mIvClose: ImageView? = null
    private var mAdapter: QuickAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_check_info)

        initView()
        initData()
    }

    var infos: MutableList<String> = ArrayList()
    var buffer: StringBuffer = StringBuffer()
    private fun initData() {
        buffer.delete(0, buffer.length)
        mTitle!!.text = module_type
        infos?.clear()
        if (module_type == "报案信息") {
            fenPei?.let {
                infos.add("报案号:   ${fenPei.number ?: ""}")
                infos.add("报案人姓名:   ${fenPei.farmerName ?: ""}")
                infos.add("报案人电话:   ${fenPei.mobile ?: ""}")
                infos.add("报案号:   ${fenPei.number ?: ""}")
                infos.add("出险地址:   ${fenPei.riskAddress ?: ""}")
                infos.add("标的名称:   ${fenPei.fItemDetailList ?: ""}")
                infos.add("出险时间:   ${fenPei.riskDate ?: ""}")
                infos.add("报案时间:   ${fenPei.baoAnDate ?: ""}")
                infos.add("出险原因:   ${fenPei.riskReason ?: ""}")
                infos.add("出险数量:   ${fenPei.riskQty ?: ""}")
                infos.add("出险经过:   ${fenPei.fRemark ?: ""}")
                mAdapter!!.setList(infos)
            }
        } else if (module_type == "承保信息") {
            RequestUtil.getInstance(context)!!.getPiccInsureBillDetail(fenPei.insureNumber, fenPei.farmerName, object : GetInsureBillDetail {
                override fun getBillDetail(billDetailBean: InsureBillDetail?) {

                    billDetailBean?.let {
                        infos.add("被保险人:   ${fenPei.farmerName ?: ""}")
                        infos.add("证件类型:   ${it.identifyType ?: ""}")
                        infos.add("证件号码:   ${it.identifyNumber ?: ""}")
                        infos.add("报案号:   ${fenPei.number ?: ""}")
                        infos.add("标的地址:   ${it.insuredAddress ?: ""}")
                        infos.add("险种:   ${it.itemName ?: ""}")
                        infos.add("承保数量:   ${it.quantity ?: ""}")
                        infos.add("保险金额:   ${it.amount ?: ""}")
                        infos.add("保险时间:    ${it.startDate + " 至 " + it.endDate}")
                        infos.add("开户银行:   ${it.bank ?: ""}")
                        infos.add("银行卡号:   ${it.accountNo ?: ""}")
                        mAdapter!!.setList(infos)

                    }

                }
            })
        } else if (module_type == "查勘公示" || module_type == "提交核心") {
            RequestUtil.getInstance(context)!!.getChaKanDetail(fenPei.number) { detail ->
                detail?.let {
                    infos.add("被保险人:   ${fenPei.farmerName}")
                    infos.add("报案号:   ${fenPei.number}")
                    infos.add("查勘日期:   ${it?.createTime ?: ""}")
                    infos.add("出险地点:   ${it.riskAddress ?: ""}")
                    infos.add("损失情况:   ${it.lostRemark ?: ""}")
                    infos.add("损失估计:   ${it.lostAssess ?: ""}")
                    infos.add("查勘意见:   ${it.chaKanResult ?: ""}")
                    infos.add("开户名:   ${it.accountName ?: ""}")
                    infos.add("开户行:   ${it.bankName ?: ""}")
                    infos.add("银行账户:    ${it.bankAccount ?: ""}")
                    infos.add("险种:   ${fenPei.fItemDetailList ?: ""}")
                    infos.add("单位保额:   ${fenPei.fUnitAmount ?: ""}")
                    infos.add("事故类型:   ${it.fReasonName ?: ""}")
                    infos.add("损失数量:   ${it.fRiskQty ?: ""}")
                    infos.add("损失金额:   ${it.fRiskAmount ?: ""}")
                    mAdapter!!.setList(infos)
                }
            }
        } else if (module_type == "查勘信息") {
            RequestUtil.getInstance(context)!!.getLandChaKanFidDetail(fenPei.number, "",object : GetCommon<FidDetail> {
                override fun getCommon(t: FidDetail) {
                    infos.add("被保险人:   ${fenPei.farmerName}")
                    infos.add("报案号:   ${fenPei.number}")
                    infos.add("查勘日期:   ${t?.fChaKanDate ?: ""}")
                    infos.add("出案地点:   ${t.fAddress ?: ""}")
                    infos.add("出险原因:   ${t.fRiskReason ?: ""}")
                    infos.add("受灾面积:   ${t.fShouZaiqty ?: ""}")
                    infos.add("损失面积:   ${t.fShouZaiqty ?: ""}")
                    infos.add("估损金额:   ${t.fLossqty ?: ""}")
                    infos.add("生长阶段:   ${t.fGrowthStage ?: ""}")
                    infos.add("出险经过:   ${t.fRiskProcess ?: ""}")
                    infos.add("查勘意见:   ${t.fOpintion ?: ""}")
                    mAdapter!!.setList(infos)
                }
            })
        }

    }

    private fun initView() {
        mIvClose = findViewById(R.id.iv_close)
        mTitle = findViewById(R.id.title)
        mIvImg = findViewById(R.id.tv_phone)
        mIvClose!!.setOnClickListener { dismiss() }
        mAdapter = QuickAdapter()

        mIvImg!!.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ColorDrawable(Color.GRAY))
        mIvImg!!.addItemDecoration(divider)
        mIvImg!!.adapter = mAdapter
    }

    class QuickAdapter : BaseQuickAdapter<String?, BaseViewHolder>(R.layout.item_text_left) {
        override fun convert(holder: BaseViewHolder, item: String?) {
            item?.let { farmer_ ->
                holder.setText(R.id.tv, farmer_)
            }
        }
    }
}