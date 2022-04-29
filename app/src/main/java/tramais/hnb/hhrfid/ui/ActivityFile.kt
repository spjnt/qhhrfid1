package tramais.hnb.hhrfid.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.litePalBean.FarmListCache
import tramais.hnb.hhrfid.service.UpLoadSeries
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.Utils

class ActivityFile : BaseActivity() {
    var upload_i = 0
    var buffer = StringBuffer()
    var upLoad_desc: String? = null
    private var mBtnLoad: Button? = null
    private var mTvFarm: TextView? = null
    private var mEarNum: TextView? = null
    private var mScrollView: ScrollView? = null
    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                3 -> {
                    val message = msg.obj
                    if (message != null) {
                        message as String
                        if (message.contains("耳标信息上传完成")) {
                            stopService(Intent(this@ActivityFile, UpLoadSeries::class.java))
                            hideAvi()
                            delay(upload_i)
                        }
                        deal(isDeal = false)
                    }

                }
            }
        }
    }
    private var mTvDesc: TextView? = null
    private var receiver: MyBReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // EventBus.getDefault().register(this)
    }

    fun delay(total: Int) {
        if (total == 0) {
            showStr("数据上传完成")
            handler.postDelayed({ Utils.goToNextUI(MainActivity::class.java) }, 300)
        }
    }

    override fun initView() {
        setTitleText("数据上传")
        mTvDesc = findViewById(R.id.tv_desc)
        mBtnLoad = findViewById(R.id.btn_load)
        mTvFarm = findViewById(R.id.tv_farm)
        mEarNum = findViewById(R.id.ear_num)
        mScrollView = findViewById(R.id.scroll_view)
    }

    override fun initData() {
        mBtnLoad!!.setOnClickListener { upLoad() }
    }

    override fun initListner() {}
    override fun onResume() {
        super.onResume()
        deal(true)
    }

    var farmSize = 0
    var animalSize = 0

    @SuppressLint("SetTextI18n")
    private fun deal(isDeal: Boolean) {
        upload_i = 0
        //where("isUpLoad=?", "0").
        val find = LitePal.where("isUpLoad =?", "0").find(FarmListCache::class.java)
        if (isDeal)
            farmSize = find.size
        mTvFarm!!.text = "待上传数量 ${find.size}"
        upload_i += find.size
        //where("isUpLoad=?", "0")
        val find1 = LitePal.where("isUpLoad=?", "0").find(AnimalSaveCache::class.java)
        if (isDeal)
            animalSize = find1.size
        mEarNum!!.text = "待上传数量 ${find1.size}"
        upload_i += find1.size
    }

    private fun upLoad() {
        if (!NetUtil.checkNet(this)) {
            showStr("请连接网络")
            return
        }
        if (upload_i == 0) {
            showStr("暂无上传数据")
            return
        }
        buffer.setLength(0)
        upLoad_desc = ""
        runOnUiThread { mTvDesc!!.text = " " }
        if (!isFinishing) showAvi("正在上传")

        val intent = Intent(this, UpLoadSeries::class.java)
        startService(intent)
        //注册广播接收器
        if (receiver == null) receiver = MyBReceiver()
        val filter = IntentFilter()
        filter.addAction("tramais.hnb.hhrfid.service.UpLoadSercie")
        registerReceiver(receiver, filter)

        // upload_i = 0
    }

    override fun onDestroy() {
        //结束服务
        stopService(Intent(this, UpLoadSeries::class.java))
        if (receiver != null) unregisterReceiver(receiver)
        super.onDestroy()
    }

    /* @Subscribe
     public fun onEvent(message: MessageEvent) {
         val texg = message.data
         LogUtils.e("textg  $texg")
         runOnUiThread { mEarNum!!.text = "待上传数量 (${texg}/${animalSize})" }
     }
 */


    inner class MyBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            upLoad_desc = intent.getStringExtra(Constants.upLoad_desc)
            buffer.append(upLoad_desc)
            buffer.append("\n")
            // LogUtils.e("upLoad_desc   $upLoad_desc")
            if (upLoad_desc == "养殖户信息上传完成")
                buffer.append("耳标信息上传中...\n")
            runOnUiThread { mTvDesc!!.text = buffer.toString() }
            scroll2Bottom(mScrollView, mTvDesc)
            val message = Message()
            message.what = 3
            message.obj = upLoad_desc
            handler.sendMessage(message)
        }
    }

    fun scroll2Bottom(scroll: ScrollView?, inner: View?) {
        val handler = Handler()
        handler.post(Runnable {
            if (scroll == null || inner == null) {
                return@Runnable
            }
            // 内层高度超过外层
            var offset = (inner.measuredHeight - scroll.measuredHeight)
            if (offset < 0) {
                offset = 0
            }
            scroll.scrollTo(0, offset)
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            Utils.goToNextUI(MainActivity::class.java)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}