package tramais.hnb.hhrfid.interfaces

import tramais.hnb.hhrfid.util.NetworkType


interface NetStateChangeObserver {
    fun onNetDisconnected()
    fun onNetConnected(networkType: NetworkType?)
}