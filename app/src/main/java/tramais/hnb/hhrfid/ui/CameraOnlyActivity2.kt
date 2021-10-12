package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.compress.OnCompressListener
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.view.Camera2Preview
import tramais.hnb.hhrfid.ui.view.CameraView
import tramais.hnb.hhrfid.util.*
import java.util.*


class CameraOnlyActivity2 : BaseActivity() {
    private var parentView: FrameLayout? = null
    var mLocationClient: LocationClient? = null
    private var mCameraPreview: CameraView? = null
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
        bitmaps?.clear()
        if (mCameraPreview != null) mCameraPreview!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()
    }

    override fun initView() {
        btn_showcamera = findViewById(R.id.btn_showcamera)
        parentView = findViewById<FrameLayout>(R.id.camera_preview)
        imv_pic = findViewById(R.id.imv_pic)
        mImvGallery = findViewById(R.id.imv_gallery)
        mScanTotal = findViewById(R.id.scan_total)
        mCameraPreview = Camera2Preview(this)
        parentView!!.addView(mCameraPreview as View?)
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

            setTitleText("投保人:$famername")
            remark = fenpei_.fRemark

        }

        textList.clear()
        if (remark == "only_photo") {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_camer
            textList.add("操作员:" + PreferUtils.getString(context, Constants.UserName))
            textList.add("被保险人:$famername")
            textList.add("耳标号:${earTag}")
            textList.add("拍摄时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            textList.add("经度:$longitude 纬度:$latitude")
            textList.add("📍:$location_add")
        } else {
            sdk_path = FileUtil.getSDPath() + Constants.sdk_middle_animal
            textList.add("操作员:" + PreferUtils.getString(context, Constants.UserName))
            textList.add("被保险人:$famername")
            textList.add("时间:" + TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss))
            textList.add("经度:$longitude 纬度:$latitude")
            textList.add("📍:$location_add")
        }
    }

    override fun initListner() {
        btn_showcamera!!.setOnClickListener { view: View? -> takePhoto() }
        mImvGallery!!.setOnClickListener { v: View? -> goBack() }

        imv_pic!!.setOnClickListener { v: View? ->
            if (bitmaps == null || bitmaps.size == 0) return@setOnClickListener
            val dialogImg = DialogImg(this, bitmaps[bitmaps.size - 1])
            if (dialogImg != null && !isFinishing) dialogImg.show()
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
    private fun takePhoto() {
        val start = System.currentTimeMillis()
        // mCameraPreview!!.takePicture()
        playSound(R.raw.camera_click)
        mCameraPreview!!.takePicture(object : CameraView.TakePictureCallback {
            override fun success(picturePath: String?) {
                var path: String? = null
                val middle = System.currentTimeMillis()
                LogUtils.e("picturePath   $picturePath")
                //  com(picturePath!!, FileUtils.getSDPath() + Constants.sdk_first_path)
                lifecycleScope.launch(Dispatchers.IO) {
                    var task = WateImagsTask()
                    val file_map = BitmapFactory.decodeFile(picturePath)
                    bitmap = task.addWater(context, textList, file_map)
                    var cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"
                    var photo_name = come_in_time + "_" + bitmaps!!.size + ".jpg"
                    path = ImageUtils.saveBitmap(this@CameraOnlyActivity2, bitmap, cdpath, photo_name)
                    val end = System.currentTimeMillis()
                    LogUtils.e("middle  ${end - middle}")
                    LogUtils.e("end  ${end - start}")
                    LogUtils.e("start  ${middle - start}")
                    withContext(Dispatchers.Main) {
                        imv_pic!!.visibility = View.VISIBLE
                        Glide.with(this@CameraOnlyActivity2).load(path).into(imv_pic!!)
                        //imv_pic!!.setImageBitmap(bitmap)
                        if (!bitmaps.contains(path)) bitmaps.add(path)
                        if (remark == "only_photo") {
                            mScanTotal!!.text = "当前第 " + (bitmaps.size) + " 张"
                        } else {
                            mScanTotal!!.text = "当前第 " + (bitmaps.size + photo_num) + "/9" + " 张"
                            if (bitmaps.size + photo_num == 9) goBack()
                        }

                    }

                }

            }

            override fun error(error: String?) {
                /*  Toast.makeText(this@CameraOnlyActivity2, "错误信息：$error", Toast.LENGTH_SHORT)
                          .show()*/
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            takePhoto()
        }
        return super.onKeyDown(keyCode, event)
    }

    fun com(path: String, target: String) {
        Luban.with(this)
                .load(path!!)
                .ignoreBy(1024)
                // .setTargetDir(getPath())

                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {
                        LogUtils.e("onstart")
                    }

                    override fun onSuccess(list: MutableList<LocalMedia>?) {
                        LogUtils.e("onSuccess")
                    }


                    override fun onError(e: Throwable) {
                        LogUtils.e("e  ${e.message}")
                    }
                }).launch()


    }

    protected override fun onResume() {
        super.onResume()
        if (mCameraPreview != null) mCameraPreview!!.onResume()
    }


}


