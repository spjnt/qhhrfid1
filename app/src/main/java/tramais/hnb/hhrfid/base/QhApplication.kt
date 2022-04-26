package tramais.hnb.hhrfid.base

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Process
import androidx.multidex.MultiDex
import com.apkfuns.logutils.LogLevel
import com.apkfuns.logutils.LogUtils
import com.grandtech.standard.IComponentApplication
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.OkHttpClient
import org.litepal.LitePal
import org.litepal.LitePal.initialize
import org.litepal.LitePalDB
import org.litepal.parser.LitePalParser
import tramais.hnb.hhrfid.util.FileUtil
import java.util.concurrent.TimeUnit

/**
 * 当定义启动时，实例化 一个单例的Application
 */
class QhApplication : Application() {
    // 您的Application中添加如下代码
    val MODULESLIST = arrayOf("com.grandtech.standard.Application")

    /**
     *
     */
    //	private User user;//用户信息
    override fun onCreate() {
        context = this.applicationContext
        handler = Handler()
        mainThread = Thread.currentThread() //取得当前的线程
        mainThreadId = Process.myTid() //取得当前的线程的id

        initNet()
        //数据库缓存
        initialize(this)
        //  LitePalDB.fromDefault("Qnb.db").dbName

        initLog()
        //国源SDK
        modulesApplicationInit()


        super.onCreate()
    }

    /**
     * @param base
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun initLog() {
        // 初始化日志工具类  本地日志
        LogUtils.getLogConfig()
                .configAllowLog(true) // 是否显示log
                .configTagPrefix("Qnb")
                .configShowBorders(true) // 显示分割线
                //                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")
                .configLevel(LogLevel.TYPE_ERROR)
        //腾讯日志
        val strategy = UserStrategy(context)
        CrashReport.initCrashReport(context, "34e5bbeeb2", true, strategy)
    }

    fun initNet() {

        // 初始话网络请求
        val okHttpClient: OkHttpClient = OkHttpClient.Builder() //                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS) //其他配置
                .build()
        OkHttpUtils.initClient(okHttpClient)
    }

    private fun modulesApplicationInit() {
        for (moduleImpl in MODULESLIST) {
            try {
                val clazz = Class.forName(moduleImpl)
                val obj = clazz.newInstance()
                if (obj is IComponentApplication) {
                    obj.init(this)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        var context //上下文
                : Context? = null
            private set
        var handler //线程通信工具
                : Handler? = null
            private set
        var mainThread //主线程
                : Thread? = null
            private set
        var mainThreadId //主线程的id
                = 0
            private set
    }

}