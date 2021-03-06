package tramais.hnb.hhrfid.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission



import tramais.hnb.hhrfid.util.NetworkType
import tramais.hnb.hhrfid.util.NetworkUtil
import java.lang.UnsupportedOperationException

class NetworkUtil private constructor() {
    companion object {
        @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
        private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
            val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.getActiveNetworkInfo()
        }

        /**
         * 获取当前网络类型
         * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
         */
        @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
        fun getNetworkType(context: Context): NetworkType {
            var netType: NetworkType = NetworkType.NETWORK_NO
            val info: NetworkInfo? = getActiveNetworkInfo(context)
            if (info != null && info.isAvailable()) {
                netType = when {
                    info.type == ConnectivityManager.TYPE_WIFI -> {
                        NetworkType.NETWORK_WIFI
                    }
                    info.type == ConnectivityManager.TYPE_MOBILE -> {
                        when (info.getSubtype()) {
                            TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.NETWORK_3G
                            TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_IWLAN -> NetworkType.NETWORK_4G
                            TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.NETWORK_2G
                            else -> {
                                val subtypeName: String = info.getSubtypeName()
                                if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                        || subtypeName.equals("WCDMA", ignoreCase = true)
                                        || subtypeName.equals("CDMA2000", ignoreCase = true)) {
                                   NetworkType.NETWORK_3G
                                } else {
                                    NetworkType.NETWORK_UNKNOWN
                                }
                            }
                        }
                    }
                    else -> {
                       NetworkType.NETWORK_UNKNOWN
                    }
                }
            }
            return netType
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}