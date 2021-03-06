package tramais.hnb.hhrfid.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.gyf.immersionbar.ktx.immersionBar
import com.rscja.deviceapi.RFIDWithUHF
import com.rscja.deviceapi.exception.ConfigurationException
import com.uhf.api.cls.Reader.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.ColorChoiceBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.service.DownloadService
import tramais.hnb.hhrfid.ui.dialog.DialogFeedBack
import tramais.hnb.hhrfid.ui.popu.PopColorChoice
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils

class SettingFragment : BaseFragment() {

    private var mIvPhotos: TextView? = null
    private var mRlPhotoChoice: RelativeLayout? = null
    private var mTvRef: TextView? = null
    private var mIvRefChoice: RelativeLayout? = null
    private var data_img: MutableList<String>? = null
    private var data_rfid: MutableList<String>? = null
    private var mColorChoice: RelativeLayout? = null
    private var mTvColor: TextView? = null
    private var mTvCologBg: TextView? = null
    private var mTvAccount: TextView? = null
    private var mTvPhone: TextView? = null
    private var mRlVersion: RelativeLayout? = null
    private var mRlCache: RelativeLayout? = null
    private var mRlFeedback: RelativeLayout? = null
    private var mBtnLogin: LinearLayout? = null
    private var mLlColor: LinearLayout? = null

