/*
package tramais.hnb.hhrfid.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.litePalBean.DingSunCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.net.SaveAllCache
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils

class FragmentSunshi : Fragment() {
    var sheep_snow: EditText? = null
    var sheep_ill: EditText? = null
    var sheep_animal: EditText? = null
    var sheep_other: EditText? = null
    var cow_snow: EditText? = null
    var cow_ill: EditText? = null
    var cow_animal: EditText? = null
    var cow_other: EditText? = null
    var isTruthTrue: RadioButton? = null
    var isTruthFalse: RadioButton? = null
    var isTagTrue: RadioButton? = null
    var isTagFalse: RadioButton? = null
    var weather: RadioButton? = null
    var zhengming: RadioButton? = null
    var other: RadioButton? = null
    var save: Button? = null
    var fenPei: FenPei? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_sunshi, container, false)
        fenPei = ActivityFeedCheck.fenPei

        initView(inflate)
        initListener()
        return inflate
    }

    fun regist() {
        if (truthImp == -1) {
            showStr("请选择案情是否属实")
            return
        }
        if (isTagImp == -1) {
            showStr("请选择是否佩戴耳标")
            return
        }
        val creator = PreferUtils.getString(context, Constants.UserName)
        val time = TimeUtil.getTime(Constants.yyyy_mm_dd)
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.saveLose(fenPei!!, Utils.getEdit(sheep_snow), Utils.getEdit(sheep_ill), Utils.getEdit(sheep_animal), Utils.getEdit(sheep_other),
                    Utils.getEdit(cow_snow), Utils.getEdit(cow_ill), Utils.getEdit(cow_animal), Utils.getEdit(cow_other), truthImp, isTagImp, boo2Int(weatherImp),
                    boo2Int(zhengMingImp), boo2Int(otherImp), creator, time, time, { rtnCode, message -> showStr(message) })
        }
        SaveAllCache.SAVE_ALL_CACHE.saveDingSun_info(fenPei!!, Utils.getEdit(sheep_snow), Utils.getEdit(sheep_ill), Utils.getEdit(sheep_animal), Utils.getEdit(sheep_other),
                Utils.getEdit(cow_snow), Utils.getEdit(cow_ill), Utils.getEdit(cow_animal), Utils.getEdit(cow_other), truthImp, isTagImp, boo2Int(weatherImp),
                boo2Int(zhengMingImp), boo2Int(otherImp), object : GetOneString {
            override fun getString(str: String?) {
                showStr(str)
            }
        })


    }


    var truthImp = -1
    var isTagImp = -1
    var weatherImp: Boolean = false
    var zhengMingImp: Boolean = false
    var otherImp: Boolean = false
    private fun initListener() {
        isTruthTrue!!.setOnClickListener {
            truthImp = 1
            isTruthTrue!!.isChecked = true
            isTruthFalse!!.isChecked = false
        }

        isTruthFalse!!.setOnClickListener {
            truthImp = 0
            isTruthFalse!!.isChecked = true
            isTruthTrue!!.isChecked = false


        }

        isTagTrue!!.setOnClickListener {
            isTagImp = 1
            isTagTrue!!.isChecked = true
            isTagFalse!!.isChecked = false


        }

        isTagFalse!!.setOnClickListener {
            isTagImp = 0
            isTagFalse!!.isChecked = true
            isTagTrue!!.isChecked = false

        }

        weather!!.setOnClickListener {
            weatherImp = !weatherImp
            weather!!.isChecked = weatherImp

        }
        zhengming!!.setOnClickListener {
            zhengMingImp = !zhengMingImp
            zhengming!!.isChecked = zhengMingImp

        }
        other!!.setOnClickListener {
            otherImp = !weatherImp
            other!!.isChecked = otherImp

        }
        save!!.setOnClickListener { regist() }
    }

    fun initView(rootView: View) {
        sheep_snow = rootView.findViewById<EditText>(R.id.et_sheep_snow)
        sheep_ill = rootView.findViewById<EditText>(R.id.et_sheep_ill)
        sheep_animal = rootView.findViewById<EditText>(R.id.et_sheep_animal)
        sheep_other = rootView.findViewById<EditText>(R.id.et_sheep_other)

        cow_snow = rootView.findViewById<EditText>(R.id.et_cow_snow)
        cow_ill = rootView.findViewById<EditText>(R.id.et_cow_ill)
        cow_animal = rootView.findViewById<EditText>(R.id.et_cow_animal)
        cow_other = rootView.findViewById<EditText>(R.id.et_cow_other)

        isTruthTrue = rootView.findViewById<RadioButton>(R.id.is_truth_true)
        isTruthFalse = rootView.findViewById<RadioButton>(R.id.is_truth_false)
        isTagTrue = rootView.findViewById<RadioButton>(R.id.is_tag_true)
        isTagFalse = rootView.findViewById<RadioButton>(R.id.is_tag_false)
        weather = rootView.findViewById<RadioButton>(R.id.weather)
        zhengming = rootView.findViewById<RadioButton>(R.id.is_zhengming)
        other = rootView.findViewById<RadioButton>(R.id.is_other)
        save = rootView.findViewById(R.id.save)
        getDetail(fenPei!!.number)
    }

    fun boo2Int(isTrue: Boolean): Int {
        if (isTrue)
            return 1
        else return 0
    }

    fun showStr(show: String?) {
        if (!TextUtils.isEmpty(show)) {
            Toast.makeText(context, show, Toast.LENGTH_SHORT).show()
        }
    }

    fun getDetail(num: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getLossDetail(num) {
                if (it.code == 0) {
                    requireActivity().run {
                        sheep_snow!!.setText(it.sheepSnowQty.toString())
                        sheep_ill!!.setText(it.sheepIllQty.toString())
                        sheep_animal!!.setText(it.sheepHarmQty.toString())
                        sheep_other!!.setText(it.sheepOtherQty.toString())
                        cow_snow!!.setText(it.cowSnowQty.toString())
                        cow_ill!!.setText(it.cowIllQty.toString())
                        cow_animal!!.setText(it.cowHarmQty.toString())
                        cow_other!!.setText(it.cowOtherQty.toString())
                        if (it.caseIsTrue == 1) {
                            truthImp = 1
                            isTruthTrue!!.isChecked = true
                        } else {
                            isTagFalse!!.isChecked = true
                            truthImp = 0
                        }

                        if (it.isEarLabel == 1) {
                            isTagImp = 1
                            isTagTrue!!.isChecked = true
                        } else {
                            isTagImp = 0
                            isTagFalse!!.isChecked = true
                        }
                        */
