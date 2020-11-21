package com.neusoft.mc2.setting.wifi

import android.content.Context
import com.neusoft.mc2.setting.utils.NetWorkUtils.isWifiConnected

class NewWifiManager private constructor(context: Context) :
    BaseWifiManager(context) {

    companion object {
        fun create(context: Context): IWifiManager {
            return NewWifiManager(context)
        }
    }

    override var isConnecting = false

    override val isOpened: Boolean
        get() = manager.isWifiEnabled

    override fun openWifi() {
        if (!manager.isWifiEnabled) manager.isWifiEnabled = true
    }

    override fun closeWifi() {
        if (manager.isWifiEnabled) manager.isWifiEnabled = false
    }

    override fun scanWifi() {
        manager.startScan()
    }

    override val isWifiConnected: Boolean
        get() = isWifiConnected(context)

    override fun disConnectWifi(): Boolean {
        return manager.disconnect()
    }

    override fun connectEncryptWifi(wifi: Wifi, password: String): Boolean {
        if (manager.connectionInfo != null && wifi.SSID == manager.connectionInfo.ssid) return true
        val networkId = WifiHelper.configOrCreateWifi(manager, wifi, password)
        val ret = manager.enableNetwork(networkId, true)
        modifyWifi(wifi.SSID, "开始连接...")
        return ret
    }

    override fun connectSavedWifi(wifi: Wifi): Boolean {
        val networkId = WifiHelper.configOrCreateWifi(manager, wifi, null)
        val ret = manager.enableNetwork(networkId, true)
        modifyWifi(wifi.SSID, "开始连接...")
        return ret
    }

    override fun connectOpenWifi(wifi: Wifi): Boolean {
        val ret = connectEncryptWifi(wifi, "")
        modifyWifi(wifi.SSID, "开始连接...")
        return ret
    }

    override fun removeWifi(ssid: String): Boolean {
        val ret = WifiHelper.deleteWifiConfiguration(manager, ssid)
        modifyWifi()
        return ret
    }

    override val wifi: List<Wifi>
        get() = wifis

}