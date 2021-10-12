package tramais.hnb.hhrfid.ui.crop.frag

import android.view.View
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.CropCheckPassActivity

class CropBaoAnInfo : BaseFragment() {
    private lateinit var mInputInfo: TextView
    private lateinit var mTvChuxianReason: TextView
    private lateinit var mTvBaoanDate: TextView
    private lateinit var mTvChuxianDate: TextView
    private lateinit var mChuxianAddress: TextView
    private lateinit var mBananDanhao: TextView
    private lateinit var mEtMobile: TextView
    private lateinit var mTvBaoanName: TextView
    private lateinit var mBananNum: TextView
    private lateinit var mAddress: TextView
    private lateinit var mDamage: TextView
    override fun findViewById(view: View?) {
        view?.let {
            mBananNum = it.findViewById(R.id.banan_num)
            mTvBaoanName = it.findViewById(R.id.tv_baoan_name)
            mEtMobile = it.findViewById(R.id.et_mobile)
            mBananDanhao = it.findViewById(R.id.banan_danhao)
            mChuxianAddress = it.findViewById(R.id.chuxian_address)
            mTvChuxianDate = it.findViewById(R.id.tv_chuxian_date)
            mTvBaoanDate = it.findViewById(R.id.tv_baoan_date)
            mTvChuxianReason = it.findViewById(R.id.tv_chuxian_reason)
            mInputInfo = it.findViewById(R.id.input_info)
            mAddress = it.findViewById(R.id.address)
            mDamage = it.findViewById(R.id.damage_qty)
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_baoan
    }

    override fun initListener() {
    }

    override fun initData() {
        val cropChaKanBean = CropCheckPassActivity.cropChaKanBean
        mBananNum.text = cropChaKanBean.number
        getDetail(cropChaKanBean.number)
    }

    private fun getDetail(num: String?) {
        RequestUtil.getInstance(context)!!.GetLandChaKanDet(num) {
            mTvBaoanName.text = it.fFarmerName
            mEtMobile.text = it.fMobile
            mBananDanhao.text = it.fInsureNumber
            mChuxianAddress.text = it.fRiskAddress
            mTvChuxianDate.text = it.fRiskDate
            mTvBaoanDate.text = it.fBaoAnDate
            mTvChuxianReason.text = it.fRiskReason
            mInputInfo.text = it.fRiskProcess
            mDamage.text = it.fLandCategory
        }
    }
    override fun initImmersionBar() {

    }

}