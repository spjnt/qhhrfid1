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
        setRightText("选择农户")
        mAdapter = ImageAdapterNew(this)

        mRvPhotos!!.layoutManager = GridLayoutManager(this, 2)
        mRvPhotos!!.adapter = mAdapter
    }

    override fun initData() {
        val month = PreferUtils.getString(this, Constants.age_month)
        if (TextUtils.isEmpty(month)) mEtMonth!!.setText("12") else mEtMonth!!.setText(month)
        //  img_lists?.clear()

        if (intent != null) {
            farmer_area = intent.getStringExtra(Constants.farmer_area)
            category = intent.getStringExtra(Constants.category)
            farmer_zjCategory = intent.getStringExtra(Constants.farmer_zjCategory)
            farmer_tel = intent.getStringExtra(Constants.farmer_tel)
            farmer_name = intent.getStringExtra(Constants.farmer_name)
            if (!farmer_name.isNullOrBlank() && farmer_name!!.length > 5) {
                var farmerName = farmer_name!!.substring(0, 5) + "..."
                setTitleText("投保人:  $farmerName")
            } else {
                setTitleText("投保人:  $farmer_name")

            }
            animal_type = intent.getStringExtra(Constants.animal_type)
            val epc = intent.getStringExtra(Constants.epc)
            img_list = intent.getStringArrayListExtra(Constants.img_list)
            id_nums = intent.getStringExtra(Constants.farmer_id_nums)
            farmer_address = intent.getStringExtra(Constants.farmer_address)
            longitude = intent.getDoubleExtra(Constants.lon, 0.0)
            latitude = intent.getDoubleExtra(Constants.lat, 0.0)
            mAdapter!!.setGroupList(img_list)
            mEtEarTag!!.text = epc
            mAnimalCategory!!.text = animal_type
            //   if (!animal_type.isNullOrBlank() && !id_nums.isNullOrBlank()){
            val animalSaveCaches = where("FarmID =? and AnimalType=? and isMakeDeal=? ", id_nums, animal_type, "0").find(AnimalSaveCache::class.java)
            if (animalSaveCaches == null || animalSaveCaches.size == 0) mScanTotal!!.text = 1.toString() + "" else mScanTotal!!.text = (animalSaveCaches.size + 1).toString() + ""
            //  }


        }

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

    //    var isSave = false
    private fun saveAnimal(ifGotoFile: Boolean, ifOnlySave: Boolean, isGoMain: Boolean) {
        if (TextUtils.isEmpty(Utils.getText(mEtEarTag))) {
            showStr("耳标号为空")
            return
        }
        if (TextUtils.isEmpty(Utils.getText(mEtMonth))) {
            showStr("请输入月龄")
            return
        }

        where("earTag =?", Utils.getText(mEtEarTag)).findAsync(EarTagCache::class.java).listen { list: List<EarTagCache?>? ->
            if (list == null || list.isEmpty()) {
                val tagCache = EarTagCache()
                tagCache.earTag = Utils.getText(mEtEarTag)
                tagCache.save()
            }
        }
        PreferUtils.putString(this, Constants.age_month, Utils.getEdit(mEtMonth))
        where("LableNum =? and FarmID =?", Utils.getText(mEtEarTag), id_nums).findAsync(AnimalSaveCache::class.java).listen { list: List<AnimalSaveCache?>? ->
            val saveCache = AnimalSaveCache()
            saveCache.farmName = farmer_name
            saveCache.tel = farmer_tel
            saveCache.isUpLoad = "0"
            saveCache.statu = "新建"
            saveCache.ageMonth = Utils.getEdit(mEtMonth)
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
            if (list != null && list.isNotEmpty()) {
                //  saveCache.saveOrUpdate()
                val i = saveCache.updateAll("LableNum =? and FarmID =?", Utils.getText(mEtEarTag), id_nums)
                if (i > 0) {
                    showStr("保存成功")
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
                        intent.putExtra(Constants.MODULE_NAME, "承保验标")
                        startActivity(intent)
                    }
                } else {
                    showStr("保存成功")
                }
            } else {
                saveCache.lableNum = Utils.getText(mEtEarTag)
                saveCache.farmID = id_nums
                val save = saveCache.save()
                if (save) {
                    showStr("保存成功")
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
                        intent.putExtra(Constants.MODULE_NAME, "承保验标")
                        startActivity(intent)
                    }
                } else {
                    showStr("保存失败")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //  isSave = false
    }
    /* private fun goDeal() {
         val intent = Intent(context, ActivityUnderWriteDeal::class.java)
         intent.putExtra(Constants.farmer_zjCategory, farmer_zjCategory)
         intent.putExtra(Constants.farmer_name, farmer_name)
         intent.putExtra(Constants.animal_type, animal_type)
         intent.putExtra(Constants.farmer_id_nums, id_nums)
         intent.putExtra(Constants.farmer_area, farmer_area)
         intent.putExtra(Constants.category, category)
         intent.putExtra(Constants.farmer_address, farmer_address)
         intent.putExtra(Constants.Type, "from_camer")
         startActivity(intent)
     }*/

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            saveAnimal(false, true, false)
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_DOWN) {
            saveAnimal(false, true, true)

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}