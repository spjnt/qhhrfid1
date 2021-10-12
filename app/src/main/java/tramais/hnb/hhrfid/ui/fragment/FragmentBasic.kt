package tramais.hnb.hhrfid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.FarmList
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.InsureBillDetail
import tramais.hnb.hhrfid.interfaces.GetInsureBillDetail
import tramais.hnb.hhrfid.litePalBean.AllBillDetailCache
import tramais.hnb.hhrfid.litePalBean.FarmListCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.util.GsonUtil
import tramais.hnb.hhrfid.util.NetUtil

class FragmentBasic : BaseFragment() {
    lateinit var rootView: View
    private lateinit var mBananNum: TextView
    private lateinit var mTvName: TextView
    private lateinit var mEtMobile: TextView
    private lateinit var mBananDanhao: TextView
    private lateinit var mChuxianAddress: TextView
    private lateinit var mTvChuxianDate: TextView
    private lateinit var mTvBaoanDate: TextView
    private lateinit var mTvChuxianReason: TextView
    lateinit var fenPei: FenPei
    private lateinit var mBankName: TextView
    private lateinit var mBankNumber: TextView
    private lateinit var mBankOpen: TextView
    private lateinit var mInsureNum: TextView
    override fun initImmersionBar() {

    }

    override fun findViewById(view: View?) {
        view?.let {
            mInsureNum = it.findViewById(R.id.insure_num)
            mBananNum = it.findViewById<TextView>(R.id.banan_num)
            mTvName = it.findViewById<TextView>(R.id.tv_name)
            mEtMobile = it.findViewById<TextView>(R.id.et_mobile)
            mBananDanhao = it.findViewById<TextView>(R.id.banan_danhao)
            mChuxianAddress = it.findViewById<TextView>(R.id.chuxian_address)
            mTvChuxianDate = it.findViewById<TextView>(R.id.tv_chuxian_date)
            mTvBaoanDate = it.findViewById<TextView>(R.id.tv_baoan_date)
            mTvChuxianReason = it.findViewById<TextView>(R.id.tv_chuxian_reason)
            mBankName = it.findViewById<TextView>(R.id.bank_name)
            mBankNumber = it.findViewById<TextView>(R.id.bank_number)
            mBankOpen = it.findViewById<TextView>(R.id.bank_open)
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_basic
    }

    override fun initListener() {

    }

    override fun initData() {
        fenPei = ActivityFeedCheck.fenPei
        getBasicInfo()
    }


    fun getBasicInfo() {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getPiccInsureBillDetail(fenPei.insureNumber, fenPei.farmerName, object : GetInsureBillDetail {
                override fun getBillDetail(billDetailBean: InsureBillDetail?) {
                    billDetailBean?.let {
                        activity!!.runOnUiThread {
                            mBananNum.text = fenPei.insureNumber
                            mTvName.text = it.insuredAddress
                            mEtMobile.text = it.itemName
                            mInsureNum.text = it.quantity//保险数量
                            mBananDanhao.text = it.amount//保险金额
                            mChuxianAddress.text = it.startDate + " 至 " + it.endDate
                            mTvChuxianDate.text = it.insuredName
                            mTvBaoanDate.text = it.identifyType
                            mTvChuxianReason.text = it.identifyNumber
                            mBankName.text = it.bank
                            mBankNumber.text = it.accountNo
                         //   mBankOpen.text = it.insuredName
                        }
                    }

                }
            })

        } else {
            LitePal.where("ZjNumber =?", fenPei.farmerNumber).findAsync(FarmListCache::class.java).listen { list ->
                if (list != null && list.size > 0) {
                    val it = list[0]
                    requireActivity().runOnUiThread {
                        mBananNum.text = fenPei.insureNumber
                        mTvChuxianDate.text = it.name
                        mTvBaoanDate.text = it.zjCategory
                        mTvChuxianReason.text = it.zjNumber
                        mBankName.text = it.bankName
                        mBankNumber.text = it.accountNumber
                        mBankOpen.text = it.accountName
                    }
                }
            }
            //MakeTouBaoDealBean
            LitePal.where("FarmerNumber=?", fenPei.farmerNumber).findAsync(AllBillDetailCache::class.java).listen { list ->
                if (list != null && list.size > 0) {
                    val it = list[0]
                    requireActivity().runOnUiThread {
                        mTvName.text = it.labelAddress
                        mEtMobile.text = it.riskType
                        mBananDanhao.text = it.sumAmount.toString()
                        mChuxianAddress.text = it.beginDate + " 至 " + it.endDate
                    }
                }
            }

        }

    }


}