/*
                        *
                        *   var isWeatherCert = 0
        var isIllCert = 0
        var isOtherCert = 0*//*

                        if (it.isWeatherCert == 1) weather!!.isChecked = true
                        if (it.isIllCert == 1) zhengming!!.isChecked = true
                        if (it.isOtherCert == 1) other!!.isChecked = true
                    }
                }
            }
        } else {
            LitePal.where("number =?", fenPei!!.number).findAsync(DingSunCache::class.java).listen {
                if (it != null && it.size > 0) {
                    val dingSunCache = it[0]
                    activity!!.run {
                        sheep_snow!!.setText(dingSunCache.sheep_snow)
                        sheep_ill!!.setText(dingSunCache.sheep_ill)
                        sheep_animal!!.setText(dingSunCache.sheep_harm)
                        sheep_other!!.setText(dingSunCache.sheep_other)
                        cow_snow!!.setText(dingSunCache.cow_snow)
                        cow_ill!!.setText(dingSunCache.cow_ill)
                        cow_animal!!.setText(dingSunCache.cow_harm)
                        cow_other!!.setText(dingSunCache.cow_other)
                        if (dingSunCache.isTruthTrue == 1) {
                            truthImp = 1
                            isTruthTrue!!.isChecked = true
                        } else {
                            isTagFalse!!.isChecked = true
                            truthImp = 0
                        }

                        if (dingSunCache.isTagTruth == 1) {
                            isTagImp = 1
                            isTagTrue!!.isChecked = true
                        } else {
                            isTagImp = 0
                            isTagFalse!!.isChecked = true
                        }
                        */
/*
                        *
                        *   var isWeatherCert = 0
        var isIllCert = 0
        var isOtherCert = 0*//*

                        if (dingSunCache.isWeather == 1) weather!!.isChecked = true
                        if (dingSunCache.isIll == 1) zhengming!!.isChecked = true
                        if (dingSunCache.isOther == 1) other!!.isChecked = true
                    }
                }
            }
        }

    }
}*/
