package tramais.hnb.hhrfid.ui


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_camera_only.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.util.*

class CameraOnlyActivity
    : BaseActivity() {

    var mLocationClient: LocationClient? = null
    private var imv_pic: ImageView? = null
    private var mFilePath: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var bitmap: Bitmap? = null
    private var btn_showcamera: ImageButton? = null
    private val id_nums: String? = null
    private val context: Context = this@CameraOnlyActivity
    private var mImvGallery: TextView? = null
    private var mScanTotal: TextView? = null
    private var photo_num = 0
    private var cameraKitView: CameraKitView? = null
    private val bitmaps: ArrayList<String?>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_only)

    }


    override fun onStart() {
        super.onStart()

    }

    override fun onRestart() {
        super.onRestart()

    }

    override fun onResume() {
        super.onResume()

        cameraKitView!!.onStart()
        cameraKitView!!.onResume()

    }

    override fun onPause() {
        super.onPause()

        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()
        cameraKitView!!.onPause();
    }

    override fun onStop() {
        super.onStop()

        cameraKitView!!.onStop()
    }

    override fun onDestroy() {

        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
        bitmaps?.clear()


    }

    override fun initView() {
        cameraKitView = findViewById(R.id.camera)
        btn_showcamera = findViewById(R.id.btn_showcamera)
        imv_pic = findViewById(R.id.imageView)
        mImvGallery = findViewById(R.id.imv_gallery)
        mScanTotal = findViewById(R.id.scan_total)

        mScanTotal!!.bringToFront()


    }


    var location_add: String? = "无法定位"
    var famername: String? = null
    var earTag: String? = null
    var remark: String? = null
    var come_in_time: String = ""
    var textList: MutableList<String> = ArrayList()
    var sdk_path: String? = null
    var cdpath: String = ""
    var creatTime: String? = null
    var riskReason: String? = null
    var riskQty: String? = null
    var insure_type: String? = null
    override fun initData() {


        come_in_time = TimeUtil.getTime(Constants.MMddHHmmss)
        val fenpei = intent.getSerializableExtra("fenpei") as FenPei?

        photo_num = intent.getIntExtra("photo_num", 0)
        come_in_time = TimeUtil.getTime(Constants.MMddHHmmss)
        fenpei?.let { fenpei_ ->
            famername = if (fenpei_.farmerName.isNullOrBlank())
                "未知投保人"
            else fenpei_.farmerName

            earTag = if (fenpei_.EarTag.isNullOrBlank())
                "未知耳标号"
            else fenpei_.EarTag
            creatTime = fenpei_.createTime
            riskReason = fenpei_.riskReason
            setTitleText("投保人:$famername")
            remark = fenpei_.fRemark
            riskQty = fenpei_.riskQty
            insure_type = fenpei_.fCoinsFlag //险种
            location_add = fenpei_.riskAddress

            latitude = fenpei_.lat
            longitude = fenpei_.log
            if (remark != "only_photo") {
                if (NetUtil.checkNet(this)) {
                    mLocationClient = LocationClient(applicationContext)
                    BDLoactionUtil.initLoaction(mLocationClient)
                    if (mLocationClient != null) mLocationClient!!.start()

                    //声明LocationClient类
                    mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
                        LogUtils.e("add  $add $lat  $log")
                        if (add.isNullOrEmpty() || add.isNullOrBlank()) {
                            location_add = "无法定位"
                        } else {
                            location_add = add
                        }

                        latitude = lat
                        longitude = log
                        mLocationClient!!.stop()
                    })
                }
            }
        }

        textList.clear()

    }


    override fun initListner() {
        btn_showcamera!!.setOnClickListener { view: View? -> takePhoto() }
        mImvGallery!!.setOnClickListener { v: View? -> goBack() }
        if (imv_pic != null)
            imv_pic!!.setOnClickListener { v: View? ->
                if (bitmaps == null || bitmaps.size == 0) return@setOnClickListener
                if (path.isNullOrEmpty()) return@setOnClickListener
                val dialogImg = DialogImg(this, path)
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
            if (insure_type == "养殖险")
                textList.add("耳标号:$earTag")
            textList.add("标的名称:$riskQty")

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
        cdpath = "$sdk_path${TimeUtil.getTime(Constants.yyyy__MM__dd)}/"

        cameraKitView!!.captureImage(object : CameraKitView.ImageCallback {
            override fun onImage(view: CameraKitView?, photo: ByteArray?) {
                if (photo == null || photo.isEmpty()) return
                playSound(R.raw.camera_click)


                val decodeByteArray = BitmapFactory.decodeByteArray(photo, 0, photo!!.size)


                val getimage = ImageUtils.getimageOnly(decodeByteArray)

                lifecycleScope.launch(Dispatchers.IO) {
                    var task = WateImagsTask()
                    bitmap = task.addWater(context, textList, getimage)

                    var photo_name = come_in_time + "_" + bitmaps!!.size + ".jpg"
                    path = ImageUtils.saveBitmap(this@CameraOnlyActivity, bitmap, cdpath, photo_name)

                    withContext(Dispatchers.Main) {
                        imv_pic!!.visibility = View.VISIBLE
                        Glide.with(this@CameraOnlyActivity).load(path).into(imv_pic!!)
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

        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            takePhoto()
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    var path: String? = null


}

