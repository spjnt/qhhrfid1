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
import android.widget.*
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.forjrking.lubankt.Luban
import kotlinx.android.synthetic.main.activity_camera_only2.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.scan_total
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.camerutils.decodeExifOrientation
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.view.CustomCameraView
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.waterimage.WaterMaskUtil
import tramais.hnb.hhrfid.waterimage.WaterMaskView
import java.io.File
import java.util.*
import kotlin.math.max


class CameraOnlyActivity2 : BaseActivity() {
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
        setContentView(R.layout.activity_camera_only2)
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
                showStr("当前设备没有闪光灯")
                return@setOnClickListener
            }
            isOpen = !isOpen
            iv_flash.isSelected = isOpen
            mCameraPreview!!.openCloseFlash(isOpen)

        }
        iv_floder.setOnClickListener {
            if (cdpath.isNullOrEmpty()) {
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
            var waterBitmap = WaterMaskUtil.loadBitmapFromView(waterMaskView)
            var watermarkBitmap = WaterMaskUtil.createWaterMaskLeftBottom(this, sourBitmap, waterBitmap, 0, 0)
            return ImageUtils.saveBitmap(this, watermarkBitmap, path_, name_)
        } catch (e: Exception) {
        }
        return ""

    }

    var location_add: String? = "无法定位"
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
                "未知投保人"
            else fenpei_.farmerName
            earTag = if (fenpei_.EarTag.isNullOrBlank())
                "未知耳标号"
            else fenpei_.EarTag
            creatTime = fenpei_.createTime
            riskReason = fenpei_.riskReason
            remark = fenpei_.fRemark
            riskQty = fenpei_.riskQty
            setTitleText("被保险人:$famername")
            remark = fenpei_.fRemark
            insure_type = fenpei_.fCoinsFlag //险种
            location_add = fenpei_.riskAddress
            latitude = fenpei_.lat
            longitude = fenpei_.log
        }
        if (remark != "only_photo") {
            if (NetUtil.checkNet(this)) {
                mLocationClient = LocationClient(applicationContext)
                BDLoactionUtil.initLoaction(mLocationClient)
                if (mLocationClient != null) mLocationClient!!.start()
                //声明LocationClient类
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
        btn_showcamera!!.setOnClickListener { view: View? -> takePhoto() }
        mImvGallery!!.setOnClickListener { v: View? -> goBack() }
        if (imv_pic != null) {
            imv_pic!!.setOnClickListener { v: View? ->
                if (path.isNullOrEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, path)
                if (dialogImg != null && !isFinishing) dialogImg.show()
            }
        }

    }

    private fun goBack() {
        //数据是使用Intent返回
        val intent = Intent()
        //把返回数据存入Intent
        intent.putExtra("imgs", bitmaps)
        //设置返回数据
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * 调用拍照功能
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
        var name = if (userName.isNullOrBlank()) {
            "未知"
        } else {
            userName
        }
        for (item in 1..10) {
            crators.add(name)
        }
        /*水印顺序：被保险人，标的名称，耳标号，出险原因，出险时间，查勘时间,经纬度，查勘地点，*/
        if (remark == "only_photo") {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_camer
            waterInfos.add("被保险人:$famername")
            if (insure_type == "养殖险")
                waterInfos.add("耳标号:$earTag")
            waterInfos.add("标的名称:$riskQty")
            waterInfos.add("出险原因:$riskReason")
            waterInfos.add("出险时间:$creatTime")
            waterInfos.add("查勘时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            waterInfos.add("经度:$longitude 纬度:$latitude")
        } else {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_middle_animal
            waterInfos.add("被保险人:$famername")
            waterInfos.add("时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            waterInfos.add("经度:$longitude 纬度:$latitude")
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
                    val photo_name = System.currentTimeMillis().toString() + ".jpg"

                    val decodeByteArray = decodeBitmap(it, 0, it.size)
/*//                    val rotateBitmap = ImageUtils.rotateBitmap(90, decodeByteArray)
                    val saveBitmap = ImageUtils.saveBitmap(this@CameraOnlyActivity2, decodeByteArray, cdpath, photo_name)
                    val readPictureDegree = ImageUtils.readPictureDegree(saveBitmap)
                    LogUtils.e("readPictureDegree  $readPictureDegree")
                    val pro_img = BitmapFactory.decodeFile(saveBitmap)
                    val Bitmapbm = if (readPictureDegree != 0) {
                        ImageUtils.rotaingImageView(readPictureDegree, pro_img)
                    } else {
                        pro_img
                    }*/
                    if (decodeByteArray != null)
                        LuBan(decodeByteArray, cdpath, photo_name, crators, waterInfos)
                }
            }

        })

    }

    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        if (max(outHeight, outWidth) > 1024) {
            val scaleFactorX = outWidth / 1024 + 1
            val scaleFactorY = outHeight / 1024 + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)
        var matrix = Matrix()
        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90f)
        }
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(ExifInterface.ORIENTATION_ROTATE_90) }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            takePhoto()
        }
        return super.onKeyDown(keyCode, event)
    }


    protected override fun onResume() {
        super.onResume()

    }

    fun LuBan(bitMap: Bitmap, path_: String, photo_name: String, crators: MutableList<String>, waterInfos: MutableList<String>) {
        try {
            Luban.with(this)               //(可选)Lifecycle,可以不填写内部使用ProcessLifecycleOwner
                    .load(bitMap)                       //支持 File,Uri,InputStream,String,Bitmap 和以上数据数组和集合
                    //.setOutPutDir(path_)              //(可选)输出目录文件夹
                    .concurrent(true)                //(可选)多文件压缩时是否并行,内部优化线程并行数量防止OOM
                    .useDownSample(true)             //(可选)压缩算法 true采用邻近采样,否则使用双线性采样(纯文字图片效果绝佳)
                    .format(Bitmap.CompressFormat.JPEG)//(可选)压缩后输出文件格式 支持 JPG,PNG,WEBP
                    .ignoreBy(1024)                   //(可选)期望大小,大小和图片呈现质量不能均衡所以压缩后不一定小于此值,
                    .quality(90)                     //(可选)质量压缩系数  0-100
                    // .rename { name_ }             //(可选)文件重命名
                    .filter { it != null }             //(可选)过滤器
                    .compressObserver {
                        onSuccess = {
                            if (it != null) {
                                val Bitmapbm = BitmapFactory.decodeFile(it.absolutePath)
                                if (Bitmapbm != null) {
                                    waterMaskView!!.setBackData(crators, Bitmapbm.height.toFloat(), Bitmapbm.width.toFloat())
                                    waterMaskView!!.setLeftData(waterInfos, Bitmapbm.height.toFloat())
                                    waterMaskView!!.setLocation(location_add!!)

                                    path = saveWaterMask(waterMaskView, Bitmapbm, path_, photo_name)
                                    if (it.exists())
                                        it.delete()
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.Main) {
                                            bitmaps!!.add(path)
                                            scan_total.bringToFront()
                                            scan_total.text = "当前第 ${bitmaps.size} 张"
                                            imv_pic!!.visibility = View.VISIBLE
                                            Glide.with(this@CameraOnlyActivity2).load(path).into(imv_pic!!)
                                        }
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

}


