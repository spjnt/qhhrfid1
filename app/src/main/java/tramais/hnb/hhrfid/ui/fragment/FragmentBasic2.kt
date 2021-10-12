/*
package tramais.hnb.hhrfid.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.baidu.location.LocationClient
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.interfaces.GetBDLocation
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.litePalBean.AllBillDetailCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.BDLoactionUtil
import tramais.hnb.hhrfid.util.NetUtil

class FragmentBasic2 : Fragment() {
    var mLocationClient: LocationClient? = null
    lateinit var inflate: View
    lateinit var fenPei: FenPei
    private lateinit var mBananNum: TextView

    private lateinit var mTvName: TextView
    private lateinit var mEtMobile: TextView

    private lateinit var mBananDanhao: TextView
    private lateinit var mChuxianAddress: TextView
    private lateinit var mTvChuxianDate: TextView
    private lateinit var mTvBaoanDate: TextView
    private lateinit var mTvChuxianReason: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflate = inflater.inflate(R.layout.fragment_basic2, container, false)
        initView(inflate)
        return inflate
    }

    fun initView(rootView: View) {
        fenPei = ActivityFeedCheck2.fenPei
        mBananNum = rootView.findViewById<TextView>(R.id.banan_num)

        mTvName = rootView.findViewById<TextView>(R.id.tv_name)
        mEtMobile = rootView.findViewById<TextView>(R.id.et_mobile)

        mBananDanhao = rootView.findViewById<TextView>(R.id.banan_danhao)
        mChuxianAddress = rootView.findViewById<TextView>(R.id.chuxian_address)
        mTvChuxianDate = rootView.findViewById<TextView>(R.id.tv_chuxian_date)
        mTvBaoanDate = rootView.findViewById<TextView>(R.id.tv_baoan_date)
        mTvChuxianReason = rootView.findViewById<TextView>(R.id.tv_chuxian_reason)
        mChuxianAddress.text = fenPei.number
        mTvBaoanDate.text = fenPei.baoAnDate

        getBillDetail(fenPei.number)


    }

    override fun onResume() {
        super.onResume()

        if (NetUtil.checkNet(context)) {
            mLocationClient = LocationClient(context)
            BDLoactionUtil.initLoaction(mLocationClient)
            mLocationClient!!.start()
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(MyLocationListener(GetBDLocation { lat: Double, log: Double, add: String ->
                if (!TextUtils.isEmpty(add)) {
                    mTvChuxianReason.text = add
                    mLocationClient!!.stop()
                }
            }))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    fun getBillDetail(number: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getBillDetail(number) { bean ->
                if (bean.code == 0) {
                    activity!!.runOnUiThread {
                        mBananNum.text = bean.billNumber
                        mTvName.text = bean.farmerName
                        if (bean.beginDate != null && bean.endDate != null)
                            mEtMobile.text = bean.beginDate + " 至 " + bean.endDate
                        mBananDanhao.text = bean.area
                        mTvChuxianDate.text = bean.billDate
                    }

                }
            }
        } else {
            mBananNum.text = fenPei.insureNumber
            mTvName.text = fenPei.farmerName
            mBananDanhao.text = fenPei.riskAddress
            mTvChuxianDate.text = fenPei.riskDate
            LitePal.where("FarmerNumber=?", fenPei.farmerNumber).findAsync(AllBillDetailCache::class.java).listen { list ->
                if (list != null && list.size > 0) {
                    val it = list[0]
                    activity!!.runOnUiThread {
                        mTvChuxianReason.text = it.labelAddress
                        mEtMobile.text = it.beginDate + " 至 " + it.endDate
                    }
                }
            }
        }

    }
}*/
