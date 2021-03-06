package tramais.hnb.hhrfid.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import org.litepal.LitePal.where
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.litePalBean.EarTagCache
import tramais.hnb.hhrfid.lv.ImageAdapterNew
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils

class ActivitySaveAnimal : BaseActivity() {
    private var mEtEarTag: TextView? = null
    private var mScanTotal: TextView? = null
    private var mAnimalCategory: TextView? = null
    private var mEtMonth: EditText? = null
    private var mRvPhotos: RecyclerView? = null
    private var mScanNext: Button? = null
    private var mGoToUpLoad: Button? = null
    private var mScanDrop: Button? = null
    private val context: Context = this@ActivitySaveAnimal
    private var id_nums: String? = null
    private var longitude = 0.0
    private var latitude = 0.0
    private var animal_type: String? = null
    private var farmer_name: String? = null
    private var img_list: MutableList<String>? = ArrayList()
    private var farmer_tel: String? = null
    private var farmer_zjCategory: String? = null
    private var farmer_area: String? = null
    private var category: String? = null
    private var farmer_address: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_animal_info)
    }

    var mAdapter: ImageAdapterNew? = null
    override fun initView() {
        //  showRootSetting();
        mEtEarTag = findViewById(R.id.et_earTag)
        mScanTotal = findViewById(R.id.scan_total)
        mAnimalCategory = findViewById(R.id.animal_category)
        mEtMonth = findViewById(R.id.et_month)
        mRvPhotos = findViewById(R.id.rv_photos)
        mScanNext = findViewById(R.id.scan_next)
        mGoToUpLoad = findViewById(R.id.goToUpLoad)
        mScanDrop = findViewById(R.id.scan_drop)
        setRightText("????????????")
        mAdapter = ImageAdapterNew(this)

        mRvPhotos!!.layoutManager = GridLayoutManager(this, 2)
        mRvPhotos!!.adapter = mAdapter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun initData() {
        val month = PreferUtils.getString(this, Constants.age_month)
        if (TextUtils.isEmpty(month)) mEtMonth!!.setText("12") else mEtMonth!!.setText(month)
        //  img_lists?.clear()


    }


    override fun initListner() {
        mCustomTitle!!.mRootDelete.setOnClickListener {
            saveAnimal(false, false, false)
        }
//        mMakeDeal!!.setOnClickListener { v: View? -> saveAnimal(true, true) }
        mScanNext!!.setOnClickListener { view: View? -> saveAnimal(false, true, false) }
        mScanDrop!!.setOnClickListener { v: View? -> goToCamera() }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                it.getItem(position)
                DialogImg(context, it.getItem(position)).show()
            }
        }
        mGoToUpLoad!!.setOnClickListener {
            saveAnimal(true, true, false)

        }
        /*if (imageAdapter != null)
            imageAdapter!!.setItemClickListener(object :ImageAdapter.OnItemClickListener{
                override fun onItemClick(position: Int) {
                    DialogImg(context, img_list!!.get(position)).show()
                }

            })*/
    }

    override fun onPause() {
        super.onPause()
        if (intent != null) intent = null
    }

    //    var isSave = false
    private fun saveAnimal(ifGotoFile: Boolean, ifOnlySave: Boolean, isGoMain: Boolean) {
        val earTag = Utils.getText(mEtEarTag)
        if (TextUtils.isEmpty(earTag)) {
            showStr("???????????????")
            return
        }
        val month = Utils.getText(mEtMonth)
        if (TextUtils.isEmpty(month)) {
            showStr("???????????????")
            return
        }

        where("earTag =?", earTag).findAsync(EarTagCache::class.java).listen { list: List<EarTagCache?>? ->
            if (list!!.isNullOrEmpty()) {
                val tagCache = EarTagCache()
                tagCache.earTag = earTag
                val save = tagCache.save()
                //   LogUtils.e("tag  $save")
            }
        }
        PreferUtils.putString(this, Constants.age_month, month)
        //   LogUtils.e("list  ${id_nums}   $earTag")
        where("lableNum =? and farmID =?", earTag, id_nums).findAsync(AnimalSaveCache::class.java).listen { list: List<AnimalSaveCache?>? ->
            val saveCache = AnimalSaveCache()
            saveCache.farmName = farmer_name
            saveCache.tel = farmer_tel
            saveCache.isUpLoad = "0"
            saveCache.statu = "??????"
            saveCache.ageMonth = month
            saveCache.latitude = latitude
            saveCache.longitude = longitude
            saveCache.animalType = animal_type
            saveCache.isMakeDeal = "0"
            saveCache.category_name = farmer_area
            saveCache.comPanyNumber = companyNum
            saveCache.employeeNumber = userNum
            saveCache.creatTime = TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss)
            if (img_list != null && img_list!!.size > 0) {
                if (img_list!!.size == 1) {
                    saveCache.img1 = img_list!![0]
                }
                if (img_list!!.size == 2) {
                    saveCache.img1 = img_list!![0]
                    saveCache.img2 = img_list!![1]
                }
                if (img_list!!.size == 3) {
                    saveCache.img1 = img_list!![0]
                    saveCache.img2 = img_list!![1]
                    saveCache.img3 = img_list!![2]
                }
                if (img_list!!.size >= 4) {
                    saveCache.img1 = img_list!![0]
                    saveCache.img2 = img_list!![1]
                    saveCache.img3 = img_list!![2]
                    saveCache.img4 = img_list!![3]
                }
            } else {
                saveCache.img1 = ""
                saveCache.img2 = ""
                saveCache.img3 = ""
                saveCache.img4 = ""
                saveCache.img5 = ""
            }
            if (list!!.isNotEmpty()) {
                val i = saveCache.updateAll("LableNum =? and FarmID =?", earTag, id_nums)
                if (i > 0) {
                    showStr("????????????")
                    if (ifOnlySave) {
                        when {
                            ifGotoFile -> {
                                finish()
                                Utils.goToNextUI(ActivityFile::class.java)
                            }
                            isGoMain -> {
                                Utils.goToNextUI(MainActivity::class.java)
                            }
                            else -> {
                                goToCamera()
                            }
                        }
                    } else {
                        val intent = Intent(context, ActivityFarmList::class.java)
                        intent.putExtra(Constants.MODULE_NAME, "????????????")
                        startActivity(intent)
                    }
                } else {
                    showStr("????????????")
                }


            } else {
                saveCache.lableNum = earTag
                saveCache.farmID = id_nums
                val save = saveCache.save()
                if (save) {
                    showStr("????????????")
                    if (ifOnlySave) {
                        when {
                            ifGotoFile -> {
                                Utils.goToNextUI(ActivityFile::class.java)
                            }
                            isGoMain -> {
                                Utils.goToNextUI(MainActivity::class.java)
                            }
                            else -> {
                                goToCamera()
                            }
                        }
                    } else {
                        val intent = Intent(context, ActivityFarmList::class.java)
                        intent.putExtra(Constants.MODULE_NAME, "????????????")
                        startActivity(intent)
                    }
                } else {
                    showStr("????????????")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isKeyDown = false
        if (intent != null) {
            farmer_area = intent.getStringExtra(Constants.farmer_area)
            category = intent.getStringExtra(Constants.category)
            farmer_zjCategory = intent.getStringExtra(Constants.farmer_zjCategory)
            farmer_tel = intent.getStringExtra(Constants.farmer_tel)
            farmer_name = intent.getStringExtra(Constants.farmer_name)
            if (!farmer_name.isNullOrBlank() && farmer_name!!.length > 5) {
                val farmerName = farmer_name!!.substring(0, 5) + "..."
                setTitleText("?????????:  $farmerName")
            } else {
                setTitleText("?????????:  $farmer_name")
            }
            animal_type = intent.getStringExtra(Constants.animal_type)
            val epc = intent.getStringExtra(Constants.epc)
            //   LogUtils.e("epc   $epc")
            img_list = intent.getStringArrayListExtra(Constants.img_list)
            id_nums = intent.getStringExtra(Constants.farmer_id_nums)
            farmer_address = intent.getStringExtra(Constants.farmer_address)
            longitude = intent.getDoubleExtra(Constants.lon, 0.0)
            latitude = intent.getDoubleExtra(Constants.lat, 0.0)
            mAdapter!!.setGroupList(img_list)
            mEtEarTag!!.text = epc
            mAnimalCategory!!.text = animal_type
            val animalSaveCaches = where("FarmID =? and AnimalType=? ", id_nums, animal_type).find(AnimalSaveCache::class.java)
            if (animalSaveCaches.isNullOrEmpty()) mScanTotal!!.text = 1.toString() + "" else mScanTotal!!.text = (animalSaveCaches.size + 1).toString() + ""
        }
    }

    private fun goToCamera() {
        val intent = Intent(context, CameraActivity::class.java)
        intent.putExtra(Constants.farmer_zjCategory, farmer_zjCategory)
        intent.putExtra(Constants.farmer_name, farmer_name)
        intent.putExtra(Constants.animal_type, animal_type)
        intent.putExtra(Constants.farmer_id_nums, id_nums)
        intent.putExtra(Constants.farmer_tel, farmer_tel)
        intent.putExtra(Constants.farmer_area, farmer_area)
        intent.putExtra(Constants.category, category)
        intent.putExtra(Constants.farmer_address, farmer_address)
        intent.putExtra(Constants.ear_tag, Utils.getText(mEtEarTag))
        startActivity(intent)
        //  finish()
    }

    var isKeyDown = false
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (!isKeyDown) {
                saveAnimal(ifGotoFile = false, ifOnlySave = true, isGoMain = false)
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (!isKeyDown)
                saveAnimal(ifGotoFile = false, ifOnlySave = true, isGoMain = true)
            return false
        }
        isKeyDown = true
        return super.onKeyDown(keyCode, event)
    }
}