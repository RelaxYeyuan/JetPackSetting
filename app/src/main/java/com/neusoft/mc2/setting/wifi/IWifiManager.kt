package com.neusoft.mc2.setting.wifi

interface IWifiManager {
    val isOpened: Boolean
    fun openWifi()
    fun closeWifi()
    fun scanWifi()
    var isConnecting: Boolean
    val isWifiConnected: Boolean
    fun disConnectWifi(): Boolean
    fun connectEncryptWifi(wifi: Wifi, password: String): Boolean
    fun connectSavedWifi(wifi: Wifi): Boolean
    fun connectOpenWifi(wifi: Wifi): Boolean
    fun removeWifi(ssid: String): Boolean
    val wifi: List<Wifi>
    fun setOnWifiConnectListener(onWifiConnectListener: BaseWifiManager.OnWifiConnectListener)
    fun setOnWifiStateChangeListener(onWifiStateChangeListener: BaseWifiManager.OnWifiStateChangeListener)
    fun setOnWifiChangeListener(onWifiChangeListener: BaseWifiManager.OnWifiChangeListener)
    fun destroy()
}