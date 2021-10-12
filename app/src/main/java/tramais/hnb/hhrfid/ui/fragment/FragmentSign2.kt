/*
package tramais.hnb.hhrfid.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetList
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.litePalBean.DingSunCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.net.SaveAllCache
import tramais.hnb.hhrfid.ui.ActivityFeedCheck2
import tramais.hnb.hhrfid.ui.view.LinePathView
import tramais.hnb.hhrfid.util.FileUtil
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.UpLoadFileUtil
import tramais.hnb.hhrfid.util.Utils

class FragmentSign2 : Fragment() {
    lateinit var rootView: View
    private lateinit var mCleanSignFarmer: Button
    private lateinit var mLineFarmer: LinePathView
    private lateinit var mTelFarmer: EditText
    private lateinit var mCleanCheck: Button
    private lateinit var mLineCheck: LinePathView
    private lateinit var mTvBaoanName: TextView
    private lateinit var mTvName: EditText
    private lateinit var mSave: Button
    private lateinit var rlFarmSign: RelativeLayout
    private lateinit var ivFarmerSign: ImageView
    private lateinit var rlCheckSign: RelativeLayout
    private lateinit var ivCheckSign: ImageView
    lateinit var fenPei: FenPei
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_sign, container, false)
        initView(rootView)
        initListener()
        return rootView
    }

    private fun initView(rootView: View) {
        fenPei = ActivityFeedCheck2.fenPei
        mCleanSignFarmer = rootView.findViewById<Button>(R.id.clean_sign_farmer)
        mLineFarmer = rootView.findViewById<LinePathView>(R.id.line_farmer)
        mTelFarmer = rootView.findViewById<EditText>(R.id.tel_farmer)
        mCleanCheck = rootView.findViewById<Button>(R.id.clean_check)
        mLineCheck = rootView.findViewById<LinePathView>(R.id.line_check)
        mTvBaoanName = rootView.findViewById<TextView>(R.id.tv_baoan_name)
        mTvName = rootView.findViewById<EditText>(R.id.tv_name)
        mSave = rootView.findViewById<Button>(R.id.save)
        rlFarmSign = rootView.findViewById(R.id.rl_farmer)
        ivFarmerSign = rootView.findViewById(R.id.iv_farm_sign)
        rlCheckSign = rootView.findViewById(R.id.rl_check)
        ivCheckSign = rootView.findViewById(R.id.iv_check_sign)
        mTelFarmer.setText(fenPei.mobile)
        getDetail(fenPei.number)
    }

    fun initListener() {
        mCleanSignFarmer.setOnClickListener {
            if (rlFarmSign.visibility == VISIBLE) {
                if (mLineFarmer.touched) {
                    mLineFarmer.clear()
                }
            } else {
                ivFarmerSign.visibility = GONE
                rlFarmSign.visibility = VISIBLE
            }

        }
        mCleanCheck.setOnClickListener {
            if (rlCheckSign.visibility == VISIBLE) {
                if (mLineCheck.touched)
                    mLineCheck.clear()
            } else {
                ivCheckSign.visibility = GONE
                rlCheckSign.visibility = VISIBLE
            }


        }
        mSave.setOnClickListener { regist() }
    }

    var sing: MutableList<String> = ArrayList()
    fun regist() {
        sing.clear()
        if (rlFarmSign.visibility == VISIBLE)
            if (!mLineFarmer.touched) {
                showStr("请被保险人签名")
                return
            }
        val farmer_mobile = Utils.getEdit(mTelFarmer)
        if (TextUtils.isEmpty(farmer_mobile)) {
            showStr("请输入被保险人电话")
            return
        }
        if (!Utils.isPhoneNum(farmer_mobile)) {
            showStr("请输入正确的被保险人电话")
            return
        }
        if (rlCheckSign.visibility == VISIBLE)
            if (!mLineCheck.touched) {
                showStr("请查勘人签名")
                return
            }
        val employee_mobile = Utils.getEdit(mTvName)
        if (TextUtils.isEmpty(employee_mobile)) {
            showStr("请输入被保险人电话")
            return
        }
        if (!Utils.isPhoneNum(employee_mobile)) {
            showStr("请输入正确查勘人电话")
            return
        }


        if (NetUtil.checkNet(context)) {
            showStr("签名信息上传中...")
            if (rlFarmSign.visibility == VISIBLE && rlCheckSign.visibility == VISIBLE) {
                val path1 = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "农户" + "-" + "sign.jpg"
                val path2 = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "员工" + "-" + "sign.jpg"
                mLineFarmer.save(path1)
                mLineCheck.save(path2)
                sing.add(path1)
                sing.add(path2)
                UpLoadFileUtil.upLoadFile(context, "签名", fenPei.number, sing, GetList { list ->
                    RequestUtil.getInstance(context)!!.saveLossSign(fenPei.number, list[0], Utils.getEdit(mTelFarmer), list[1], Utils.getEdit(mTvName)) { rtnCode, str ->
                        showStr(str)
                    }

                })
            } else if (rlFarmSign.visibility == VISIBLE && rlCheckSign.visibility == GONE) {
                val path1 = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "农户" + "-" + "sign.jpg"
                sing.add(path1)
                mLineFarmer.save(path1)
                UpLoadFileUtil.upLoadFile(context, "签名", fenPei.number, sing, GetList { list ->
                    RequestUtil.getInstance(context)!!.saveLossSign(fenPei.number, list[0], Utils.getEdit(mTelFarmer), checkSign, Utils.getEdit(mTvName)) { rtnCode, str ->
                        showStr(str)
                    }

                })
            } else if (rlFarmSign.visibility == GONE && rlCheckSign.visibility == VISIBLE) {
                val path1 = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "员工" + "-" + "sign.jpg"
                sing.add(path1)
                mLineCheck.save(path1)
                UpLoadFileUtil.upLoadFile(context, "签名", fenPei.number, sing, GetList { list ->
                    RequestUtil.getInstance(context)!!.saveLossSign(fenPei.number, farmSign, Utils.getEdit(mTelFarmer), list[0], Utils.getEdit(mTvName)) { rtnCode, str ->
                        showStr(str)
                    }

                })
            } else {
                UpLoadFileUtil.upLoadFile(context, "签名", fenPei.number, sing, GetList { list ->
                    RequestUtil.getInstance(context)!!.saveLossSign(fenPei.number, farmSign, Utils.getEdit(mTelFarmer), checkSign, Utils.getEdit(mTvName)) { rtnCode, str ->
                        showStr(str)
                    }

                })
            }
        } else {
            farmSign = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "农户" + "-" + "sign.jpg"
            checkSign = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "员工" + "-" + "sign.jpg"
            mLineFarmer.save(farmSign)
            mLineCheck.save(checkSign)
            SaveAllCache.SAVE_ALL_CACHE.saveDingSun_sign(fenPei.number.toString(), farmSign, Utils.getEdit(mTelFarmer), checkSign, Utils.getEdit(mTvName), object : GetOneString {
                override fun getString(str: String?) {
                    showStr(str)
                }
            })
        }


    }

    fun showStr(show: String?) {
        if (!TextUtils.isEmpty(show)) {
            activity!!.runOnUiThread { Toast.makeText(context, show, Toast.LENGTH_SHORT).show() }

        }
    }

    var farmSign = ""
    var checkSign = ""
    fun getDetail(num: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getLossDetail(num) {
                if (it.code == 0) {
                    activity!!.run {
                        mTelFarmer.setText(it.farmerMobile)
                        mTvName.setText(it.employeeMobile)
                        farmSign = it.farmerSignature
                        checkSign = it.employeeSignature
                        val farmerSignature = it.farmerSignature
                        val employeeSignature = it.employeeSignature
                        if (!TextUtils.isEmpty(farmerSignature) && farmerSignature != null) {
                            rlCheckSign.visibility = GONE
                            ivFarmerSign.visibility = VISIBLE
                            context?.let { it1 -> Glide.with(it1).load(farmerSignature).into(ivFarmerSign) }
                        }
                        if (!TextUtils.isEmpty(employeeSignature) && employeeSignature != null) {
                            rlFarmSign.visibility = GONE
                            ivCheckSign.visibility = VISIBLE
                            context?.let { it1 -> Glide.with(it1).load(employeeSignature).into(ivCheckSign) }
                        }


                    }
                } else {
                    rlCheckSign.visibility = VISIBLE
                    rlFarmSign.visibility = VISIBLE
                    ivFarmerSign.visibility = GONE
                    ivCheckSign.visibility = GONE
                }
            }
        } else {
            LitePal.where("number =?", fenPei.number).findAsync(DingSunCache::class.java).listen {
                if (it != null && it.size > 0) {
                    val dingSunCache = it[0]
                    activity!!.run {
                        mTelFarmer.setText(dingSunCache.farmer_mobile)
                        mTvName.setText(dingSunCache.check_mobile)
                        farmSign = dingSunCache.farmer_sign.toString()
                        checkSign = dingSunCache.check_sign.toString()

                        if (!TextUtils.isEmpty(farmSign) && farmSign != null) {
                            rlCheckSign.visibility = GONE
                            ivFarmerSign.visibility = VISIBLE
                            context?.let { it1 -> Glide.with(it1).load(farmSign).into(ivFarmerSign) }
                        }
                        if (!TextUtils.isEmpty(checkSign) && checkSign != null) {
                            rlFarmSign.visibility = GONE
                            ivCheckSign.visibility = VISIBLE
                            context?.let { it1 -> Glide.with(it1).load(checkSign).into(ivCheckSign) }
                        }
                    }
                } else {
                    rlCheckSign.visibility = VISIBLE
                    rlFarmSign.visibility = VISIBLE
                    ivFarmerSign.visibility = GONE
                    ivCheckSign.visibility = GONE
                }
            }
        }

    }
}*/