    /*   Intent intent= new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://www.baidu.com");
        intent.setData(content_url);
        startActivity(intent);*/
    //    private var mTvVersion: TextView? = null
    private var mTvCacheTime: TextView? = null
    private var mTvCompany: TextView? = null
    override fun findViewById(view: View?) {
        view?.let {
            mLlColor = it.findViewById(R.id.ll_color)
            mTvColor = it.findViewById(R.id.tv_color)
            mTvCologBg = it.findViewById(R.id.tv_color_bg)
            mIvPhotos = it.findViewById(R.id.iv_photos)
            mRlPhotoChoice = it.findViewById(R.id.rl_photo_choice)
            mTvRef = it.findViewById(R.id.tv_ref)
            mIvRefChoice = it.findViewById(R.id.iv_ref_choice)
            mTvAccount = it.findViewById(R.id.tv_account)
            mTvPhone = it.findViewById(R.id.tv_phone)
            mRlVersion = it.findViewById(R.id.rl_version)
            mRlCache = it.findViewById(R.id.rl_cache)
            mRlFeedback = it.findViewById(R.id.rl_feedback)
            mBtnLogin = it.findViewById(R.id.btn_quit)
            mTvCompany = it.findViewById(R.id.tv_company)
            mColorChoice = it.findViewById(R.id.rl_color)
//            mTvVersion = it.findViewById(R.id.tv_version)
            // mTvVersion!!.text = "V " + PackageUtils.getVersionName(requireContext())
            if (ifC72()) {
                Thread { initUhf() }.start()
                val power = PreferUtils.getString(context, Constants.c72_power)
                if (!TextUtils.isEmpty(power) && Integer.valueOf(power) >= 0) mTvRef!!.text = power + "dbm"
            }
            if (ifHC720s()) {
                val power = getPower()
                mTvRef!!.text = power.toString() + "dbm"
            }

            mTvCacheTime = it.findViewById(R.id.tv_cache_time)
            val cache_time = PreferUtils.getString(context, Constants.cache_time)
            if (!TextUtils.isEmpty(cache_time)) mTvCacheTime!!.text = "????????????:\n$cache_time"
            val image_total = PreferUtils.getInt(context, Constants.img_total)
            if (image_total > 0) {
                mIvPhotos!!.text = image_total.toString() + "???"
            }
            val color_str = PreferUtils.getString(context, Constants.color_str)
            val color_int = PreferUtils.getInt(context, Constants.color_int)
            // LogUtils.e("color_int  $color_int")
            try {
                if (!color_str.isNullOrEmpty() && color_int != -1) {
                    mTvColor!!.text = color_str.toString()
                    mTvCologBg!!.setBackgroundColor(requireActivity().resources.getColor(color_int))
                } else {
                    mTvColor!!.text = colors[0].colorStr
                    mTvCologBg!!.setBackgroundColor(requireActivity().resources.getColor(colors[0].colorInt))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_setting
    }


    override fun initData() {
        data_img = ArrayList()
        for (i in 1..4) {
            data_img!!.add(i.toString() + "")
        }
        data_rfid = ArrayList()
        for (i in 1..30) {
            data_rfid!!.add(i.toString() + "")
        }
        val login_data = PreferUtils.getString(context, Constants.login_data)
        if (login_data != null) {
            val jsonObject = JSONObject.parseObject(login_data)
            val userName = jsonObject.getString("UserName")
            val mobile = jsonObject.getString("Mobile")
            val companName = jsonObject.getString("CompanName")
            val role = jsonObject.getString("UserRole")
            mTvAccount!!.text = "$userName  [$role]"
            mTvPhone!!.text = "??????: $mobile"
            mTvCompany!!.text = "??????: $companName"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(): SettingFragment {
            val fragment = SettingFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    var mReader: RFIDWithUHF? = null
    fun initUhf() {
        try {
            if (mReader == null) {
                mReader = RFIDWithUHF.getInstance()
                mReader!!.init()
            }
        } catch (ex: ConfigurationException) {
            ex.printStackTrace()
        }
    }

    private val colors: MutableList<ColorChoiceBean> = mutableListOf(
            ColorChoiceBean("??????", R.color.new_theme), ColorChoiceBean("??????", R.color.f08c792),
            ColorChoiceBean("??????", R.color.login_button), ColorChoiceBean("??????", R.color.orange),
            ColorChoiceBean("??????", R.color.black), ColorChoiceBean("??????", R.color.white))

    override fun initListener() {
        mColorChoice!!.setOnClickListener {
            PopColorChoice(requireActivity(), mColorChoice!!, "???????????????", colors, object : GetCommon<ColorChoiceBean> {
                override fun getCommon(t: ColorChoiceBean) {
                    val color_str = t.colorStr
                    mTvColor!!.text = color_str
                    mTvCologBg!!.setBackgroundColor(requireActivity().resources.getColor(t.colorInt))
                    PreferUtils.putString(requireContext(), Constants.color_str, color_str)
                    PreferUtils.putInt(requireContext(), Constants.color_int, t.colorInt)
                }
            })
        }
        /*????????????*/
        mIvRefChoice!!.setOnClickListener { view: View? ->
            when {
                ifC72() -> {
                    PopuChoice(activity, mIvRefChoice, "???????????????", data_rfid) { str: String ->
                        mTvRef!!.text = str + "dbm"
                        mReader!!.power = Integer.valueOf(str)
                        PreferUtils.putString(context, Constants.c72_power, str)
                    }
                }
                ifHC720s() -> {
                    PopuChoice(activity, mIvRefChoice, "???????????????", data_rfid) { str: String ->
                        mTvRef!!.text = str + "dbm"
                        //  mReader!!.power = Integer.valueOf(str)
                        setPower(str.toInt() * 100)
                        //  PreferUtils.putString(context, Constants.c72_power, str)
                    }
                }
                else -> {
                    "?????????????????????????????????".showStr()

                }
            }
        }
        /*????????????*/
        mRlCache!!.setOnClickListener { view: View? ->
            if (NetUtil.checkNet(requireContext())) {
                // showStr(context, "????????????,?????????")
                startDownload()
            } else {
                "?????????????????????".showStr()
                // showStr(context, "?????????????????????")
            }
        }
        mRlVersion!!.setOnClickListener { view: View? ->
            Utils.goToNextUI(ActivityVersion::class.java)
        }
        mRlFeedback!!.setOnClickListener { view: View? ->
//
//            val config = LitePalParser.parseLitePalConfiguration()
//            val path = LitePal.getDatabase().path
//            LogUtils.e("path  $path")
//            val newPath = FileUtil.getSDPath() + Constants.sdk_middle_animal + config.dbName + ".db"
//            if (File(newPath).exists()) File(newPath).delete()
//            fileCopy(File(path), File(newPath))
            DialogFeedBack(requireContext()).show()
        }
        mBtnLogin!!.setOnClickListener { view: View? ->
            //  Utils.goToNextUI(ActivityLogin.class);
            requireActivity().finish()
        }
    }


    private fun startDownload() {
        val intent = Intent(context, DownloadService::class.java)
        requireActivity().startService(intent)
        showAvi("????????????,?????????")
        //?????????????????????
        // DownloadService().javaClass.toString()
        if (receiver == null) receiver = MyReceiver()
        val filter = IntentFilter()
        filter.addAction("tramais.hnb.hhrfid.service.DownLoadService")
        requireActivity().registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) requireActivity().unregisterReceiver(receiver)
    }

    override fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(false)
        }
    }


    private var receiver: MyReceiver? = null

    inner class MyReceiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val down = intent.getIntExtra(Constants.DownLoad_desc, 0)
            if (down == 7) {
                val cacheTime = TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss)
                val cacheTimeToCom = TimeUtil.getFeatDay(7)
                requireActivity().runOnUiThread {
                    mTvCacheTime!!.text = """
                     ????????????:
                     $cacheTime
                     """.trimIndent()
                }
                PreferUtils.putString(context, Constants.cache_time, cacheTime)
                PreferUtils.putString(context, Constants.cache_time_com, cacheTimeToCom)
                hideAvi()
            }
        }
    }

    //????????????
    fun getPower(): Int {
        val apcf2: AntPowerConf = (activity as BaseActivity).uhfReader!!.AntPowerConf()
        val er: READER_ERR = (activity as BaseActivity).uhfReader!!.ParamGet(
                Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf2)
        return if (er == READER_ERR.MT_OK_ERR) {
            (apcf2.Powers[0].readPower) / 100

        } else {
            0
        }
    }

    //????????????
    fun setPower(setPower: Int) {
        val apcf: AntPowerConf = (activity as BaseActivity).uhfReader!!.AntPowerConf()
        apcf.antcnt = 1
        val jaap: AntPower = (activity as BaseActivity).uhfReader!!.AntPower()
        jaap.antid = 1
        jaap.readPower = setPower.toShort()
        jaap.writePower = setPower.toShort()
        apcf.Powers[0] = jaap
        val powerSet: READER_ERR = (activity as BaseActivity).uhfReader!!.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf)
        if (powerSet == READER_ERR.MT_OK_ERR) {
            "????????????".showStr()
        } else {
            "????????????".showStr()
            // toast("???????????? $powerSet")
        }
    }

}