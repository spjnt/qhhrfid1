package tramais.hnb.hhrfid.ui

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
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.ColorChoiceBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.service.DownloadService
import tramais.hnb.hhrfid.ui.dialog.DialogFeedBack
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.ui.popu.PopuColorChoice
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

    //    private var mTvVersion: TextView? = null
    private var mTvCacheTime: TextView? = null
    private var mTvCompany: TextView? = null
    override fun findViewById(view: View?) {
        view?.let {
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
            val power = PreferUtils.getString(context, Constants.c72_power)

            if (!TextUtils.isEmpty(power) && Integer.valueOf(power) >= 0) mTvRef!!.text = power + "dbm"


            mTvCacheTime = it.findViewById(R.id.tv_cache_time)
            val cache_time = PreferUtils.getString(context, Constants.cache_time)
            if (!TextUtils.isEmpty(cache_time)) mTvCacheTime!!.text = "更新时间:\n$cache_time"
            val image_total = PreferUtils.getInt(context, Constants.img_total)
            if (image_total > 0) {
                mIvPhotos!!.text = image_total.toString() + "张"
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
            mTvAccount!!.text = userName
            mTvPhone!!.text = "手机: $mobile"
            mTvCompany!!.text = "公司: $companName"
        }
        if (ifC72()) {
            Thread { initUhf() }.start()
        }
        val color_str = PreferUtils.getString(context, Constants.color_str)
        val color_int = PreferUtils.getInt(context, Constants.color_int)
        if (color_str.isNullOrEmpty() && color_int == -1) {
            mTvColor!!.text = colors[0].colorStr
            mTvCologBg!!.setBackgroundColor(resources.getColor(colors[0].colorInt))
        } else {
            mTvColor!!.text = color_str
            mTvCologBg!!.setBackgroundColor(resources.getColor(color_int))
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
            //   "请确定是否使用正确的设备".showStr()

            ex.printStackTrace()
        }
    }

    val colors: MutableList<ColorChoiceBean> = mutableListOf(ColorChoiceBean("红色", R.color.new_theme),
            ColorChoiceBean("绿色", R.color.f08c792),
            ColorChoiceBean("蓝色", R.color.login_button), ColorChoiceBean("橙色", R.color.orange),
            ColorChoiceBean("黑色", R.color.black), ColorChoiceBean("白色", R.color.white))

    override fun initListener() {
        mColorChoice!!.setOnClickListener {
            PopuColorChoice(requireActivity(), mColorChoice!!, "请选择颜色", colors, object : GetCommon<ColorChoiceBean> {
                override fun getCommon(t: ColorChoiceBean) {
                    mTvColor!!.text = t.colorStr
                    mTvCologBg!!.setBackgroundColor(resources.getColor(t.colorInt))
                    PreferUtils.putString(requireContext(), Constants.color_str, t.colorStr)
                    PreferUtils.putInt(requireContext(), Constants.color_int, t.colorInt)
                }
            })
        }
        /*设置照片数量*/
        mRlPhotoChoice!!.setOnClickListener { view: View? ->
            PopuChoice(activity, mRlPhotoChoice, "请选择照片数量", data_img) { str: String ->
                mIvPhotos!!.text = str + "张"
                PreferUtils.putInt(context, Constants.img_total, Integer.valueOf(str))
            }
        }
        /*设置功率*/mIvRefChoice!!.setOnClickListener { view: View? ->
            if (ifC72()) {
                PopuChoice(activity, mIvRefChoice, "请设置功率", data_rfid) { str: String ->
                    mTvRef!!.text = str + "dbm"
                    mReader!!.power = Integer.valueOf(str)
                    PreferUtils.putString(context, Constants.c72_power, str)
                }
            } else {
                "仅专用设备支持功率设置".showStr()

            }
        }
        /*更新缓存*/mRlCache!!.setOnClickListener { view: View? ->
            if (NetUtil.checkNet(requireContext())) {
                // showStr(context, "正在缓存,请稍等")
                startDownload()

            } else {
                "请确认是否联网".showStr()
                // showStr(context, "请确认是否联网")
            }
        }
        mRlVersion!!.setOnClickListener { view: View? ->
            Utils.goToNextUI(ActivityVersion::class.java)
        }
        mRlFeedback!!.setOnClickListener { view: View? -> DialogFeedBack(requireContext()).show() }
        mBtnLogin!!.setOnClickListener { view: View? ->
            //  Utils.goToNextUI(ActivityLogin.class);
            requireActivity().finish()

        }
    }


    private fun startDownload() {
        val intent = Intent(context, DownloadService::class.java)
        requireActivity().startService(intent)
        showAvi("正在下载,请稍等")
        //注册广播接收器
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
        override fun onReceive(context: Context, intent: Intent) {
            val down = intent.getIntExtra(Constants.DownLoad_desc, 0)
            if (down == 6) {
                mTvCacheTime!!.text = """
                     更新时间:
                     ${TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss)}
                     """.trimIndent()
                PreferUtils.putString(context, Constants.cache_time, TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
                hideAvi()
            }
        }
    }

}