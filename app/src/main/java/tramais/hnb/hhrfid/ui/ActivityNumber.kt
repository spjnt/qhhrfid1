package tramais.hnb.hhrfid.ui

import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.apkfuns.logutils.LogUtils
import com.baidu.location.LocationClient
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_weight_length.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.ShuShu
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.ChoicePhoto
import tramais.hnb.hhrfid.interfaces.GetShuShu
import tramais.hnb.hhrfid.listener.MyLocationListener
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.util.*
import java.io.File

class ActivityNumber : BaseActivity(), ChoicePhoto {
    var mLocationClient: LocationClient? = null
    private var mPhoto: ImageView? = null
    private var mNumber: EditText? = null
    private var mAddres: EditText? = null
    private var mFarmer: TextView? = null
    private var mCamer: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number)
    }


    override fun onPause() {
        super.onPause()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationClient != null) mLocationClient!!.stop()
    }

    var zjNmber: String? = null
    override fun initView() {
        setTitleText("智能点数")
        mNumber = findViewById(R.id.et_number)
        mPhoto = findViewById(R.id.iv_phpto)
        mAddres = findViewById(R.id.et_address)
        mCamer = findViewById(R.id.camer)
        mFarmer = findViewById(R.id.et_farmer)
        mFarmer!!.text = intent.getStringExtra(Constants.farmer_name) ?: ""
        zjNmber = intent.getStringExtra(Constants.farmer_id_nums)
    }

    var lat_: Double = 0.0
    var lon_: Double = 0.0
    var address_: String = ""
    override fun initData() {
        mLocationClient = LocationClient(applicationContext)
        BDLoactionUtil.initLoaction(mLocationClient)
        if (mLocationClient != null) mLocationClient!!.start()
        //声明LocationClient类
        mLocationClient!!.registerLocationListener(MyLocationListener { lat: Double, log: Double, add: String? ->
            if (!TextUtils.isEmpty(add)) {

                mLocationClient!!.stop()
                lat_ = lat
                lon_ = log
                address_ = add.toString()
                mAddres!!.setText(add)
            }
        })
    }

    override fun initListner() {
        mCamer!!.setOnClickListener {
            if (!NetUtil.checkNet(this)){
                netTips()
                return@setOnClickListener
            }
            PopuChoicePicture(this@ActivityNumber, this@ActivityNumber).showAtLocation(mCamer, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)

        }
    }

    override fun actionCamera() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .compressEngine(ImageCompressEngine.createCompressEngine())
                .isCompress(true)
                .cutOutQuality(30)// 裁剪压缩质量 默认90 int
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .selectionMode(PictureConfig.SINGLE)

                .compressSavePath(FileUtil.getSDPath() + Constants.sdk_camer)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) return
                        val realPath = result[0].realPath
                        // FileUtil.deleteImg(realPath)
                        val compressPath = result[0].compressPath
                        LogUtils.e("compress $compressPath")
                        Glide.with(this@ActivityNumber).load(compressPath).into(mPhoto!!)
                        Thread {
                            Looper.prepare()
                            upLoadImage(compressPath)
                            Looper.loop()

                        }.start()

                    }

                    override fun onCancel() {
                    }
                })
    }

    override fun actionAlbum() {
        LogUtils.e("${TimeUtil.getTime(Constants.yyyy__MM__dd)}    ${TimeUtil.getTime(Constants.yyyyMMddHHmmss)}")
        var path = FileUtil.getSDPath() + Constants.sdk_camer + TimeUtil.getTime(Constants.yyyy__MM__dd)
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .isCompress(true)
                .cutOutQuality(30)// 裁剪压缩质量 默认90 int
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .selectionMode(PictureConfig.SINGLE)
                .maxSelectNum(1)
                .compressSavePath(path)
                //TimeUtil.getTime(Constants.MMddHHmmss)
                .renameCompressFile("" + ".jpeg")
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: List<LocalMedia>) {
                        for (item in result) {
                            LogUtils.e("compress ${item.compressPath}")
                        }
                        /* if (result.isNullOrEmpty()) return
                         val realPath = result[0].realPath
                         //  FileUtil.deleteImg(realPath)
                         val compressPath = result[0].compressPath
                         LogUtils.e("compress $compressPath")
                         Glide.with(this@ActivityNumber).load(compressPath).into(mPhoto!!)
                         Thread {
                             // Looper.prepare()
                             upLoadImage(compressPath)
                             //  Looper.loop()

                         }.start()*/

                    }

                    override fun onCancel() {}
                })
    }


    fun upLoadImage(path: String?) {
        if (path.isNullOrEmpty()) return
        var paths: HashMap<String, String?> = HashMap()
        paths.clear()
        paths["点数"] = path.toString()
        //  LogUtils.e("Come in ")
        //showAvi()
        UpLoadFileUtil.upLoadFile(this, "", paths) { str ->

            RequestUtil.getInstance(this)!!.ai(zjNmber, str[0], address_, lat_, lon_, object : GetShuShu {
                override fun getShu(shu: ShuShu?) {

                    //  hideAvi()
                    shu?.let {

                        runOnUiThread {
                            Glide.with(this@ActivityNumber).load(it.picturename).into(mPhoto!!)
                            mNumber!!.setText(it.count.toString())
                        }
                    }

                }
            })

        }
    }

}