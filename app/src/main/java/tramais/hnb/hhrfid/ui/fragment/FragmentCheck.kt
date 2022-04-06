package tramais.hnb.hhrfid.ui.fragment

import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.baidu.location.LocationClient
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.litePalBean.BaoAnListCache
import tramais.hnb.hhrfid.litePalBean.ChaKanCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.net.SaveAllCache
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.ui.dialog.DialogCheckInfo
import tramais.hnb.hhrfid.util.*

class FragmentCheck : BaseFragment()/*, ChoicePhoto*/ {
    var address: EditText? = null
    var checkDate: TextView? = null
    var sunshi: EditText? = null
    var guji: EditText? = null
    var advice: EditText? = null
    var save: Button? = null

    //    var addImage: ImageView? = null
//    var rvPhoto: RecyclerView? = null
    var baoAn: TextView? = null
    var chengBao: TextView? = null
    var fenPei: FenPei? = null

    override fun initImmersionBar() {}

    override fun findViewById(view: View?) {
        view?.let {
            baoAn = it.findViewById(R.id.baoan_info)
            chengBao = it.findViewById(R.id.chengbao_info)
            checkDate = it.findViewById(R.id.check_date)
            address = it.findViewById(R.id.tv_name)
            sunshi = it.findViewById(R.id.input_sunshi)
            guji = it.findViewById(R.id.sunshi_guji)
            advice = it.findViewById(R.id.check_advice)
            save = it.findViewById<Button>(R.id.save)
//            addImage = it.findViewById(R.id.add)
//            rvPhoto = it.findViewById(R.id.rv_photos)
            checkDate!!.text = TimeUtil.getTime(Constants.yyyy_mm_dd)
//            val layoutManager = GridLayoutManager(context, 3)
//            rvPhoto!!.layoutManager = layoutManager

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_check
    }

    override fun initListener() {
        baoAn!!.setOnClickListener {
            DialogCheckInfo(requireContext(), "报案信息", fenPei!!).show()
        }
        chengBao!!.setOnClickListener {
            DialogCheckInfo(requireContext(), "承保信息", fenPei!!).show()
        }
        checkDate!!.setOnClickListener { TimeUtil.initTimePicker(context, -1, -1) { str: String? -> checkDate!!.text = str } }

        save!!.setOnClickListener {
            saveInfo()
        }
    }

    fun saveInfo() {
        val name = PreferUtils.getString(context, Constants.UserName)
        if (NetUtil.checkNet(requireContext())) {
            showAvi()
            RequestUtil.getInstance(context)!!.saveChaKan(fenPei!!, Utils.getText(checkDate), Utils.getEdit(address), Utils.getEdit(sunshi), Utils.getEdit(guji), Utils.getEdit(advice), name, null, TimeUtil.getTime(Constants.yyyy_mm_dd), TimeUtil.getTime(Constants.yyyy_mm_dd)) { rtnCode, message ->
                hideAvi()
                message.showStr()
            }
        } else {
            SaveAllCache.SAVE_ALL_CACHE.saveCheck_info(fenPei!!, Utils.getText(checkDate), Utils.getEdit(address), Utils.getEdit(sunshi), Utils.getEdit(guji), Utils.getEdit(advice), null, object : GetOneString {
                override fun getString(str: String?) {
                    str.showStr()
                    if (str!!.contains("成功")) {
                        val cache = BaoAnListCache()
                        cache.status = "已查勘"
                        val updateAll = cache.updateAll("number =?", fenPei!!.number)
                        //   LogUtils.e("查勘缓存" + if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")

                    }
                }
            })
        }
    }

    override fun initData() {
        fenPei = ActivityFeedCheck.fenPei
        getChaKanDetail(fenPei!!.number)
    }


    private fun getChaKanDetail(number: String?) {
        if (NetUtil.checkNet(requireContext())) {
            RequestUtil.getInstance(context)!!.getChaKanDetail(number) { detail ->
                detail?.let {
                    requireActivity().runOnUiThread {
                        if (it.chaKanDate.isNullOrBlank())
                            checkDate!!.text = TimeUtil.getTime(Constants.yyyy_mm_dd)
                        else checkDate!!.text = it.chaKanDate
                        address!!.setText(it.riskAddress)
                        sunshi!!.setText(it.lostRemark)
                        guji!!.setText(it.lostAssess)
                        advice!!.setText(it.chaKanResult)
                    }
                }
            }
        } else {
            LitePal.where("number =?", fenPei!!.number).findAsync(ChaKanCache::class.java).listen { list ->
                if (list != null && list.size > 0) {
                    val chaKanCache = list[0]
                    checkDate!!.text = chaKanCache.chakanDate
                    address!!.setText(chaKanCache.chakanAddress)
                    sunshi!!.setText(chaKanCache.chankandesc)
                    guji!!.setText(chaKanCache.chankansunshi)
                    advice!!.setText(chaKanCache.chakanAdvice)
                }
            }
        }

    }

    var mLocationClient: LocationClient? = null
    override fun onResume() {
        super.onResume()
        if (NetUtil.checkNet(requireContext())) {
            if (address!!.text.toString().isEmpty()) {
                mLocationClient = LocationClient(context)
                BDLoactionUtil.initLoaction(mLocationClient)
                mLocationClient!!.start()
                mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String ->
                    if (!TextUtils.isEmpty(add)) {
                        address!!.setText(add)
                        mLocationClient!!.stop()
                    }
                })
            }

        }
    }
}