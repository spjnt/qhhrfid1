package tramais.hnb.hhrfid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.ui.ActivityFeedCheck

class FragmentBaoAn : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_baoan, container, false)
        initView(rootView)
        return rootView
    }

    fun initView(rootView: View) {
        var fenPei = ActivityFeedCheck.fenPei
        rootView.findViewById<TextView>(R.id.banan_num).text = fenPei.number
        rootView.findViewById<TextView>(R.id.tv_name).text = fenPei.farmerName
        rootView.findViewById<TextView>(R.id.et_mobile).text = fenPei.mobile
        rootView.findViewById<TextView>(R.id.banan_danhao).text = fenPei.insureNumber
        rootView.findViewById<TextView>(R.id.chuxian_address).text = fenPei.riskAddress
        rootView.findViewById<TextView>(R.id.tv_chuxian_date).text = fenPei.riskDate
        rootView.findViewById<TextView>(R.id.tv_baoan_date).text = fenPei.baoAnDate
        rootView.findViewById<TextView>(R.id.tv_chuxian_reason).text = fenPei.riskReason
        rootView.findViewById<TextView>(R.id.et_chuxian_num).text = fenPei.riskQty
        rootView.findViewById<TextView>(R.id.input_info).text = fenPei.riskProcess
        rootView.findViewById<TextView>(R.id.biaodi_mi).text = fenPei.fItemDetailList
        /*  } else {
              LitePal.where("number=?", fenPei.number).findAsync(BanAnInfo::class.java).listen { list ->
                  if (list != null && list.size > 0) {
                      val banAnInfo = list[0]
                      rootView.findViewById<TextView>(R.id.banan_num).text = banAnInfo.number
                      rootView.findViewById<TextView>(R.id.tv_name).text = banAnInfo.farmName
                      rootView.findViewById<TextView>(R.id.et_mobile).text = banAnInfo.farmMobile
                      rootView.findViewById<TextView>(R.id.banan_danhao).text = banAnInfo.insureNumber
                      rootView.findViewById<TextView>(R.id.chuxian_address).text = banAnInfo.insureAddress
                      rootView.findViewById<TextView>(R.id.tv_chuxian_date).text = banAnInfo.insureTime
                      rootView.findViewById<TextView>(R.id.tv_baoan_date).text = banAnInfo.baoAnTime
                      rootView.findViewById<TextView>(R.id.tv_chuxian_reason).text = banAnInfo.insureReason
                      rootView.findViewById<TextView>(R.id.et_chuxian_num).text = banAnInfo.insureNum
                      rootView.findViewById<TextView>(R.id.input_info).text = banAnInfo.insureRemark
                  }
              }
          }*/


    }

}
