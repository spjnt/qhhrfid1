package tramais.hnb.hhrfid.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import org.litepal.LitePal.findAllAsync
import org.litepal.LitePal.where
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetResultJsonObject
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.lv.ImageAdapterNew
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.Utils
import java.io.File
import java.util.*

class ActivitySearchByEarTag : BaseActivity() {
    var lables: MutableList<String?>? = ArrayList()
    private var mEtEarTag: AutoCompleteTextView? = null
    private var mTvFarm: TextView? = null
    private var mTvIds: TextView? = null
    private var mTvCheckTime: TextView? = null

    //    private var mTvSatate: TextView? = null
    private val context: Context = this@ActivitySearchByEarTag
    private val url: MutableList<String?>? = ArrayList()
    private var mRvPhotos: RecyclerView? = null
    private var mSearchLable: Button? = null

    //    private var imageAdapter: ImageAdapter? = null
    private val handler: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                IMAGE_LIST -> {
                    val urls = msg.obj as MutableList<String>
                    mAdapter!!.setGroupList(urls)


                }
                GET_EPC_C72 -> {
                    val epc = msg.obj as String
                    if (!TextUtils.isEmpty(epc)) {
                        mEtEarTag!!.setText(epc)
                        if (!epc.contains("停止扫描,请重新按键")) {
                            doSearch(Utils.getEdit(mEtEarTag))
                        }
                    }
                }
            }
        }
    }
    private var mIvRfidScan: ImageView? = null
    private var allLables: MutableList<String?>? = null
    private val delayRun: Runnable? = Runnable { doSearch(Utils.getEdit(mEtEarTag)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_id)
    }

    private var mAdapter: ImageAdapterNew? = null
    override fun initView() {
        setTitleText("耳标查询")
        // setTitlTextColor(R.color.light_blue)
        mEtEarTag = findViewById(R.id.et_ear_tag)
        mTvFarm = findViewById(R.id.tv_farm)
        mTvIds = findViewById(R.id.tv_ids)
        mTvCheckTime = findViewById(R.id.tv_check_time)
//        mTvSatate = findViewById(R.id.tv_satate)
        mRvPhotos = findViewById(R.id.rv_photos)
        mSearchLable = findViewById(R.id.search_lable)

        mAdapter = ImageAdapterNew(this)

        mRvPhotos!!.layoutManager = GridLayoutManager(context, 2)
        mRvPhotos!!.adapter = mAdapter
        mIvRfidScan = findViewById(R.id.iv_rfid_scan)
    }

    override fun initData() {
        if (!NetUtil.checkNet(context)) allLables = getAllLables()
        val intent = intent
        if (intent != null) {
            val eatTag = intent.getStringExtra(Constants.animal_lable)
            if (!TextUtils.isEmpty(eatTag)) {
                mEtEarTag!!.setText(eatTag)
                if (NetUtil.checkNet(context)) doSearchOnLine(Utils.getEdit(mEtEarTag)) else doSearch(Utils.getEdit(mEtEarTag))
            }
        }
        mEtEarTag!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!NetUtil.checkNet(context)) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //延迟600ms，如果不再输入字符，则执行该线程的run方法
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 600)
                }
            }
        })
    }

    override fun initListner() {
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val path = it.getItem(position)

                if (!path!!.contains("http")) {
                    val file = File(path)
                    if (file.exists()) {
                        DialogImg(context, path).show()
                    } else {
                        showStr("本地文件已清除")
                    }
                } else {
                    DialogImg(context, path).show()
                }
            }
        }
        mSearchLable!!.setOnClickListener { view: View? ->
            if (TextUtils.isEmpty(Utils.getEdit(mEtEarTag))) {
                showStr("请输入耳标号")
                return@setOnClickListener
            }
            if (NetUtil.checkNet(this)) {
                doSearchOnLine(Utils.getEdit(mEtEarTag))
            } else {
                doSearch(Utils.getEdit(mEtEarTag))
            }
        }
        mIvRfidScan!!.setOnClickListener { view: View? -> if (ifC72()) ReadTag(mReader, handler!!) }
    }

    private fun getAllLables(): MutableList<String?>? {
        if (lables != null) lables!!.clear()
        findAllAsync(AnimalSaveCache::class.java).listen { list: List<AnimalSaveCache>? ->
            if (list != null && list.size > 0) {
                for (item in list) {
                    val lableNum = item.lableNum
                    if (!TextUtils.isEmpty(lableNum) && !lables!!.contains(lableNum)) lables!!.add(lableNum)
                }
            }
        }
        return lables
    }

    private fun doSearchOnLine(lableNum: String) {
        if (TextUtils.isEmpty(lableNum)) {
            showStr("请输入耳标号搜索")
            return
        }
        url?.clear()
        showAvi()
        RequestUtil.getInstance(context)!!.searchByLable(lableNum, object : GetResultJsonObject {
            override fun getResult(rtnCode: Int, message: String?, totalNums: Int, datas: JSONObject?) {

                hideAvi()
                if (rtnCode >= 0 && datas != null) {
                    val farmerName = datas.getString("FarmerName")
                    mTvFarm!!.text = farmerName
                    val zjNumber = datas.getString("ZjNumber")
                    mTvIds!!.text = zjNumber
                    val checkTime = datas.getString("CheckTime")
                    mTvCheckTime!!.text = checkTime
                    val insureTime = datas.getString("InsureTime")
                    val pictureUrl1 = datas.getString("PictureUrl1")
                    val pictureUrl2 = datas.getString("PictureUrl2")
                    val pictureUrl3 = datas.getString("PictureUrl3")
                    val pictureUrl4 = datas.getString("PictureUrl4")
                    val status = datas.getString("Status")
//                    mTvSatate!!.text = status
                    if (!TextUtils.isEmpty(pictureUrl1)) url!!.add(pictureUrl1)
                    if (!TextUtils.isEmpty(pictureUrl2)) url!!.add(pictureUrl2)
                    if (!TextUtils.isEmpty(pictureUrl3)) url!!.add(pictureUrl3)
                    if (!TextUtils.isEmpty(pictureUrl4)) url!!.add(pictureUrl4)
                    val messag = Message()
                    messag.obj = url
                    messag.what = IMAGE_LIST
                    handler!!.sendMessage(messag)
                } else {
                    showStr(message)
                }
            }
        })
    }

    private fun doSearch(lableNum: String) {
        if (TextUtils.isEmpty(lableNum)) return
        url?.clear()
        where("LableNum = ?", lableNum).findAsync(AnimalSaveCache::class.java).listen { list: List<AnimalSaveCache>? ->
            if (list != null && list.size > 0) {
                val saveCache = list[0]
                mTvFarm!!.text = saveCache.farmName
                mTvIds!!.text = saveCache.farmID
                mTvCheckTime!!.text = saveCache.creatTime
//                mTvSatate!!.text = "新建"
                for (cache in list) {
                    if (!TextUtils.isEmpty(cache.img1)) url!!.add(cache.img1)
                    if (!TextUtils.isEmpty(cache.img2)) url!!.add(cache.img2)
                    if (!TextUtils.isEmpty(cache.img3)) url!!.add(cache.img3)
                    if (!TextUtils.isEmpty(cache.img4)) url!!.add(cache.img4)
                }
                val messag = Message()
                messag.obj = url
                messag.what = IMAGE_LIST
                handler!!.sendMessage(messag)
            } else {
                mTvFarm!!.text = ""
                mTvIds!!.text = ""
                mTvCheckTime!!.text = ""
//                mTvSatate!!.text = "新建"
                url!!.clear()
                mRvPhotos!!.adapter = null
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280) {
            ReadTag(mReader, handler!!)
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val IMAGE_LIST = 1 shl 1
        private const val LABLE_LIST = 1 shl 2
    }
}