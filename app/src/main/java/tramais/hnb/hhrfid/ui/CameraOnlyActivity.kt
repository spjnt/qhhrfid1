package tramais.hnb.hhrfid.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.forjrking.lubankt.Luban
import kotlinx.android.synthetic.main.activity_camera_only.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.view.CustomCameraView
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.waterimage.WaterMaskUtil
import tramais.hnb.hhrfid.waterimage.WaterMaskView
import java.io.File
import kotlin.math.max


class CameraOnlyActivity : BaseActivity() {
    private var waterMaskView: WaterMaskView? = null
    var mLocationClient: LocationClient? = null
    private var mCameraPreview: CustomCameraView? = null
    private var imv_pic: ImageView? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var btn_showcamera: ImageButton? = null
    private var mImvGallery: TextView? = null
    private var mScanTotal: TextView? = null
    private var photo_num = 0
    private val bitmaps: ArrayList<String?>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_only)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onPause() {
        super.onPause()
        if (mLocationClient != null) mLocationClient!!.stop()


    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()
    }

    var isOpen: Boolean = false
    override fun initView() {
        btn_showcamera = findViewById(R.id.btn_showcamera)
        imv_pic = findViewById(R.id.imv_pic)
        mImvGallery = findViewById(R.id.imv_gallery)
        mScanTotal = findViewById(R.id.scan_total)
        mCameraPreview = findViewById(R.id.cc_camera)

        waterMaskView = WaterMaskView(this)
        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )
        waterMaskView!!.layoutParams = params

        iv_flash.setOnClickListener {
            val hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
            if (!hasFlash) {
                showStr("???????????????????????????")
                return@setOnClickListener
            }
            isOpen = !isOpen
            iv_flash.isSelected = isOpen
            mCameraPreview!!.openCloseFlash(isOpen)

        }
        iv_floder.setOnClickListener {
            if (cdpath.isEmpty()) {
                return@setOnClickListener
            }
            val file = File(cdpath)
            if (!file.exists()) {
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(Uri.fromFile(file), "file/*")
            startActivity(intent)
        }
    }

    private fun saveWaterMask(waterMaskView: WaterMaskView?, sourBitmap: Bitmap, path_: String, name_: String): String {
        try {
            val waterBitmap = WaterMaskUtil.loadBitmapFromView(waterMaskView)
            val watermarkBitmap = WaterMaskUtil.createWaterMaskLeftBottom(this, sourBitmap, waterBitmap, 0, 0)
            return ImageUtils.saveBitmap(this, watermarkBitmap, path_, name_,90)
        } catch (e: Exception) {
            LogUtils.e("e  ${e.message}")
        }
        return ""
    }

    var location_add: String? = "????????????"
    var famername: String? = null
    var earTag: String? = null
    var remark: String? = null
    var textList: MutableList<String> = ArrayList()
    var sdk_path: String? = null

    override fun initData() {

        val fenpei_ = intent.getSerializableExtra("fenpei")
        photo_num = intent.getIntExtra("photo_num", 0)
        if (fenpei_ != null) {
            fenpei_ as FenPei?
            famername = if (fenpei_.farmerName.isNullOrBlank())
                "???????????????"
            else fenpei_.farmerName
            earTag = if (fenpei_.EarTag.isNullOrBlank())
                "???????????????"
            else fenpei_.EarTag
            creatTime = fenpei_.createTime
            riskReason = fenpei_.riskReason
            remark = fenpei_.fRemark
            riskQty = fenpei_.riskQty
            setTitleText("????????????:$famername")
            remark = fenpei_.fRemark
            insure_type = fenpei_.fCoinsFlag //??????
            location_add = fenpei_.riskAddress
            latitude = fenpei_.lat
            longitude = fenpei_.log
        }

        if (remark != "only_photo") {
            if (NetUtil.checkNet(this)) {
                mLocationClient = LocationClient(applicationContext)
                BDLoactionUtil.initLoaction(mLocationClient)
                if (mLocationClient != null) mLocationClient!!.start()
                //??????LocationClient???
                mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                    //   LogUtils.e("add  $add $lat  $log")
                    location_add = add
                    latitude = lat
                    longitude = log
                    mLocationClient!!.stop()
                })
            }
        }
    }

    override fun initListner() {
        btn_showcamera!!.setOnClickListener { takePhoto() }
        mImvGallery!!.setOnClickListener {
            if (remark == "only_photo") {
                finish()
            } else {
                goBack()
            }
        }
        if (imv_pic != null) {
            imv_pic!!.setOnClickListener { v: View? ->
                if (path.isEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, path)
                if (!isFinishing) dialogImg.show()
            }
        }

    }

    private fun goBack() {
        //???????????????Intent??????
        val intent = Intent()
        //?????????????????????Intent
        intent.putExtra("imgs", bitmaps)
        //??????????????????
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * ??????????????????
     */
    var creatTime: String? = null
    var riskReason: String? = null
    var riskQty: String? = null
    var insure_type: String? = null
    var path: String = ""
    var crators: MutableList<String> = ArrayList()
    var waterInfos: MutableList<String> = ArrayList()
    var cdpath = ""
    private fun takePhoto() {
        crators.clear()
        waterInfos.clear()
        textList.clear()
        val userName = PreferUtils.getString(this, Constants.UserName)
        val name = if (userName.isNullOrBlank()) {
            "??????"
        } else {
            userName
        }
        for (item in 1..10) {
            crators.add(name)
        }

        /*???????????????????????????????????????????????????????????????????????????????????????????????????,???????????????????????????*/
        if (remark == "only_photo") {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_camer
            waterInfos.add("????????????:$famername")
            if (insure_type == "?????????")
                waterInfos.add("?????????:$earTag")
            waterInfos.add("????????????:$riskQty")
            waterInfos.add("????????????:$riskReason")
            waterInfos.add("????????????:$creatTime")
            waterInfos.add("????????????:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            waterInfos.add("??????:$longitude ??????:$latitude")
        } else {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_middle_animal
            waterInfos.add("????????????:$famername")
            waterInfos.add("??????:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            waterInfos.add("??????:$longitude ??????:$latitude")
        }
        playSound(R.raw.camera_click)
        mCameraPreview!!.takePicture()
        mCameraPreview!!.setOnTakePictureInfo(object : CustomCameraView.OnTakePictureInfo {
            override fun onTakePictureInofo(_success: Boolean, _file: ByteArray?) {
                if (!_success) {
                    return
                }
                _file?.let {
                    cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"
                    val photo_name = famername + "_" + System.currentTimeMillis().toString() + ".jpg"
                    val decodeByteArray = decodeBitmap(it/*, it.size*/) ?: return
                    LuBan(decodeByteArray, cdpath, photo_name, crators, waterInfos)
                }
            }
        })
    }

    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        try {
            val max = max(outHeight, outWidth)
            if (max > 1024) {
                val scaleFactorX = outWidth / 1024 + 1
                val scaleFactorY = outHeight / 1024 + 1
                inSampleSize = max(scaleFactorX, scaleFactorY)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun decodeBitmap(buffer: ByteArray): Bitmap? {
        if (buffer.isEmpty()) return null
        val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size/*, bitmapOptions*/)
        val matrix = Matrix()
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) return null
        if (width > height) {
            matrix.postRotate(90f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            takePhoto()
        }
        return super.onKeyDown(keyCode, event)
    }

    protected override fun onResume() {
        super.onResume()
    }

    fun LuBan(bitMap: Bitmap?, path_: String, photo_name: String, crators: MutableList<String>, waterInfos: MutableList<String>) {
        if (bitMap == null) return
        try {
            Luban.with(this)               //(??????)Lifecycle,???????????????????????????ProcessLifecycleOwner
                    .load(bitMap)                       //?????? File,Uri,InputStream,String,Bitmap ??????????????????????????????
                    //.setOutPutDir(path_)              //(??????)?????????????????????
                    .concurrent(true)                //(??????)??????????????????????????????,????????????????????????????????????OOM
                    .useDownSample(true)             //(??????)???????????? true??????????????????,???????????????????????????(???????????????????????????)
                    .format(Bitmap.CompressFormat.JPEG)//(??????)??????????????????????????? ?????? JPG,PNG,WEBP
                    .ignoreBy(1024 * 2)                   //(??????)????????????,???????????????????????????????????????????????????????????????????????????,
                    .quality(90)                     //(??????)??????????????????  0-100
                    // .rename { name_ }             //(??????)???????????????
                    .filter { it != null }             //(??????)?????????
                    .compressObserver {
                        onSuccess = {
                            // LogUtils.e("it.absolutePath   ${it.absolutePath}")
                            val Bitmapbm = BitmapFactory.decodeFile(it.absolutePath)
                            if (Bitmapbm != null) {
                                val int = PreferUtils.getInt(this@CameraOnlyActivity, Constants.color_int)
                                val intColor = if (int == -1) {
                                    resources.getColor(R.color.new_theme)
                                } else {
                                    resources.getColor(int)
                                }
                                waterMaskView!!.setBackData(crators, Bitmapbm.height.toFloat(), Bitmapbm.width.toFloat())
                                waterMaskView!!.setLeftData(waterInfos, Bitmapbm.width.toFloat(), intColor)
                                waterMaskView!!.setLocation(location_add, Bitmapbm.width, intColor)

                                path = saveWaterMask(waterMaskView, Bitmapbm, path_, photo_name)
                                if (path.isNotEmpty() && File(path).exists()) {
                                    if (it.exists())
                                        it.delete()
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.Main) {
                                            bitmaps!!.add(path)
                                            // LogUtils.e("path  $path")
                                            scan_total.bringToFront()
                                            scan_total.text = "????????? ${bitmaps.size} ???"
                                            imv_pic!!.visibility = View.VISIBLE
                                            Glide.with(this@CameraOnlyActivity).load(path).into(imv_pic!!)
                                        }
                                    }
                                }
                            }
                        }
                        onStart = { }
                        onCompletion = { }
                        onError = { e, _ -> }
                    }.launch()

        } catch (e: Exception) {
            LogUtils.e("Exception  ${e.message}  ")
        }

    }

}


