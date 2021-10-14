package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.view.CustomCameraView
import tramais.hnb.hhrfid.util.*
import java.io.File
import java.util.*


class CameraOnlyActivity2 : BaseActivity() {
    //    private var parentView: FrameLayout? = null
    var mLocationClient: LocationClient? = null
    private var mCameraPreview: CustomCameraView? = null
    private var imv_pic: ImageView? = null
    private var mFilePath: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var bitmap: Bitmap? = null
    private var btn_showcamera: ImageButton? = null
    private val id_nums: String? = null
    private val context: Context = this@CameraOnlyActivity2
    private var mImvGallery: TextView? = null
    private var mScanTotal: TextView? = null
    private var photo_num = 0
    private val bitmaps: ArrayList<String?>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_only2)
//        val outSize = Point()
//        windowManager.defaultDisplay.getRealSize(outSize)
//        val x: Int = outSize.x
//        val y: Int = outSize.y
//        LogUtils.e("x = $x,y = $y")
        //x = 1440,y = 2960
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

    override fun initView() {
        btn_showcamera = findViewById(R.id.btn_showcamera)
//        parentView = findViewById<FrameLayout>(R.id.camera_preview)
        imv_pic = findViewById(R.id.imv_pic)
        mImvGallery = findViewById(R.id.imv_gallery)
        mScanTotal = findViewById(R.id.scan_total)
        mCameraPreview = findViewById(R.id.cc_camera)
    }


    var location_add: String? = "无法定位"
    var famername: String? = null
    var earTag: String? = null
    var remark: String? = null
    var come_in_time: String = ""
    var textList: MutableList<String> = ArrayList()
    var sdk_path: String? = null
    override fun initData() {
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
        //  val intent = getIntent()
        val fenpei = intent.getSerializableExtra("fenpei") as FenPei?

        photo_num = intent.getIntExtra("photo_num", 0)
        come_in_time = TimeUtil.getTime(Constants.MMddHHmmss)
        fenpei?.let { fenpei_ ->
            famername = if (fenpei_.farmerName.isNullOrBlank())
                "未知投保人"
            else fenpei_.farmerName
            /*  var farmerNumber=if(fenpei_.farmerNumber.isNullOrBlank())
                  "未知证件号"
              else fenpei_.farmerNumber*/
            earTag = if (fenpei_.EarTag.isNullOrBlank())
                "未知耳标号"
            else fenpei_.EarTag
            creatTime = fenpei_.createTime
            riskReason = fenpei_.riskReason
            setTitleText("投保人:$famername")
            remark = fenpei_.fRemark
            riskQty = fenpei_.riskQty
            setTitleText("被保险人:$famername")
            remark = fenpei_.fRemark

        }

    }

    override fun initListner() {
        btn_showcamera!!.setOnClickListener { view: View? -> takePhoto() }
        mImvGallery!!.setOnClickListener { v: View? -> goBack() }
        if (imv_pic != null) {
            imv_pic!!.setOnClickListener { v: View? ->
                if (bitmaps == null || bitmaps.size == 0) return@setOnClickListener
                val dialogImg = DialogImg(this, bitmaps[bitmaps.size - 1])
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
    private fun takePhoto() {

        textList.clear()
        /*水印顺序：被保险人，标的名称，耳标号，出险原因，出险时间，查勘时间,经纬度，查勘地点，*/
        if (remark == "only_photo") {
            val userName = PreferUtils.getString(context, Constants.UserName)
            var name = if (userName.isNullOrBlank()) {
                "未知"
            } else {
                userName
            }
            sdk_path = FileUtil.getSDPath() + Constants.sdk_camer
            //  textList.add("操作员:$name")
            textList.add("被保险人:$famername")
//            if (insure_type == "养殖险")
            textList.add("耳标号:$earTag")
//            textList.add("标的名称:$riskQty")

            textList.add("出险原因:$riskReason")
            textList.add("出险时间:$creatTime")
            textList.add("查勘时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            textList.add("经度:$longitude 纬度:$latitude")
            val length = if (location_add.isNullOrEmpty()) {
                0
            } else {
                location_add!!.length
            }
            var one_length = 14
            if (length >= one_length) {
                var first = location_add!!.substring(0, one_length)
                var end = location_add!!.substring(one_length, length)
                textList.add("📍:$first")
                textList.add(end)

                //  LogUtils.e("location_add  $location_add   $first  $end")
            } else {
                textList.add("📍:$location_add")
            }

        } else {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_middle_animal
            //  textList.add("操作员:" + PreferUtils.getString(context, Constants.UserName))
            textList.add("被保险人:$famername")
            textList.add("时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            textList.add("经度:$longitude 纬度:$latitude")
            val length = if (location_add.isNullOrEmpty()) {
                0
            } else {
                location_add!!.length
            }

            var one_length = 14
            if (length >= one_length) {
                var first = location_add!!.substring(0, one_length)
                var end = location_add!!.substring(one_length, length)
                textList.add("📍:$first")
                textList.add(end)
            } else {
                textList.add("📍:$location_add")
            }
        }

        playSound(R.raw.camera_click)
        mCameraPreview!!.takePicture()
//        mCameraPreview.PHOTO_FILE_NAME = FileUtil.getSDPath() + sdk_first_path;
//        mCameraPreview.cameramode = "newfarmer";
        mCameraPreview!!.setOnTakePictureInfo(object : CustomCameraView.OnTakePictureInfo {
            override fun onTakePictureInofo(_success: Boolean, _file: File?) {
                LogUtils.e("_success  $_success")
                if (!_success) {
                    return
                }
                _file?.let {
                    val absolutePath = it.absolutePath
                    LogUtils.e("absolutePath $absolutePath")
                    if (absolutePath != null) {
                        val getimage = ImageUtils.getimage(absolutePath)
                        if (getimage != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                var task = WateImagsTask()

                                bitmap = task.addWater(context, textList, getimage)
                                var cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"
                                var photo_name = System.currentTimeMillis().toString() + ".jpg"
                                path = ImageUtils.saveBitmap(this@CameraOnlyActivity2, bitmap, cdpath, photo_name)
                                withContext(Dispatchers.Main) {
                                    imv_pic!!.visibility = View.VISIBLE
                                    Glide.with(this@CameraOnlyActivity2).load(path).into(imv_pic!!)
                                    //imv_pic!!.setImageBitmap(bitmap)
                                    if (!bitmaps!!.contains(path)) bitmaps.add(path)
                                    if (remark == "only_photo") {
                                        mScanTotal!!.text = "当前第 " + (bitmaps.size) + " 张"
                                    } else {
                                        mScanTotal!!.text = "当前第 " + (bitmaps.size + photo_num) + "/9" + " 张"
                                        if (bitmaps.size + photo_num == 9) goBack()
                                    }

                                }

                            }
                        }

                    }


                }
            }

        })

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


}


