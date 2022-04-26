package tramais.hnb.hhrfid.base

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.hc.pda.HcPowerCtrl
import com.pow.api.cls.RfidPower
import com.rscja.deviceapi.RFIDWithUHF
import com.rscja.deviceapi.exception.ConfigurationException
import com.uhf.api.cls.Reader
import com.uhf.api.cls.Reader.READER_ERR
import com.uhf.api.cls.Reader.TAGINFO
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetInt
import tramais.hnb.hhrfid.ui.dialog.DialogAvi
import tramais.hnb.hhrfid.ui.view.CustemTitle
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.SoundUtil
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    var mCustomTitle: CustemTitle? = null

    @JvmField
    var mReader: RFIDWithUHF? = null
    var timer: Timer? = null
    var task: TimerTask? = null
    private var parentLinearLayout //把父类的activity和子类的activity的view都add到这里来
            : LinearLayout? = null
    private var dialogAvi: DialogAvi? = null
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        initContentView(R.layout.activity_base)
        if (mActivities == null) {
            mActivities = Stack()
        }
        mActivities!!.push(this)
    }

    private fun initContentView(layoutResID: Int) {
        // TODO Auto-generated method stub
        val group = findViewById<View>(android.R.id.content) as ViewGroup //得到窗口的根布局
        group.removeAllViews() //首先先移除在根布局上的组件
        //创建自定义父布局
        parentLinearLayout = LinearLayout(this)
        parentLinearLayout!!.orientation = LinearLayout.VERTICAL
        group.addView(parentLinearLayout) //将自定义的父布局，加载到窗口的根布局上
        LayoutInflater.from(this).inflate(layoutResID, parentLinearLayout, true) //这句话的意思就是将自定义的子布局加到parentLinearLayout上，true的意思表示添加上去
        mCustomTitle = group.findViewById(R.id.custom_title)
    }

    @JvmOverloads
    fun showAvi(desc: String? = "") {
        if (dialogAvi == null) dialogAvi = DialogAvi(this, desc)
        if (dialogAvi!!.isShowing) dialogAvi!!.setText(desc)
        if (!dialogAvi!!.isShowing && !isFinishing) dialogAvi!!.show()
    }

    fun hideAvi() {
        if (dialogAvi == null) dialogAvi = DialogAvi(this, "")
        if (dialogAvi != null && !isFinishing) dialogAvi!!.dismiss()
    }

    fun hideRootDelete(i: Int) {
        mCustomTitle!!.hideRootDelete(i)
    }

    /**
     * 这句的意思表示将MainActivity的布局又加到parentLinearLayout上
     */
    @SuppressLint("ResourceAsColor")
    override fun setContentView(layoutResID: Int) {
        LayoutInflater.from(this).inflate(layoutResID, parentLinearLayout, true)
        initView()
        initData()
        initListner()
        if (ifC72()) {
            Thread { initUhf() }.start()
        }
        if (ifHC720s()) {
            Thread { initHC720s() }.start()
        }
        SoundUtil.initSound(this@BaseActivity)
        immersionBar {
            statusBarDarkFont(true, 0.2F)
                    .fitsSystemWindows(true)
                    .statusBarColor(R.color.white)
        }
        //initSound(BaseActivity.this);
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        parentLinearLayout!!.addView(view)
    }

    fun ifNmg(): Boolean {
        return Config.BASE_URL.contains(Config.nmg)
    }

    protected val tag: String
        protected get() = "main"

    fun finishActivities(tag: String) {
        if (mActivities!!.empty()) {
            return
        }
        var temp: Stack<BaseActivity?>? = Stack<BaseActivity?>()
        for (activity in mActivities!!) {
            if (activity != null) {
                temp!!.push(activity)
            }
        }
        for (activity in temp!!) {
            mActivities!!.remove(activity)
            activity!!.finish()
        }
        temp = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mActivities != null) {
            mActivities!!.remove(this)
        }
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
        parentLinearLayout!!.addView(view, params)
    }

    protected abstract fun initView() //初始化控件
    protected abstract fun initData() //初始化数据
    protected abstract fun initListner() //初始化监听事件

    fun showStr(show: String?) {
        if (!TextUtils.isEmpty(show)) {
            runOnUiThread { Toast.makeText(this@BaseActivity, show, Toast.LENGTH_SHORT).show() }
        }
    }

    val ivBack: ImageView
        get() = mCustomTitle!!.ivBack

    fun setTitleText(title: String?) {
        mCustomTitle!!.setTitleText(title)
    }

    /*   public void hideAllTitle() {
           mCustomTitle.hideAllTitle();
       }*/
    fun setTitleText(title: Int) {
        mCustomTitle!!.setTitleText(title)
    }

    fun setTitlTextColor(color: Int) {
        mCustomTitle!!.setTitleTextColor(color)
    }

    fun setRightText(deleteText: String?) {
        mCustomTitle!!.setmRootDeleteText(deleteText)
    }

    fun showRootSetting() {
        mCustomTitle!!.showRootSetting()
    }

    fun hideBackImage() {
        mCustomTitle!!.hideBackImage()
    }

    fun hideAllTitle() {
        mCustomTitle!!.visibility = View.GONE
    }

    val companyNum: String
        get() = PreferUtils.getString(this, Constants.companyNumber)
    val userNum: String
        get() = PreferUtils.getString(this, Constants.userNumber)
    val userName: String
        get() = PreferUtils.getString(this, Constants.UserName)

    fun ifC72(): Boolean {
        return Build.MODEL.contains("HC72") || Build.MODEL.equals("SAH6380")||Build.MODEL.contains("c72")||Build.MODEL.contains("HC720")
    }

    fun ifHC720s(): Boolean {
        return Build.MODEL.equals("HC720S")
    }

    fun initUhf() {
        try {
            if (mReader == null) {
                mReader = RFIDWithUHF.getInstance()
                mReader!!.init()
            }
        } catch (ex: ConfigurationException) {
            showStr("请确定是否使用正确的设备")
            ex.printStackTrace()
        }
    }

    var uhfReader: Reader? = null
    var power: RfidPower? = null
    var ctrl: HcPowerCtrl? = null
    var inventoryEpc: Boolean = false //盘存模式，EPC 或 TID
    var isReading = false //是否正在扫描

    fun initHC720s() {
        if (!ifHC720s()) return
        ctrl = HcPowerCtrl()
        ctrl!!.identityPower(1)
        if (uhfReader == null) {
            uhfReader = Reader()
            power = RfidPower(RfidPower.PDATYPE.NONE, applicationContext)
            if (power!!.PowerUp()) {
                val reader_err = uhfReader!!.InitReader_Notype("/dev/ttysWK0", 1)
                if (reader_err == Reader.READER_ERR.MT_OK_ERR) {
                    val apcf = uhfReader!!.AntPowerConf()
                    apcf.antcnt = 1
                    uhfReader!!.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, intArrayOf(1))
                    uhfReader!!.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, null)
                }
            }
        }
    }

    open fun startScan(handler: Handler) {
        if (!isReading) {
            val ants = intArrayOf(1)
            var option = 16
            if (!inventoryEpc) {
                option = 32768
            }
            val reader_err: READER_ERR = uhfReader!!.AsyncStartReading(ants, 1, option)
            Thread {
                val tagcnt = IntArray(1)
                synchronized(this) {
                    while (isReading) {
                        //  var er: READER_ERR
                        var er = uhfReader!!.AsyncGetTagCount(tagcnt)
                        if (er == READER_ERR.MT_OK_ERR) {
                            if (tagcnt[0] > 0) {
                                for (i in 0 until tagcnt[0]) {
                                    val taginfo: TAGINFO = uhfReader!!.TAGINFO()
                                    er = uhfReader!!.AsyncGetNextTag(taginfo)
                                    if (er == READER_ERR.MT_OK_ERR) {
                                        val epc = Reader.bytes_Hexstr(taginfo.EpcId)
                                        stopScan()
                                        reverseTag(epc, handler)
                                    }
                                }
                            }
                        }
                    }
                }
            }.start()
            if (reader_err == READER_ERR.MT_OK_ERR) {
                isReading = true
            }
        } else {
            stopScan()
        }
    }

    open fun stopScan() {
        isReading = false
        uhfReader!!.AsyncStopReading()

    }

    fun stopC72Read(mReader: RFIDWithUHF?) {
        if (mReader != null && ifC72()) {
            mReader.stopInventory()
        }
    }

    fun netTips() {
        if (!NetUtil.checkNet(this)) {
            showStr("请在联网环境下操作")
            return
        }

    }

    fun playSound(raw: Int) {
        mediaPlayer = MediaPlayer.create(this, raw)
        mediaPlayer!!.start()
    }

    fun onPauseaMedia() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
    }

    fun ReadTag(handler: Handler) {
        if (ifC72()) {
            if (mReader == null) return
            Thread {
                val srt_tag = mReader!!.inventorySingleTag()
                if (srt_tag.isNullOrEmpty()) {
                  //  stopC72Read(mReader)
                    return@Thread
                }
                reverseTag(srt_tag, handler)
            }.start()
        }
        if (ifHC720s()) {
            startScan(handler)
        }
    }

    private fun reverseTag(srt_tag: String, handler: Handler) {
        if (srt_tag.isNullOrEmpty()) return
        val ep = arrayOf("")
        getTime { count: Int ->
            if (count < 5) {
                if (!TextUtils.isEmpty(srt_tag)) {
                    if (srt_tag.length >= 16) {
                        ep[0] = srt_tag.substring(srt_tag.length - 15)
                    } else {
                        ep[0] = srt_tag
                    }
                    if (timer != null) {
                        timer!!.cancel()
                        timer = null
                    }
                    if (task != null) {
                        task!!.cancel()
                        task = null
                    }
                    playSound(R.raw.barcodebeep)
                    //                        SoundUtil.playSound(1);
                    val msg = handler.obtainMessage()
                    msg.obj = ep[0]
                    msg.what = GET_EPC_C72
                    handler.sendMessage(msg)
                }
            } else if (count == 5) {
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
                if (task != null) {
                    task!!.cancel()
                    task = null
                }
                val msg = handler.obtainMessage(GET_EPC_C72)
                msg.obj = "停止扫描,请重新按键"
                handler.sendMessageDelayed(msg, 4000)
            }
        }
    }

    fun getTime(getInt: GetInt) {
        val count = intArrayOf(0)
        if (timer == null && task == null) {
            timer = Timer()
            task = object : TimerTask() {
                override fun run() {
                    getInt.getInt(count[0]++)
                }
            }
            timer!!.schedule(task, 0, 1000)
        }
    }

    fun formatColor(currentPage: Int, totalPage: Int): Spanned {
        return Html.fromHtml("<font color='#646f7f'  size='5'>" + "当前第" + "</font>" +
                "&nbsp;&nbsp" + "<font color='#a7002b'>" + currentPage + "&nbsp;&nbsp" + "</font>" +
                "<font color='#646f7f'  size='5'>" + "页,共" + "</font>" + "&nbsp;&nbsp" + "<font color='#a7002b'>" + totalPage + "&nbsp;&nbsp" + "</font>"
                + "<font color='#646f7f'  size='5'>" + "页" + "</font>")
    }


    companion object {
        const val GET_EPC_C72 = 1 shl 100
        private var mActivities: Stack<BaseActivity?>? = null
    }
}