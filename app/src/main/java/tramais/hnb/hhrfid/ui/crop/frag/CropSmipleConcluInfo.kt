package tramais.hnb.hhrfid.ui.crop.frag

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.CropChaKanBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivitySign
import tramais.hnb.hhrfid.ui.crop.CropSmipleActivity
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.UpLoadFileUtil
import tramais.hnb.hhrfid.util.Utils
import java.util.*

class CropSmipleConcluInfo : BaseFragment() {
    private lateinit var mFarmerSign: ImageView
    private lateinit var mBtnFarmerSign: Button
    private lateinit var mEmployeeSign: ImageView
    private lateinit var mBtnEmployeeSign: Button
    private lateinit var mZhuanjiaSign: ImageView
    private lateinit var mBtnZhuanjiaSign: Button
    private lateinit var mInputInfo:
            EditText
    override fun initImmersionBar() {

    }


    override fun findViewById(view: View?) {
        view?.let {


            mInputInfo = it.findViewById(R.id.input_info)
            mBtnZhuanjiaSign = it.findViewById(R.id.btn_zhuanjia_sign)
            mZhuanjiaSign = it.findViewById(R.id.zhuanjia_sign)
            mBtnEmployeeSign = it.findViewById(R.id.btn_employee_sign)
            mEmployeeSign = it.findViewById(R.id.employee_sign)
            mBtnFarmerSign = it.findViewById(R.id.btn_farmer_sign)
            mFarmerSign = it.findViewById(R.id.farmer_sign)
            mOnlySave = it.findViewById(R.id.only_save)


        }
    }

    override fun setLayoutId(): Int {
        return R.layout.crop_fragment_smiple_conclu
    }

    override fun initListener() {
        mBtnZhuanjiaSign.setOnClickListener { v: View? ->
            val intent = Intent(context, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "鉴定机构/技术专家签名")
            intent.putExtra(Constants.Sign_Common, "")
            intent.putExtra(Constants.Type, "种植")
            startActivityForResult(intent, Config.REQUEST_SING)
        }
        mBtnEmployeeSign.setOnClickListener { v: View? ->
            val intent = Intent(context, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "查勘人签名")
            intent.putExtra(Constants.Sign_Common, "")
            intent.putExtra(Constants.Type, "种植")
            startActivityForResult(intent, 123)
        }
        mBtnFarmerSign.setOnClickListener { v: View? ->
            val intent = Intent(context, ActivitySign::class.java)
            intent.putExtra(Constants.For_Sign, "被保险人签名")
            intent.putExtra(Constants.Sign_Common, "")
            intent.putExtra(Constants.Type, "种植")
            startActivityForResult(intent, 124)
        }
        mOnlySave.setOnClickListener { save() }
    }

    override fun initData() {
        cropChaKanBean = CropSmipleActivity.cropChaKanBean
    }

    var cropChaKanBean: CropChaKanBean? = null


    private var zhuanjia_sign: String? = null
    private var employee_sign: String? = null
    private var farmer_sign: String? = null
    private lateinit var mOnlySave:
            Button


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.REQUEST_SING) {
                zhuanjia_sign = data!!.getStringExtra(Constants.Sign_Path)
                if (zhuanjia_sign!!.isNotEmpty()) {
                    Glide.with(requireContext()).load(zhuanjia_sign).into(mZhuanjiaSign)
                }
            } else if (requestCode == 123) {
                employee_sign = data!!.getStringExtra(Constants.Sign_Path)
                if (employee_sign!!.isNotEmpty()) {
                    Glide.with(requireContext()).load(employee_sign).into(mEmployeeSign)
                }
            } else if (requestCode == 124) {
                farmer_sign = data!!.getStringExtra(Constants.Sign_Path)
                if (farmer_sign!!.isNotEmpty()) {
                    Glide.with(requireContext()).load(farmer_sign).into(mFarmerSign)
                }
            }
        }
    }

    fun save() {
        val signs: MutableMap<String, String?> = HashMap()
        signs["抽样专家签名"] = zhuanjia_sign.toString()
        signs["抽样查勘员签名"] = employee_sign.toString()
        signs["抽样农户签名"] = farmer_sign.toString()
        UpLoadFileUtil.upLoadFile(context, TimeUtil.getTime(Constants.yyyyMMddHHmmss1), signs) { list ->
            //LogUtils.e("list $list")
         /*   RequestUtil.getInstance(context)!!.SaveChouYangSign(cropChaKanBean!!.fNumber, Utils.getEdit(mInputInfo), list.get(0), list.get(1), list.get(2)
            ) { rtnCode, message -> message.showStr() }*/


        }
    }


}