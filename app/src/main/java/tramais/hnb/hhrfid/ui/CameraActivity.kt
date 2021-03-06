package tramais.hnb.hhrfid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.forjrking.lubankt.Luban
import org.litepal.LitePal.findAll
import org.litepal.LitePal.findAllAsync
import org.litepal.LitePal.where
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.CropTypeList
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.litePalBean.AnimalCateGoryCache
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.litePalBean.EarTagCache
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.view.CustomCameraView
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.waterimage.WaterMaskUtil
import tramais.hnb.hhrfid.waterimage.WaterMaskView
import java.util.*
import kotlin.math.max

class CameraActivity : BaseActivity(), TextToSpeech.OnInitListener {
    var mLocationClient: LocationClient? = null

    private var cameraView: CustomCameraView? = null
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

    //private ImageView mIvArrow;
    private val context: Context = this@CameraActivity

    //    private var image_total = 0
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
    private var mTextToSpeech: TextToSpeech? = null
    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                GET_EPC_C72 -> {
                    epc = msg.obj as String

                    if (!TextUtils.isEmpty(epc) && epc != "????????????,???????????????") {
                        if (cache_nums != null && cache_nums!!.size > 0) {
                            if (cache_nums.contains(epc)) {

                                showStr("???????????? $epc")
                                submit("???????????? $epc")
                                playSound(R.raw.serror)
                                imv_tips!!.text = ""
                                isSuccess_ = true
                            } else {
                                totalSize += 1
                                submit("$epc")
                                cache_nums.add(epc)
                                playSound(R.raw.barcodebeep)
                                //                                    SoundUtil.playSound(1);
                                imv_tips!!.text = epc
                                mScanTotal!!.text = "???????????? $totalSize"
                                takePhoto()
                            }

                        } else {
                            submit("$epc")
                            cache_nums!!.add(epc)
                            playSound(R.raw.barcodebeep)
                            imv_tips!!.text = epc
                            mScanTotal!!.text = "???????????? " + "1"
                            takePhoto()
                        }
                    }
                }
            }
        }
    }

    private fun initTextToSpeech() {
        // ??????Context,TextToSpeech.OnInitListener
        mTextToSpeech = TextToSpeech(this, this)
        // ???????????????????????????????????????????????????????????????????????????,1.0?????????
        mTextToSpeech!!.setPitch(1.0f)
        // ????????????
        mTextToSpeech!!.setSpeechRate(0.5f)
    }

    //    private var content: FrameLayout? = null
    private var isClick = true
    private var farmer_address_int: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    private var waterMaskView: WaterMaskView? = null
    private var cropTypeLists: List<CropTypeList>? = null
    override fun onResume() {
        super.onResume()
        animals?.clear()
        initTextToSpeech()
        if (NetUtil.checkNet(context)) {
            getInstance(this)!!.getCrop("?????????") { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
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
//                if (animals != null && animals.size > 0) {
//                    mBtnAnimalChoice!!.text = animals[0]
//                }
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

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    var totalSize = 0
    private fun getIntentData(animals: List<String>?) {
        if (animals == null || animals.isEmpty()) return

        val animal_Type = PreferUtils.getString(context, Constants.animal_type)

        mTvFeiQi!!.text = "??????"
        mSacnEarTag!!.text = "??????????????????????????????"
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
                mRootTitle!!.text = "?????????:  $farmerName"
            } else {
                mRootTitle!!.text = "?????????:  $farm_name"
            }

            id_nums = intent!!.getStringExtra(Constants.farmer_id_nums)
            farmer_sign = intent!!.getStringExtra(Constants.farmer_sign)
            if (TextUtils.isEmpty(animal_Type)) {
                animal_type = intent!!.getStringExtra(Constants.animal_type)
                if (!TextUtils.isEmpty(animal_type)) {
                    mBtnAnimalChoice!!.text = animal_type
                } else {
                    showStr("???????????????")
                }
            } else {
                mBtnAnimalChoice!!.text = animal_Type
            }
            if (!ifC72() || ifHC720s()) {
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
        mScanTotal!!.text = "???????????? $totalSize"

    }

    private fun getAllTag(id_nums: String?, animal_type: String): MutableList<AnimalSaveCache>? {
        return if (TextUtils.isEmpty(id_nums) || TextUtils.isEmpty(animal_type)) {
            null
        } else where("FarmID =? and AnimalType = ? and isMakeDeal=? ", id_nums, animal_type, "0").find(AnimalSaveCache::class.java)
    }

    override fun onPause() {
        super.onPause()
        if (mLocationClient != null) mLocationClient!!.stop()
        // bitmaps?.clear()
        if (intent != null) intent = null
        onPauseaMedia()
        isSuccess_ = true
    }

    override fun onStart() {
        super.onStart()
    }

    //    private var mCameraPreview: CameraView? = null
    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()
        if (mTextToSpeech != null) {
            mTextToSpeech!!.stop()
            mTextToSpeech!!.shutdown()
            mTextToSpeech = null
        }
    }

    private fun submit(someVoice: String) {
        if (someVoice.isEmpty()) {
            return
        }
        if (mTextToSpeech != null && !mTextToSpeech!!.isSpeaking) {
            /*
                TextToSpeech???speak????????????????????????
                // ?????????????????????
                speak(CharSequence text,int queueMode,Bundle params,String utteranceId);
                // ??????????????????????????????????????????
                synthesizeToFile(CharSequence text,Bundle params,File file,String utteranceId);
                ???????????????queueMode???????????????????????????????????????????????????
                ???1???TextToSpeech.QUEUE_FLUSH??????????????????????????????????????????????????????????????????????????????????????????
                ???2???TextToSpeech.QUEUE_ADD??????????????????????????????????????????????????????????????????
                ??????????????????????????????????????????????????????????????????
             */
            mTextToSpeech!!.speak(someVoice, TextToSpeech.QUEUE_FLUSH, null, "1")
        }
    }

    override fun initView() {
        hideAllTitle()

        waterMaskView = WaterMaskView(this)
        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )
        waterMaskView!!.layoutParams = params
        cameraView = findViewById(R.id.cc_camera)
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
        if (ifC72() || ifHC720s()) {
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
//        image_total = PreferUtils.getInt(context, Constants.img_total)
//        if (image_total == -1) image_total = 4

        if (NetUtil.checkNet(this)) {
            mLocationClient = LocationClient(applicationContext)
            BDLoactionUtil.initLoaction(mLocationClient)
            mLocationClient!!.start()
            //??????LocationClient???
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
            if (path.isEmpty()) return@setOnClickListener
            val dialogImg = DialogImg(this, path)
            if (!isFinishing) dialogImg.show()
        }

        iv_dele!!.setOnClickListener { v: View? -> et_rfid!!.setText("") }
        mIvBack!!.setOnClickListener { v: View? -> onBackPressed() }
        btn_showcamera!!.setOnClickListener { view: View? ->
            if (ifC72() || ifHC720s()) {
                if (Utils.getText(mBtnAnimalChoice) != "????????????") {
                    var text = ""
                    text = if (ifC72() || ifHC720s()) {
                        Utils.getText(imv_tips)
                    } else {
                        Utils.getEdit(et_rfid)
                    }
                    if (!TextUtils.isEmpty(text) && !text.contains("?????????") && !text.contains("??????????????????????????????") && !text.contains("????????????,???????????????")) {
                        if (isSuccess_) {
                            isSuccess_ = false
                            takePhoto()
                        }
                    } else {
                        showStr("?????????????????????")
                    }
                } else {
                    showStr("???????????????")
                }
            } else {
                if (Utils.getText(mBtnAnimalChoice) == "????????????") {
                    showStr("???????????????")
                    return@setOnClickListener
                }
                val rfid = Utils.getText(et_rfid)
                if (TextUtils.isEmpty(rfid)) {
                    showStr("???????????????")
                    return@setOnClickListener
                }
                where("earTag =?", rfid).findAsync(EarTagCache::class.java).listen { list: List<EarTagCache?>? ->
                    if (list == null || list.isEmpty()) {
                        takePhoto()
                    } else {
                        showStr("??????????????????????????????")
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
                    if (allEarTag == null || allEarTag!!.size == 0) mScanTotal!!.text = "???????????? 0" else mScanTotal!!.text = "???????????? " + allEarTag!!.size
                }
                //                if (isClick) {
//                    mIvArrow.animate().setDuration(100).rotation(0).start();
//                } else {
//                    mIvArrow.animate().setDuration(100).rotation(90).start();
//                }
            } else {
                showStr("??????????????????")
            }
        }
        mTvFeiQi!!.setOnClickListener { v: View? ->
            if (bitmaps!!.size == 1) {
                FileUtil.deleteImg(bitmaps[bitmaps.size - 1])
                totalSize -= 1
                // imv_label!!.text = ""
                bitmaps.clear()
                val text = Utils.getText(imv_tips)
                if (text.isNotEmpty() && cache_nums!!.contains(text))
                    cache_nums.remove(text)
                mScanTotal!!.text = ""
                imv_tips!!.text = ""
                et_rfid!!.setText("")

//                imv_pic!!.visibility = View.GONE
            } else if (bitmaps.size >= 2) {
//                imv_pic!!.visibility = View.VISIBLE
//                mScanTotal!!.visibility = View.VISIBLE
                FileUtil.deleteImg(bitmaps[bitmaps.size - 1])
                bitmaps.removeAt(bitmaps.size - 1)
                val s = bitmaps[bitmaps.size - 1]
                Glide.with(this).load(s).into(imv_pic!!)
                mScanTotal!!.text = "????????? ${bitmaps.size} ???"
                //  imv_label!!.text = "????????? " + bitmaps.size + "???"
            }
        }
    }

    private fun gotoSaveAnimal() {

        val era_tag = if (ifC72() || ifHC720s()) {
            Utils.getText(imv_tips)
        } else {
            Utils.getEdit(et_rfid)
        }
        //  LogUtils.e("eat camera  $era_tag")
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
     * ??????????????????
     */
    var crators: MutableList<String> = java.util.ArrayList()
    var waterInfos: MutableList<String> = java.util.ArrayList()
    var path: String = ""
    private fun takePhoto() {
        crators.clear()
        waterInfos.clear()

        val userName = PreferUtils.getString(this, Constants.UserName)
        val name = if (userName.isNullOrBlank()) {
            "??????"
        } else {
            userName
        }
        for (item in 1..10) {
            crators.add(name)
        }

        var era_tag: String? = ""
        era_tag = if (ifC72() || ifHC720s()) {
            Utils.getText(imv_tips)
        } else {

            Utils.getEdit(et_rfid)
        }
        if (!ifC72() && !ifHC720s()) {
            if (bitmaps.isNullOrEmpty()) submit(era_tag!!)
        }
        waterInfos.add("?????????:$era_tag")
        waterInfos.add("????????????:$farm_name")
        waterInfos.add("??????:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
        waterInfos.add("??????:$longitude ??????:$latitude")

        playSound(R.raw.camera_click)
        cameraView!!.takePicture()
        cameraView!!.setOnTakePictureInfo(object : CustomCameraView.OnTakePictureInfo {
            override fun onTakePictureInofo(_success: Boolean, _file: ByteArray?) {
                if (!_success) {
                    return
                }
                val cdpath = FileUtil.getSDPath() + Constants.sdk_middle_animal + id_nums + "/"
                _file?.let {
                    val photo_name = era_tag + "_" + bitmaps!!.size + ".jpg"
                    // cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"
                    //  Thread {
                    val decodeByteArray = decodeBitmap(it)
                    if (decodeByteArray != null)
                        LuBan(decodeByteArray, cdpath, photo_name, crators, waterInfos)
                    // }.start()

                }
            }

        })
    }

    fun LuBan(bitMap: Bitmap, path_: String, photo_name: String, crators: MutableList<String>, waterInfos: MutableList<String>) {
        try {
            Luban.with(this)               //(??????)Lifecycle,???????????????????????????ProcessLifecycleOwner
                    .load(bitMap)                       //?????? File,Uri,InputStream,String,Bitmap ??????????????????????????????
                    //.setOutPutDir(path_)              //(??????)?????????????????????
                    .concurrent(true)                //(??????)??????????????????????????????,????????????????????????????????????OOM
                    .useDownSample(true)             //(??????)???????????? true??????????????????,???????????????????????????(???????????????????????????)
                    .format(Bitmap.CompressFormat.JPEG)//(??????)??????????????????????????? ?????? JPG,PNG,WEBP
                    .ignoreBy(250)                   //(??????)????????????,???????????????????????????????????????????????????????????????????????????,
                    .quality(Config.img_quality_small)                     //(??????)??????????????????  0-100
                    // .rename { name_ }             //(??????)???????????????
                    .filter { it != null }             //(??????)?????????
                    .compressObserver {
                        onSuccess = {
                            if (it != null) {
                                val Bitmapbm = BitmapFactory.decodeFile(it.absolutePath)
                                if (Bitmapbm != null && waterMaskView != null) {
                                    waterMaskView!!.setBackData(crators, Bitmapbm.height.toFloat(), Bitmapbm.width.toFloat())
                                    val int = PreferUtils.getInt(this@CameraActivity, Constants.color_int)
                                    val intColor = if (int == -1) {
                                        resources.getColor(R.color.new_theme)
                                    } else {
                                        resources.getColor(int)
                                    }
                                    waterMaskView!!.setLeftData(waterInfos, Bitmapbm.height.toFloat(), intColor)
                                    waterMaskView!!.setLocation(farmer_address, Bitmapbm.width, intColor)

                                    path = saveWaterMask(waterMaskView, Bitmapbm, path_, photo_name)
                                    if (it.exists())
                                        it.delete()
                                    runOnUiThread {
                                        bitmaps!!.add(path)
                                        mScanTotal!!.bringToFront()
                                        mScanTotal!!.text = "????????? ${bitmaps.size} ???"
                                        imv_pic!!.visibility = View.VISIBLE
                                        Glide.with(context).load(path).into(imv_pic!!)

                                        if (bitmaps!!.size >= 2) mTvFeiQi!!.text = "??????"
                                        //  updatePhotos(_file)
                                        if (bitmaps.size == 1) mSacnEarTag!!.text = "????????????????????????"
                                        if (bitmaps.size == 4) {
                                            gotoSaveAnimal()
                                            mSacnEarTag!!.text = "??????????????????????????????"
                                        }
                                        isSuccess_ = true
                                    }
                                }
                            }

                        }
                        onStart = {

                        }
                        onCompletion = { }
                        onError = { e, _ -> }
                    }.launch()

        } catch (e: Exception) {
            LogUtils.e("Exception  ${e.message}  ")
        }

    }

    private fun saveWaterMask(waterMaskView: WaterMaskView?, sourBitmap: Bitmap, path_: String, name_: String): String {
        try {
            val waterBitmap = WaterMaskUtil.loadBitmapFromView(waterMaskView)
            val watermarkBitmap = WaterMaskUtil.createWaterMaskLeftBottom(this, sourBitmap, waterBitmap, 0, 0)
            return ImageUtils.saveBitmap(this, watermarkBitmap, path_, name_, Config.img_quality_small)
        } catch (e: Exception) {
        }
        return ""
    }

    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        if (max(outHeight, outWidth) > 1024) {
            val scaleFactorX = outWidth / 1024 + 1
            val scaleFactorY = outHeight / 1024 + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    private fun decodeBitmap(buffer: ByteArray): Bitmap? {
        if (buffer.isEmpty()) return null
        val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size/*, bitmapOptions*/)
        val matrix = Matrix()
        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90f)
        }
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //  if (ifC72()) {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (Utils.getText(mBtnAnimalChoice) != "????????????" && Utils.getText(mBtnAnimalChoice) != "??????") {
                val text = Utils.getText(imv_tips)
                if (!TextUtils.isEmpty(text) /*&& !text.contains("?????????") && !text.contains("??????????????????????????????") && !text.contains("????????????,???????????????")*/) {
                    if (isSuccess_) {
                        isSuccess_ = false
                        takePhoto()
                    }
                } else {
                    //  if (mReader != null) {
                    if (isSuccess_) {
                        isSuccess_ = false
                        if (ifC72() && mReader != null) {
                            ReadTag(/*mReader, */handler)
                        }
                        if (ifHC720s()) {
                            startScan(handler)
                        }
                        mSacnEarTag!!.text = "??????????????????????????????"
                    }
                    // }
                }
            } else {
                playSound(R.raw.serror)
                showStr("???????????????")
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            /*
                ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            ????????????????????????????????????????????????????????????????????????????????????????????????????????????TTS????????????
            ???????????????????????????Pico TTs???????????????????????????3.0?????????????????????3.0?????????Pico TTS?????????
            ??????????????????????????????????????????????????????????????????????????????3.0??????????????????

                ??????????????????????????????????????????????????????????????????
            ???????????????????????????Module??????????????????????????????????????????3.0.apk??????????????????????????????????????????
            ??????????????????????????????????????????TTS??????????????????????????????????????????????????????3.0???????????????????????????
            Demo???????????????????????????????????????
             */
            // setLanguage????????????
            val result = mTextToSpeech!!.setLanguage(Locale.CHINA)
            // TextToSpeech.LANG_MISSING_DATA??????????????????????????????
            // TextToSpeech.LANG_NOT_SUPPORTED????????????
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                showStr("????????????????????????")
            } else if (result == TextToSpeech.LANG_AVAILABLE) {
                LogUtils.e("result  ${TextToSpeech.LANG_AVAILABLE}")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // ????????????????????????TTS????????????
        mTextToSpeech!!.stop()
        // ?????????????????????
        mTextToSpeech!!.shutdown()
    }


}