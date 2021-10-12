package tramais.hnb.hhrfid.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_check.*
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
import tramais.hnb.hhrfid.ui.dialog.DialogImg
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

    override fun initImmersionBar() {

    }

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
        if (NetUtil.checkNet(context)) {
            showAvi()
            RequestUtil.getInstance(context)!!.saveChaKan(fenPei!!, Utils.getText(checkDate), Utils.getEdit(address), Utils.getEdit(sunshi), Utils.getEdit(guji), Utils.getEdit(advice), name, null, TimeUtil.getTime(Constants.yyyy_mm_dd), TimeUtil.getTime(Constants.yyyy_mm_dd)) { rtnCode, message ->
                hideAvi()
                message.showStr()
            }
            /* UpLoadFileUtil.upLoadFile(context, "查勘", fenPei!!.number, mAdapter!!.data) { list ->
                 if (list.size == fileUrlLists!!.size)
                     RequestUtil.getInstance(context)!!.saveChaKan(fenPei!!, Utils.getText(checkDate), Utils.getEdit(address), Utils.getEdit(sunshi), Utils.getEdit(guji), Utils.getEdit(advice), name, list, TimeUtil.getTime(Constants.yyyy_mm_dd), TimeUtil.getTime(Constants.yyyy_mm_dd)) { rtnCode, message ->
                         hideAvi()
                         message.showStr()
                     }
             }*/
        } else {
            SaveAllCache.SAVE_ALL_CACHE.saveCheck_info(fenPei!!, Utils.getText(checkDate), Utils.getEdit(address), Utils.getEdit(sunshi), Utils.getEdit(guji), Utils.getEdit(advice), null, object : GetOneString {
                override fun getString(str: String?) {
                    str.showStr()
                    if (str!!.contains("成功")) {
                        var cache = BaoAnListCache()
                        cache.status = "已查勘"
                        val updateAll = cache.updateAll("number =?", fenPei!!.number)
                        LogUtils.e("查勘缓存" + if (updateAll >= 1) "更新缓存成功" else "更新缓存失败")

                    }
                }
            })
        }
    }

    override fun initData() {
        fenPei = ActivityFeedCheck.fenPei
        getChaKanDetail(fenPei!!.number)
    }


    fun getChaKanDetail(number: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getChaKanDetail(number) { detail ->
                detail?.let {
                    requireActivity()!!.runOnUiThread {

                        if (it.chaKanDate.isNullOrBlank())
                            checkDate!!.text = TimeUtil.getTime(Constants.yyyy_mm_dd)
                        else     checkDate!!.text = it.chaKanDate
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
//                    fileUrlLists?.clear()
//                    Utils.stringToList(chaKanCache.chankanphoto)?.let { fileUrlLists!!.addAll(it) }
//                    showPhoto(fileUrlLists)
                }
            }
        }

    }

//    override fun onPause() {
//        super.onPause()
//        //   LogUtils.e("onPause")
//        // saveInfo()
//    }
//
//    var fileUrlLists: MutableList<String>? = ArrayList()
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 124 && resultCode == Activity.RESULT_OK) {
//            tv_name.clearFocus()
//            val stringArrayListExtra = data!!.getStringArrayListExtra("imgs")
//            if (stringArrayListExtra != null) {
//                fileUrlLists!!.addAll(0, stringArrayListExtra)
//            }
//            rvPhoto!!.smoothScrollToPosition(fileUrlLists!!.size - 1)
//            showPhoto(fileUrlLists)
//        }
//    }

//    fun showPhoto(imgs: MutableList<String>?) {
//        if (imgs == null || imgs.size == 0) return
//
//        // rvPhoto!!.focusable
//        // rvPhoto!!.smoothScrollToPosition(-1)
//        mAdapter?.let {
//
//            it.setList(imgs)
//        }
//    }
//
//    //检查权限
//    private fun checkPermission(): Boolean {
//        //是否有权限
//        val haveCameraPermission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } == PackageManager.PERMISSION_GRANTED
//        val haveWritePermission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED
//        return haveCameraPermission && haveWritePermission
//    }
//
//    // 请求所需权限
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private fun requestPermissions() {
//        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
//    }
//
//    // 请求权限后会在这里回调
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            101 -> {
//                var allowAllPermission = false
//                var i = 0
//                while (i < grantResults.size) {
//                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //被拒绝授权
//                        allowAllPermission = false
//                        break
//                    }
//                    allowAllPermission = true
//                    i++
//                }
//                if (allowAllPermission) {
//
//                } else {
//                    Toast.makeText(context, "该功能需要授权方可使用", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    var mLocationClient: LocationClient? = null
    override fun onResume() {
        super.onResume()

        if (NetUtil.checkNet(context)) {
            mLocationClient = LocationClient(context)
            BDLoactionUtil.initLoaction(mLocationClient)
            mLocationClient!!.start()
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String ->
                if (!TextUtils.isEmpty(add)) {
                    address!!.setText(add)
                    mLocationClient!!.stop()
                }
            })
        }
    }
}