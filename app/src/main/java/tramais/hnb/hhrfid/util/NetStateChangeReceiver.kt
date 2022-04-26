package tramais.hnb.hhrfid.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import tramais.hnb.hhrfid.interfaces.NetStateChangeObserver
import java.util.ArrayList

class NetStateChangeReceiver : BroadcastReceiver() {
    private object InstanceHolder {
        val INSTANCE = NetStateChangeReceiver()
    }

    private val mObservers: MutableList<NetStateChangeObserver>? = ArrayList<NetStateChangeObserver>()
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            val networkType:NetworkType = NetworkUtil.getNetworkType(context)
           //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   Log.i("NetStateChangeReceiver", networkType.toString())
            notifyObservers(networkType)
        }
    }

    private fun notifyObservers(networkType: NetworkType) {
        if (networkType === NetworkType.NETWORK_NO) {
            if (mObservers != null) {
                for (observer in mObservers) {
                    observer.onNetDisconnected()
                }
            }
        } else {
            if (mObservers != null) {
                for (observer in mObservers) {
                    observer.onNetConnected(networkType)
                }
            }
        }
    }

    companion object {
        fun registerReceiver(context: Context) {
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(InstanceHolder.INSTANCE, intentFilter)
        }

        fun unRegisterReceiver(context: Context) {
            context.unregisterReceiver(InstanceHolder.INSTANCE)
        }

        fun registerObserver(observer: NetStateChangeObserver?) {
            if (observer == null) {
                return
            }
            if (!InstanceHolder.INSTANCE.mObservers!!.contains(observer)) {
                InstanceHolder.INSTANCE.mObservers.add(observer)
            }
        }

        fun unRegisterObserver(observer: NetStateChangeObserver?) {
            if (observer == null) {
                return
            }
            if (InstanceHolder.INSTANCE.mObservers == null) {
                return
            }
            InstanceHolder.INSTANCE.mObservers.remove(observer)
        }
    }
}