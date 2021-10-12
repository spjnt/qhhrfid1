package tramais.hnb.hhrfid.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

object NetUtil {
    fun checkNet(context: Context): Boolean {
        /**
         * 判断是否为WiFi连接
         */
        val isWIFI = isWIFIConnection(context)

        /**
         * 判断是否为移动网络连接
         */
        val isMobile = isMobileConnection(context)
        return isMobile || isWIFI
    }

    @SuppressLint("MissingPermission")
    private fun isMobileConnection(context: Context): Boolean {
        //获取连接管理器
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //获取mobile连接
        val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return networkInfo?.isConnected ?: false
    }
    @SuppressLint("MissingPermission")
    fun isWIFIConnection(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //获取mobile连接
        val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return networkInfo?.isConnected ?: false
    }

    const val TYPE_WIFI = 1
    const val TYPE_MOBILE = 2
    const val TYPE_NOT_CONNECTED = 0
    @SuppressLint("MissingPermission")
    fun getConnectivityStatus(context: Context): Int {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }
}