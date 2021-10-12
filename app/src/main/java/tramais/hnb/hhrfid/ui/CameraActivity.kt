package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.camerakit.CameraKitView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.LitePal.findAll
import org.litepal.LitePal.findAllAsync
import org.litepal.LitePal.where
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.CropTypeList
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.litePalBean.AnimalCateGoryCache
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.litePalBean.EarTagCache
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CameraActivity : BaseActivity() {
    var mLocationClient: LocationClient? = null

    //    private var cameraView: CustomCameraView? = null
    private var imv_label: TextView? = null
    private var imv_pic: ImageView? = null
    private var mFilePath: String? = null
    private val lblnumber = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private var imv_tips: TextView? = null
    private val cache_nums: MutableList<String?>? = ArrayList()
    private var epc: String? = null
    private var bitmap: Bitmap? = null
    private var btn_showcamera: ImageButton? = null
    private var id_nums: String? = null
    private var farm_name: String? = null
    private var mBtnAnimalChoice: TextView? = null
    private var cameraKitView: CameraKitView? = null

    //private ImageView mIvArrow;
    private val context: Context = this@CameraActivity
    private var image_total = 0
    private var animal_type: String? = null
    private var allEarTag: MutableList<AnimalSaveCache>? = ArrayList()
    private var mScanTotal: TextView? = null
    private var farmer_tel: String? = null

    private var cacheTag: List<EarTagCache>? = null
    private var mSacnEarTag: TextView? = null

    // private var mRlAnimalType: RelativeLayout? = null
    private var mTvFeiQi: TextView? = null

    //    private var category_name: String? = null
    private var farmer_area: String? = null
    private var farmer_zjCategory: String? = null
    private var farmer_address: String? = null
    private var mIvBack: ImageView? = null
    private var mRootTitle: TextView? = null

    // private var mRootDelete: TextView? = null
    private var et_rfid: EditText? = null
    private var rl_et: RelativeLayout? = null
    private var iv_dele: ImageView? = null
    private var ba_num: String? = null
    private var farmer_sign: String? = null
    private var category: String? = null
    private val bitmaps: ArrayList<String>? = ArrayList()
    private val animals: ArrayList<String>? = ArrayList()
    private var isSuccess_ = true
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                GET_EPC_C72 -> {
                    epc = msg.obj as String

                    if (!TextUtils.isEmpty(epc)) {
                        if (cache_nums != null && cache_nums!!.size > 0) {
                            if (cache_nums.contains(epc)) {

                                showStr("扫描到重复耳标")
                                playSound(R.raw.serror)
                                imv_tips!!.text = ""
                                isSuccess_ = true
                            } else {
                                totalSize += 1
                                cache_nums.add(epc)
                                playSound(R.raw.barcodebeep)
                                //                                    SoundUtil.playSound(1);
                                imv_tips!!.text = epc
                                mScanTotal!!.text = "扫描数量 $totalSize"
                                takePhoto()
                            }

                        } else {
                            cache_nums!!.add(epc)
                            playSound(R.raw.barcodebeep)
                            imv_tips!!.text = epc
                            mScanTotal!!.text = "扫描数量 " + "1"
                            takePhoto()
                        }
                    }
                }
            }
        }
    }

    //    private var content: FrameLayout? = null
    private var isClick = true
    private var farmer_address_int: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_new)
    }

    private var cropTypeLists: List<CropTypeList>? = null
    override fun onResume() {
        super.onResume()
        animals?.clear()

        if (NetUtil.checkNet(context)) {
            getInstance(this)!!.getCrop("养殖险") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
                if (datas != null && datas.size > 0) {
                    cropTypeLists = instant!!.parseCommonUseArr(datas, CropTypeList::class.java)
                    if (cropTypeLists != null && cropTypeLists!!.isNotEmpty()) {
                        for (item in cropTypeLists!!) {
                            if (item.fCropName != null && !TextUtils.isEmpty(item.fCropName)) {
                                val s = item.fCropName + "~" + item.fproductCode + "~" + item.fCropCode + "~" + item.fClauseCode
                                if (!animals!!.contains(s)) animals.add(s)
                            }
                        }
                    }
                }
                if (animals != null && animals.size > 0) {
                    mBtnAnimalChoice!!.text = animals[0]
                }
                getIntentData(animals)
            }
        } else {
            findAllAsync(AnimalCateGoryCache::class.java).listen { list: List<AnimalCateGoryCache> ->
                for (item in list) {
                    if (item.fCropName != null && !TextUtils.isEmpty(item.fCropName)) {
                        val s = item.fCropName + "~" + item.fproductCode + "~" + item.fCropCode + "~" + item.fClauseCode
                        if (!animals!!.contains(s)) animals.add(s)
                    }
                }
                getIntentData(animals)
            }
        }
        getIntentData(animals)
        cacheTag = findAll(EarTagCache::class.java)
        for (cache in cacheTag!!) {
            val lableNum = cache.earTag
            if (!TextUtils.isEmpty(lableNum) && !cache_nums!!.contains(lableNum)) cache_nums!!.add(lableNum)

        }
        cameraKitView!!.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    var totalSize = 0
    private fun getIntentData(animals: List<String>?) {
        if (animals == null || animals.isEmpty()) return

        val animal_Type = PreferUtils.getString(context, Constants.animal_type)

        mTvFeiQi!!.text = "重扫"
        mSacnEarTag!!.text = "按扫描键扫描电子耳标"
        if (intent != null) {

            farmer_address_int = intent!!.getStringExtra(Constants.farmer_address)
            farmer_area = intent!!.getStringExtra(Constants.farmer_area)
            category = intent!!.getStringExtra(Constants.category)
            ba_num = intent!!.getStringExtra(Constants.Ba_num)
            farmer_zjCategory = intent!!.getStringExtra(Constants.farmer_zjCategory)
            farmer_tel = intent!!.getStringExtra(Constants.farmer_tel)
            farm_name = intent!!.getStringExtra(Constants.farmer_name)
//        category_name = intent!!.getStringExtra(Constants.category_name)
            if (!farm_name.isNullOrBlank() && farm_name!!.length > 5) {
                var farmerName = farm_name!!.substring(0, 5) + "..."
                mRootTitle!!.text = "投保人:  $farmerName"
            } else {
                mRootTitle!!.text = "投保人:  $farm_name"
            }

            id_nums = intent!!.getStringExtra(Constants.farmer_id_nums)
            farmer_sign = intent!!.getStringExtra(Constants.farmer_sign)
            if (TextUtils.isEmpty(animal_Type)) {
                animal_type = intent!!.getStringExtra(Constants.animal_type)
                if (!TextUtils.isEmpty(animal_type)) {
                    mBtnAnimalChoice!!.text = animal_type
                } else {
                    showStr("请选择畜种")
                }
            } else {
                mBtnAnimalChoice!!.text = animal_Type
            }
            if (!ifC72()) {
                et_rfid!!.setText(intent!!.getStringExtra(Constants.ear_tag))
            }
        }
        if (allEarTag != null) allEarTag!!.clear()
        totalSize = 0
        allEarTag = getAllTag(id_nums, Utils.getText(mBtnAnimalChoice))
        totalSize = if (allEarTag == null || allEarTag!!.size == 0)
            0
        else
            allEarTag!!.size
        mScanTotal!!.text = "扫描数量 $totalSize"

    }

    private fun getAllTag(id_nums: String?, animal_type: String): MutableList<AnimalSaveCache>? {
        return if (TextUtils.isEmpty(id_nums) || TextUtils.isEmpty(animal_type)) {
            null
        } else where("FarmID =? and AnimalType = ? and isMakeDeal=? ", id_nums, animal_type, "0").find(AnimalSaveCache::class.java)
    }

    override fun onPause() {
        super.onPause()
        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()
        if (intent != null) intent = null
        onPauseaMedia()
        cameraKitView!!.onPause()
    }

    override fun onStart() {
        super.onStart()
        cameraKitView!!.onStart()
        LogUtils.e("onStart")
    }

    //    private var mCameraPreview: CameraView? = null
    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()
        cameraKitView!!.onStop()
    }

    override fun initView() {
        hideAllTitle()
        cameraKitView = findViewById(R.id.cc_camera)
//        content = findViewById(R.id.content)
        btn_showcamera = findViewById(R.id.btn_showcamera)
//        cameraView = findViewById(R.id.cc_camera)
        imv_label = findViewById<View>(R.id.imv_label) as TextView
        imv_pic = findViewById(R.id.imv_pic)
        imv_tips = findViewById<View>(R.id.imv_rfid) as TextView
        mBtnAnimalChoice = findViewById(R.id.btn_animal_choice)
        //mIvArrow = findViewById(R.id.iv_arrow);
        mScanTotal = findViewById(R.id.scan_total)
        mSacnEarTag = findViewById(R.id.sacn_earTag)
        //  mRlAnimalType = findViewById(R.id.rl_animal_type)
        mTvFeiQi = findViewById(R.id.feiqi)
        rl_et = findViewById(R.id.rl_et)
        iv_dele = findViewById(R.id.delete_epc)
        mIvBack = findViewById(R.id.iv_back1)
        mRootTitle = findViewById(R.id.title)
        // mRootDelete = findViewById(R.id.delete)
        et_rfid = findViewById(R.id.et_rfid)
        if (ifC72()) {
            rl_et!!.visibility = View.GONE
            imv_tips!!.visibility = View.VISIBLE
        } else {
            rl_et!!.visibility = View.VISIBLE
            imv_tips!!.visibility = View.GONE
        }
//        mCameraPreview = Camera2Preview(this)
//        content!!.addView(mCameraPreview as View?)
    }

    var era_tag = ""
    override fun initData() {
        image_total = PreferUtils.getInt(context, Constants.img_total)
        if (image_total == -1) image_total = 4

        if (NetUtil.checkNet(this)) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            mLocationClient!!.start()
            //声明LocationClient类
            mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                farmer_address = add
                latitude = lat
                longitude = log
                mLocationClient!!.stop()
            })
        }

        if (mLocationClient != null) mLocationClient!!.start()
    }


    override fun initListner() {
        imv_pic!!.setOnClickListener { v: View? ->
            if (bitmaps == null || bitmaps.size == 0) return@setOnClickListener
            if (path.isNullOrEmpty()) return@setOnClickListener
            val dialogImg = DialogImg(this, path)
            if (dialogImg != null && !isFinishing) dialogImg.show()
        }

        iv_dele!!.setOnClickListener { v: View? -> et_rfid!!.setText("") }
        mIvBack!!.setOnClickListener { v: View? -> onBackPressed() }
        btn_showcamera!!.setOnClickListener { view: View? ->
            if (ifC72()) {
                if (Utils.getText(mBtnAnimalChoice) != "畜种选择") {
                    var text = ""
                    text = if (ifC72()) {
                        Utils.getText(imv_tips)
                    } else {
                        Utils.getEdit(et_rfid)
                    }
                    if (!TextUtils.isEmpty(text) && !text.contains("已扫码") && !text.contains("正在扫描电子耳标...") && !text.contains("停止扫描,请重新按键")) {
                        if (isSuccess_) {
                            isSuccess_ = false
                            takePhoto()
                        }
                    } else {
                        showStr("请先扫描耳标号")
                    }
                } else {
                    showStr("请选择畜种")
                }
            } else {
                if (Utils.getText(mBtnAnimalChoice) == "畜种选择") {
                    showStr("请选择畜种")
                    return@setOnClickListener
                }
                val rfid = Utils.getText(et_rfid)
                if (TextUtils.isEmpty(rfid)) {
                    showStr("请输入耳号")
                    return@setOnClickListener
                }
                where("earTag =?", rfid).findAsync(EarTagCache::class.java).listen { list: List<EarTagCache?>? ->
                    if (list == null || list.isEmpty()) {
                        takePhoto()
                    } else {
                        showStr("重复耳号，请重新输入")
                    }
                }
            }
        }
        mBtnAnimalChoice!!.setOnClickListener { view: View? ->
            if (allEarTag != null) allEarTag!!.clear()
            if (animals != null && animals.size > 0) {
                isClick = !isClick
                PopuUtilsBig(this@CameraActivity).initChoicePop(mBtnAnimalChoice, animals) { str: String ->
                    PreferUtils.putString(context, Constants.animal_type, str)
                    mBtnAnimalChoice!!.text = str
                    allEarTag = getAllTag(id_nums, str)
                    totalSize = allEarTag!!.size
                    if (allEarTag == null || allEarTag!!.size == 0) mScanTotal!!.text = "扫描数量 0" else mScanTotal!!.text = "扫描数量 " + allEarTag!!.size
                }
                //                if (isClick) {
//                    mIvArrow.animate().setDuration(100).rotation(0).start();
//                } else {
//                    mIvArrow.animate().setDuration(100).rotation(90).start();
//                }
            } else {
                showStr("暂无畜种可选")
            }
        }
        mTvFeiQi!!.setOnClickListener { v: View? ->
            if (bitmaps!!.size == 1) {
                FileUtil.deleteImg(bitmaps[bitmaps.size - 1])
                imv_label!!.text = ""
                bitmaps.clear()

                imv_tips!!.text = ""
                et_rfid!!.setText("")
                imv_pic!!.visibility = View.GONE
            } else if (bitmaps.size >= 2) {
                imv_pic!!.visibility = View.VISIBLE
                FileUtil.deleteImg(bitmaps[bitmaps.size - 1])
                bitmaps.removeAt(bitmaps.size - 1)
                val s = bitmaps[bitmaps.size - 1]

                Glide.with(this).load(s).into(imv_pic!!)
                imv_label!!.text = "当前第 " + bitmaps.size + "/" + image_total + " 张"
            }
        }
    }

    private fun gotoSaveAnimal() {
        var era_tag: String? = ""
        era_tag = if (ifC72()) {
            Utils.getText(imv_tips)
        } else {
            Utils.getEdit(et_rfid)
        }
        val intent = Intent(context, ActivitySaveAnimal::class.java)
        intent.putExtra(Constants.epc, era_tag)
        intent.putExtra(Constants.farmer_name, farm_name)
        intent.putExtra(Constants.animal_type, Utils.getText(mBtnAnimalChoice))
        intent.putExtra(Constants.farmer_tel, farmer_tel)
        intent.putStringArrayListExtra(Constants.img_list, bitmaps)
        intent.putExtra(Constants.farmer_id_nums, id_nums)
        intent.putExtra(Constants.farmer_area, farmer_area)
        intent.putExtra(Constants.farmer_zjCategory, farmer_zjCategory)
        intent.putExtra(Constants.farmer_address, farmer_address_int)
        intent.putExtra(Constants.category, category)
        intent.putExtra(Constants.lat, latitude)
        intent.putExtra(Constants.lon, longitude)
        et_rfid!!.setText("")
        startActivity(intent)
        imv_tips!!.text = ""
        bitmaps!!.clear()
        imv_label!!.text = ""
        finish()
    }

    /**
     * 调用拍照功能
     */
    var path: String = ""
    private fun takePhoto() {

        playSound(R.raw.camera_click)
        cameraKitView!!.captureImage(object : CameraKitView.ImageCallback {
            override fun onImage(view: CameraKitView?, photo: ByteArray?) {
                era_tag = if (ifC72()) {
                    Utils.getText(imv_tips)
                } else {
                    Utils.getEdit(et_rfid)
                }

                val decodeByteArray = BitmapFactory.decodeByteArray(photo, 0, photo!!.size);
                val getimage = ImageUtils.getimageOnly(decodeByteArray)
                lifecycleScope.launch(Dispatchers.IO) {
                    var task = WateImagsTask()

                    var textList: MutableList<String> = ArrayList()
                    textList.clear()
                    //  textList.add("操作员:" + PreferUtils.getString(context, Constants.UserName))
                    textList.add("耳标号:$era_tag")
                    textList.add("被保险人:$farm_name")
                    textList.add("时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
                    textList.add("经度:$longitude 纬度:$latitude")
                    textList.add("📍" + farmer_address.toString())
                    val length = if (farmer_address.isNullOrEmpty()) {
                        0
                    } else {
                        farmer_address!!.length
                    }
                    var one_length = 14
                    if (length >= one_length) {
                        var first = farmer_address!!.substring(0, one_length)
                        var end = farmer_address!!.substring(one_length, length)
                        textList.add("📍:$first")
                        textList.add(end)

                        //  LogUtils.e("location_add  $location_add   $first  $end")
                    } else {
                        textList.add("📍:$farmer_address")
                    }

                    bitmap = task.addWater(context, textList, getimage)
                    path = ImageUtils.saveBitmap(this@CameraActivity, bitmap, FileUtil.getSDPath() + Constants.sdk_middle_animal + id_nums + "/", era_tag + "_" + bitmaps!!.size + ".jpg")

                    withContext(Dispatchers.Main) {
                        imv_pic!!.visibility = View.VISIBLE
                        Glide.with(this@CameraActivity).load(path).into(imv_pic!!)
                        if (!bitmaps.contains(path)) bitmaps.add(path!!)
                        imv_label!!.text = "当前第 " + bitmaps!!.size + "/" + image_total + " 张"
                        if (bitmaps!!.size >= 2) mTvFeiQi!!.text = "重拍"
                        //  updatePhotos(_file)
                        if (bitmaps.size == 1) mSacnEarTag!!.text = "按扫描键拍摄照片"
                        if (bitmaps.size == image_total) {
                            gotoSaveAnimal()
                            mSacnEarTag!!.text = "按扫描键扫描电子耳标"
                        }
                        isSuccess_ = true

                    }

                }
            }

        })

    }

    /**
     * 发送广播，图库更新照片
     *
     * @param file 新增的图片
     */
    private fun updatePhotos(file: File?) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(file)
        intent.data = uri
        sendBroadcast(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (Utils.getText(mBtnAnimalChoice) != "畜种选择" && Utils.getText(mBtnAnimalChoice) != "全部") {
                val text = Utils.getText(imv_tips)
                if (!TextUtils.isEmpty(text)) {
                    if (isSuccess_) {
                        isSuccess_ = false
                        takePhoto()
                    }
                } else {
                    if (mReader != null) {
                        if (isSuccess_) {
                            isSuccess_ = false
                            ReadTag(mReader, handler)
                            mSacnEarTag!!.text = "正在扫描电子耳标..."
                        }
                    }
                }
            } else {
                playSound(R.raw.serror)
                showStr("请选择畜种")
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}