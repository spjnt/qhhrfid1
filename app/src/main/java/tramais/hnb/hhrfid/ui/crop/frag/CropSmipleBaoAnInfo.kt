package tramais.hnb.hhrfid.ui.crop.frag

import android.view.View
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.CropSmipleActivity

class CropSmipleBaoAnInfo : BaseFragment() {
    private lateinit var mInputInfo: TextView
    private lateinit var mTvChuxianReason: TextView
    private lateinit var mTvBaoanDate: TextView
    private lateinit var mTvChuxianDate: TextView
    private lateinit var mChuxianAddress: TextView
    private lateinit var mBananDanhao: TextView

    private lateinit var mEtMobile: TextView

    private lateinit var mTvBaoanName: TextView
    private lateinit var mBananNum:
            TextView
    private lateinit var mAddress: TextView
    override fun findViewById(view: View?) {
        view?.let {
            mBananNum = view.findViewById(R.id.banan_num)
            mTvBaoanName = view.findViewById(R.id.tv_baoan_name)
            mEtMobile = view.findViewById(R.id.et_mobile)

            mBananDanhao = view.findViewById(R.id.banan_danhao)
            mChuxianAddress = view.findViewById(R.id.chuxian_address)
            mTvChuxianDate = view.findViewById(R.id.tv_chuxian_date)
            mTvBaoanDate = view.findViewById(R.id.tv_baoan_date)
            mTvChuxianReason = view.findViewById(R.id.tv_chuxian_reason)
            mInputInfo = view.findViewById(R.id.input_info)

            mAddress = view.findViewById(R.id.address)

        }

    }


    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_baoan
    }

    override fun initListener() {

    }

    override fun initData() {
        val cropChaKanBean = CropSmipleActivity.cropChaKanBean
        mBananNum.text = cropChaKanBean.fNumber
        getDetail(cropChaKanBean.fNumber)
    }

    private fun getDetail(num: String) {
        RequestUtil.getInstance(context)!!.GetLandChaKanDet(num

        ) {
            mTvBaoanName.text = it.fFarmerName
            mEtMobile.text = it.fMobile
            mBananDanhao.text = it.fInsureNumber
            mChuxianAddress.text = it.fRiskAddress
            mTvChuxianDate.text = it.fRiskDate
            mTvBaoanDate.text = it.fBaoAnDate
            mTvChuxianReason.text = it.fRiskReason
            mInputInfo.text = it.fRiskProcess
            val fLandCategory = it.fLandCategory

        }
    }
    override fun initImmersionBar() {

    }

}




