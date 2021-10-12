package tramais.hnb.hhrfid.ui.crop.frag

import android.text.TextUtils
import android.view.View
import android.widget.ListView
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.base.CommonAdapter
import tramais.hnb.hhrfid.bean.CropCheckChengBaoBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCropChengBaoDetail
import tramais.hnb.hhrfid.lv.ViewHolder
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.crop.CropCheckPassActivity
import tramais.hnb.hhrfid.util.PreferUtils

class CropChengBaoInfo : BaseFragment() {
    lateinit var rootView: View
    private lateinit var mLv: ListView
    private lateinit var mChuxianAddress: TextView
    private lateinit var mBananDanhao: TextView
    private lateinit var mEtMobile: TextView
    private lateinit var mTvName: TextView
    private lateinit var mBananNum: TextView
    private lateinit var mSelf: TextView
    private lateinit var mGvButie: TextView
    private lateinit var mBaoxianJine: TextView
    private lateinit var mDanweiBaoe: TextView


    override fun findViewById(view: View?) {
        view?.let {
            mLv = it.findViewById(R.id.lv)
            mBananNum = it.findViewById(R.id.banan_num)
            mTvName = it.findViewById(R.id.tv_name)
            mEtMobile = it.findViewById(R.id.et_mobile)
            mBananDanhao = it.findViewById(R.id.banan_danhao)
            mChuxianAddress = it.findViewById(R.id.chuxian_address)
            mDanweiBaoe = it.findViewById(R.id.danwei_baoe)
            mBaoxianJine = it.findViewById(R.id.baoxian_jine)
            mGvButie = it.findViewById(R.id.gv_butie)
            mSelf = it.findViewById(R.id.self)
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_chengbao
    }

    override fun initListener() {

    }

    override fun initData() {
        val cropChaKanBean = CropCheckPassActivity.cropChaKanBean//IN2020081600006
        RequestUtil.getInstance(context)!!.getLandBillDetail(cropChaKanBean.number, object : GetCropChengBaoDetail {
            override fun getBillDetail(chanKanDetailBean: CropCheckChengBaoBean?) {
                chanKanDetailBean?.let {
                    mBananNum.text = it.fNumber
                    mTvName.text = it.fLandAddress
                    mEtMobile.text = it.fLandCategory
                    PreferUtils.putString(context, Constants.LandCategory, it.flandCategoryID)
                    mBananDanhao.text = it.dwbe
                    mChuxianAddress.text = it.fBeginDate + " 至 " + it.fEndDate
                    mDanweiBaoe.text = it.dwbe
                    if (!TextUtils.isEmpty(it.fSubsidies) && !TextUtils.isEmpty(it.fOwnAmount))
                        mBaoxianJine.text = (it.fSubsidies.toDouble() + it.fOwnAmount.toDouble()).toString()
                    else if (!TextUtils.isEmpty(it.fSubsidies))
                        mBaoxianJine.text = (it.fSubsidies.toDouble()).toString()
                    else if (!TextUtils.isEmpty(it.fOwnAmount))
                        mBaoxianJine.text = (it.fOwnAmount.toDouble()).toString()
                    mGvButie.text = it.fSubsidies
                    mSelf.text = it.fOwnAmount
                    val allLandList = it.getAllLandList
                    if (allLandList == null || allLandList.size == 0) return
                    mLv.adapter = object : CommonAdapter<CropCheckChengBaoBean.GetAllLandListDTO?>(context, allLandList, R.layout.item_land_detail) {
                        override fun convert(helper: ViewHolder?, item: CropCheckChengBaoBean.GetAllLandListDTO?) {
                            helper?.let {
                                val farmer_name = it.getView<TextView>(R.id.farmer_name)
                                val farmer_dk = it.getView<TextView>(R.id.dk)
                                val insure_dk = it.getView<TextView>(R.id.insure_dk)
                                val check_dk = it.getView<TextView>(R.id.check_dk)
                                farmer_name.text = item!!.fName
                                farmer_dk.text = item.fLandName
                                insure_dk.text = item.fSquare
                                check_dk.text = item.fCheckSquare
                            }

                        }
                    }
                }
            }

        })


    }

    override fun initImmersionBar() {

    }
}