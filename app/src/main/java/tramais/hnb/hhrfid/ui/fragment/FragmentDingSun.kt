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
import tramais.hnb.hhrfid.litePalBean.BaoAnListCache
import tramais.hnb.hhrfid.litePalBean.DingSunCache
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.net.SaveAllCache
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.ui.view.LinePathView
import tramais.hnb.hhrfid.util.*

class FragmentDingSun : Fragment() {
    var sheep_snow: EditText? = null
    var sheep_ill: EditText? = null
    var sheep_animal: EditText? = null
    var sheep_other: EditText? = null
    var cow_snow: EditText? = null
    var cow_ill: EditText? = null
    var cow_animal: EditText? = null
    var cow_other: EditText? = null
    var rl_sign: RelativeLayout? = null
    var iv_sign: ImageView? = null
    lateinit var fenPei: FenPei
    var save: Button? = null
    private lateinit var mBananNum: TextView
    private lateinit var mCleanSign: Button
    private lateinit var mLinePathView: LinePathView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_dingsun, container, false)

        initView(inflate)
        initListener()
        return inflate
    }

    private fun initListener() {
        mCleanSign.setOnClickListener {
            if (rl_sign!!.visibility == VISIBLE) {
                if (mLinePathView.touched)
                    mLinePathView.clear()
            } else {
                rl_sign!!.visibility = VISIBLE
                iv_sign!!.visibility = GONE
            }

        }
        save!!.setOnClickListener { regist() }
    }

    val sing: MutableList<String> = ArrayList()
    fun initView(rootView: View) {
        fenPei = ActivityFeedCheck.fenPei


        mCleanSign = rootView.findViewById<Button>(R.id.clean_sign)
        mLinePathView = rootView.findViewById<LinePathView>(R.id.line_path_view)

        mBananNum = rootView.findViewById<TextView>(R.id.banan_num)
        mBananNum.text = fenPei.number
        sheep_snow = rootView.findViewById<EditText>(R.id.et_sheep_snow)
        sheep_ill = rootView.findViewById<EditText>(R.id.et_sheep_ill)
        sheep_animal = rootView.findViewById<EditText>(R.id.et_sheep_animal)
        sheep_other = rootView.findViewById<EditText>(R.id.et_sheep_other)
        cow_snow = rootView.findViewById<EditText>(R.id.et_cow_snow)
        cow_ill = rootView.findViewById<EditText>(R.id.et_cow_ill)
        cow_animal = rootView.findViewById<EditText>(R.id.et_cow_animal)
        cow_other = rootView.findViewById<EditText>(R.id.et_cow_other)
        save = rootView.findViewById(R.id.save)
        rl_sign = rootView.findViewById<RelativeLayout>(R.id.rl_sign)
        iv_sign = rootView.findViewById(R.id.iv_sign)
        getDetail(fenPei.number)
    }

    fun regist() {

        val creator = PreferUtils.getString(context, Constants.UserName)
        if (rl_sign!!.visibility == VISIBLE)
            if (!mLinePathView.touched) {
                showStr("请签名")
                return
            }



        if (NetUtil.checkNet(context)) {
            val path = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "_" + "sign.jpg"
            mLinePathView.save(path)

            sing.add(path)
            UpLoadFileUtil.upLoadFile(context, "签名", fenPei.number, sing, GetList { list ->
                RequestUtil.getInstance(context)!!.saveLossValue(fenPei.number, Utils.getEdit(sheep_snow), Utils.getEdit(sheep_ill), Utils.getEdit(sheep_animal), Utils.getEdit(sheep_other),
                        Utils.getEdit(cow_snow), Utils.getEdit(cow_ill), Utils.getEdit(cow_animal), Utils.getEdit(cow_other), list[0], creator, { rtnCode, message ->
                    showStr(message)
                })
            })
        } else {
            dingSunCache_sing = FileUtil.getSDPath() + Constants.sdk_middle_animal + fenPei.number + "_" + "sign.jpg"
            mLinePathView.save(dingSunCache_sing)
            SaveAllCache.SAVE_ALL_CACHE.saveDingSun_sure(fenPei, Utils.getEdit(sheep_snow), Utils.getEdit(sheep_ill), Utils.getEdit(sheep_animal), Utils.getEdit(sheep_other),
                    Utils.getEdit(cow_snow), Utils.getEdit(cow_ill), Utils.getEdit(cow_animal), Utils.getEdit(cow_other), dingSunCache_sing, object : GetOneString {
                override fun getString(str: String?) {
                    showStr(str)
                    if (str!!.contains("成功")) {
                        var cache = BaoAnListCache()
                        cache.status = "已定损"
                        cache.updateAll("number =?", fenPei.number)
                    }
                }
            })
        }


    }

    fun showStr(show: String?) {
        if (!TextUtils.isEmpty(show)) {
            Toast.makeText(context, show, Toast.LENGTH_SHORT).show()
        }
    }

    var dingSunCache_sing = ""
    fun getDetail(num: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getLossDetail(num) {
                if (it.code == 0) {
                    requireActivity().run {
                        sheep_snow!!.setText(it.sheepReduceAmt.toString())
                        sheep_ill!!.setText(it.sheepMarketAmt.toString())
                        sheep_animal!!.setText(it.sheepLPRatio.toString())
                        sheep_other!!.setText(it.sheepLPAmt.toString())
                        cow_snow!!.setText(it.cowReduceAmt.toString())
                        cow_ill!!.setText(it.cowMarketAmt.toString())
                        cow_animal!!.setText(it.cowLPRatio.toString())
                        cow_other!!.setText(it.cowLpAmt.toString())
                        iv_sign!!.visibility = View.VISIBLE
                        rl_sign!!.visibility = GONE
                        context?.let { it1 -> Glide.with(it1).load(it.checkerSignature).into(iv_sign!!) }
                    }
                } else {
                    iv_sign!!.visibility = GONE
                    rl_sign!!.visibility = VISIBLE
                }
            }
        } else {
            LitePal.where("number =?", fenPei.number).findAsync(DingSunCache::class.java).listen {
                if (it != null && it.size > 0) {
                    val dingSunCache = it[0]
                    activity!!.run {
                        sheep_snow!!.setText(dingSunCache.sheep_slavge)
                        sheep_ill!!.setText(dingSunCache.sheep_market)
                        sheep_animal!!.setText(dingSunCache.sheep_ratio)
                        sheep_other!!.setText(dingSunCache.sheep_lpr)
                        cow_snow!!.setText(dingSunCache.cow_slavge)
                        cow_ill!!.setText(dingSunCache.cow_market)
                        cow_animal!!.setText(dingSunCache.cow_ratio)
                        cow_other!!.setText(dingSunCache.cow_lpr)
                        iv_sign!!.visibility = View.VISIBLE
                        rl_sign!!.visibility = GONE
                        dingSunCache_sing = dingSunCache.repet_sign.toString()
                        context?.let { it1 -> Glide.with(it1).load(dingSunCache.repet_sign).into(iv_sign!!) }
                    }
                } else {
                    iv_sign!!.visibility = GONE
                    rl_sign!!.visibility = VISIBLE
                }
            }

        }

    }
}