package tramais.hnb.hhrfid.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.gyf.immersionbar.ktx.immersionBar
import com.mylhyl.acp.Acp
import com.mylhyl.acp.AcpListener
import com.mylhyl.acp.AcpOptions
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.Roles
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetBool
import tramais.hnb.hhrfid.litePalBean.*
import tramais.hnb.hhrfid.ui.dialog.DialogUpLoad
import tramais.hnb.hhrfid.ui.table.TabConst
import tramais.hnb.hhrfid.ui.table.TabItem
import tramais.hnb.hhrfid.ui.table.TabLayout
import tramais.hnb.hhrfid.ui.table.TabSelectListener
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.Utils
import java.util.*

class MainActivity : BaseActivity() {
    private var layout: FrameLayout? = null

    private var tabLayout: TabLayout? = null
    var mainTabs = ArrayList<TabItem>()
    var fragments = ArrayList<Fragment?>()
    private var homeFragment: HomeFragment? = null
    private var animalInsureFragment: AnimalInsureFragment? = null
    private var cropInsureFragment: CropInsureFragment? = null
    private var settingFragment: SettingFragment? = null
    private var currentIndex = 0
    var start: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        immersionBar()
        start = System.currentTimeMillis()
        permission
        if (NetUtil.checkNet(this))
            deal()
        //  NetworkChangeReceiver.registerReceiver(this)
    }

    private fun setCurrentTab(index: Int) {
        TabConst.MAIN_TAB_INDEX = index
        tabLayout?.let { it.currentTab = index }
    }

    private fun addFragment(fragment: Fragment?) {
        if (!fragment!!.isAdded) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.main_content, fragment, fragment.javaClass.simpleName)
                    .commit()
            fragments.add(fragment)
        }
    }


    private fun showFragment(fragment: Fragment?) {

        for (frag in fragments) {
            if (frag != null && frag != fragment) {
                supportFragmentManager.beginTransaction().hide(frag).commit()
            }
        }
        supportFragmentManager.beginTransaction().show(fragment!!).commit()
    }

    private fun selectTab(index: Int) {

        TabConst.MAIN_TAB_INDEX = index
        // LogUtils.e("index  $index")
        if (NetUtil.checkNet(this)) {
            when (index) {
                0 ->
                    showFragment(homeFragment)
                1 ->
                    showFragment(animalInsureFragment)
                2 ->
                    showFragment(cropInsureFragment)
                3 ->
                    showFragment(settingFragment)
            }
        } else {
            when (index) {
                0 ->
                    showFragment(animalInsureFragment)
                1 ->
                    showFragment(cropInsureFragment)
                2 ->
                    showFragment(settingFragment)
            }
        }
        currentIndex = index


    }

    private fun initTab(checkNet: Boolean, currentIndex: Int) {

        mainTabs.clear()
        if (checkNet) mainTabs.add(TabItem("首页", R.mipmap.icon_home_gray, R.mipmap.icon_home_red))
        mainTabs.add(TabItem("养殖险", R.mipmap.yazhi_gray, R.mipmap.icon_yangzhi_redl))
        mainTabs.add(TabItem("种植险", R.mipmap.zhognzhi_gray, R.mipmap.zhongzhi_red))
        mainTabs.add(TabItem("设置", R.mipmap.setting_gray, R.mipmap.setting_red))
        tabLayout!!.setTabData(mainTabs)
        tabLayout!!.setOnTabSelectListener(object : TabSelectListener {
            override fun onTabSelect(position: Int): Boolean {
                selectTab(position)
                return true
            }

            override fun onTabReselect(position: Int): Boolean {
                return true
            }
        })
        // if (!isResume) {
        homeFragment = HomeFragment.newInstance()
        animalInsureFragment = AnimalInsureFragment.newInstance()
        cropInsureFragment = CropInsureFragment.newInstance()
        settingFragment = SettingFragment.newInstance()

        if (checkNet)
            addFragment(homeFragment)
        addFragment(animalInsureFragment)
        addFragment(cropInsureFragment)
        addFragment(settingFragment)


        setCurrentTab(currentIndex)

    }

    override fun initView() {
        layout = findViewById(R.id.main_content)
        tabLayout = findViewById(R.id.main_tab_layout)
    }

    companion object {
        var roles: MutableList<Roles> = ArrayList()
    }

    override fun initData() {
        /**直接跳转至位置信息设置界面*/

        if (!isLocServiceEnable(this)) {
            showStr("请打开位置服务")
            var intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, 134)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val enabled: Boolean = isLocationEnabled(this@MainActivity)
        if (!enabled) {
            showStr("未开启位置服务,将不能定位")
        }
    }

    fun isLocServiceEnable(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }

    override fun initListner() {}
    private val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE)
    private val permission: Unit
        private get() {
            Acp.getInstance(this).request(AcpOptions.Builder()
                    .setPermissions(*permissions)
                    .build(),
                    object : AcpListener {
                        override fun onGranted() {}
                        override fun onDenied(permissions: List<String>) {
                            showStr( "相机相册，内存读写，定位权限被拒绝")
                        }
                    })
        }


    override fun onPause() {
        super.onPause()
        PreferUtils.putInt(this@MainActivity, Constants.current_index, currentIndex)

    }


    override fun onResume() {
        super.onResume()
        hideAllTitle()
        currentIndex = PreferUtils.getInt(this, Constants.current_index)
        initTab(NetUtil.checkNet(this), currentIndex)

        val role_array = PreferUtils.getString(this, Constants.Role_array)
        if (!role_array.isNullOrEmpty())
            roles = JSON.parseArray(role_array, Roles::class.java)


    }

    private var exitTime: Long = 0 // 用来计算返回键的点击间隔时间
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action === KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                showStr("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    fun isLocationEnabled(context: Context): Boolean {

        var locationMode = Settings.Secure.LOCATION_MODE_OFF
        try {
            locationMode =
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            // do nothing
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF

    }


    private fun deal() {
        var upload_i = 0
        upload_i += LitePal.where("isUpLoad=?", "0").find(FarmListCache::class.java).size
        upload_i += LitePal.where("isUpLoad=?", "0").find(AnimalSaveCache::class.java).size
//        upload_i += LitePal.where("isUpLoad=?", "0").find(AllBillDetailCache::class.java).size
//        upload_i += LitePal.where("isUpLoad=? ", "0").find(BanAnInfo::class.java).size
//        upload_i += LitePal.where("isUpLoad_Basic=? ", "0").find(ChaKanDetailListCache::class.java).size
//        upload_i += LitePal.where("UpLoad_sunshi=? ", "0").find(LossListCache::class.java).size

        if (upload_i > 0) {
            DialogUpLoad(this, upload_i.toString(), object : GetBool {
                override fun getBool(tf: Boolean) {
                    if (tf) {
                        Utils.goToNextUI(ActivityFile::class.java)
                    }

                }
            }).show()
        }
    }
}