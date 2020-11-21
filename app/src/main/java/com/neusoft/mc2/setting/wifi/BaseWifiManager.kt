package com.neusoft.mc2.setting.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.NetworkInfo.DetailedState
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import java.util.*

/**
 * https://github.com/hacknife/WifiManager
 */
abstract class BaseWifiManager internal constructor(val context: Context) : IWifiManager {

    enum class State {
        ENABLING, ENABLED, DISABLING, DISABLED, UNKNOWN
    }

    interface OnWifiChangeListener {
        fun onWifiChanged(wifis: List<Wifi>)
    }

    interface OnWifiConnectListener {
        fun onConnectChanged(status: Int)
    }

    interface OnWifiStateChangeListener {
        fun onStateChanged(state: State)
    }

    var connectSSID: String? = null
    var manager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var wifiReceiver: WifiReceiver
    var wifis = ArrayList<Wifi>()

    private var onWifiChangeListener: OnWifiChangeListener? = null

    override fun setOnWifiChangeListener(onWifiChangeListener: OnWifiChangeListener) {
        this.onWifiChangeListener = onWifiChangeListener
    }

    private var onWifiConnectListener: OnWifiConnectListener? = null

    override fun setOnWifiConnectListener(onWifiConnectListener: OnWifiConnectListener) {
        this.onWifiConnectListener = onWifiConnectListener
    }

    private var onWifiStateChangeListener: OnWifiStateChangeListener? = null
    override fun setOnWifiStateChangeListener(onWifiStateChangeListener: OnWifiStateChangeListener) {
        this.onWifiStateChangeListener = onWifiStateChangeListener
    }

    companion object {
        private const val TAG = "BaseWifiManager"
        const val WIFI_STATE_DISABLED = 1
        const val WIFI_STATE_DISABLING = 2
        const val WIFI_STATE_ENABLING = 3
        const val WIFI_STATE_ENABLED = 4
        const val WIFI_STATE_UNKNOWN = 5
        const val WIFI_STATE_MODIFY = 6
        const val WIFI_STATE_CONNECTED = 7
        const val WIFI_STATE_UNCONNECTED = 8
        const val WIFI_STATE_CONNECT_FAIL = 9
    }

    init {
        IntentFilter().run {
            wifiReceiver = WifiReceiver()
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
            context.registerReceiver(wifiReceiver, this)
        }
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WIFI_STATE_DISABLED -> onWifiStateChangeListener?.onStateChanged(
                    State.DISABLED
                )
                WIFI_STATE_DISABLING -> onWifiStateChangeListener?.onStateChanged(
                    State.DISABLING
                )
                WIFI_STATE_ENABLING -> onWifiStateChangeListener?.onStateChanged(
                    State.ENABLING
                )
                WIFI_STATE_ENABLED -> onWifiStateChangeListener?.onStateChanged(
                    State.ENABLED
                )
                WIFI_STATE_UNKNOWN -> onWifiStateChangeListener?.onStateChanged(
                    State.UNKNOWN
                )
                WIFI_STATE_MODIFY -> onWifiChangeListener?.onWifiChanged(
                    wifis
                )
                WIFI_STATE_CONNECTED -> onWifiConnectListener?.onConnectChanged(
                    WIFI_STATE_CONNECTED
                )
                WIFI_STATE_UNCONNECTED -> onWifiConnectListener?.onConnectChanged(
                    WIFI_STATE_UNCONNECTED
                )
                WIFI_STATE_CONNECT_FAIL -> onWifiConnectListener?.onConnectChanged(
                    WIFI_STATE_CONNECT_FAIL
                )
            }
        }
    }

    override fun destroy() {
        context.unregisterReceiver(wifiReceiver)
        handler.removeCallbacksAndMessages(null)
    }

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            if (TextUtils.isEmpty(action)) return
            when (action) {
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN
                    )
                    var what = 0
                    when (state) {
                        WifiManager.WIFI_STATE_DISABLED -> what =
                            WIFI_STATE_DISABLED
                        WifiManager.WIFI_STATE_DISABLING -> what =
                            WIFI_STATE_DISABLING
                        WifiManager.WIFI_STATE_ENABLING -> what =
                            WIFI_STATE_ENABLING
                        WifiManager.WIFI_STATE_ENABLED -> {
                            scanWifi()
                            what = WIFI_STATE_ENABLED
                        }
                        WifiManager.WIFI_STATE_UNKNOWN -> what =
                            WIFI_STATE_UNKNOWN
                    }
                    handler.sendEmptyMessage(what)
                }
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val isUpdated =
                        intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    if (isUpdated) modifyWifi()
                } else {
                    modifyWifi()
                }
                WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                    val info =
                        intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                            ?: return
                    val state = info.detailedState ?: return
                    val SSID = info.extraInfo
                    if (TextUtils.isEmpty(SSID)) return
                    when (state) {
                        DetailedState.IDLE -> {
                        }
                        DetailedState.SCANNING -> {
                        }
                        DetailedState.AUTHENTICATING -> {
                            modifyWifi(SSID, "身份验证中...")
                        }
                        DetailedState.OBTAINING_IPADDR -> {
                            modifyWifi(SSID, "获取地址信息...")
                        }
                        DetailedState.CONNECTED -> {
                            //                    modifyWifi(SSID, "已连接");
                            modifyWifi()
                            handler.sendEmptyMessage(WIFI_STATE_CONNECTED)
                        }
                        DetailedState.SUSPENDED -> {
                            modifyWifi(SSID, "连接中断")
                        }
                        DetailedState.DISCONNECTING -> {
                            modifyWifi(SSID, "断开中...")
                        }
                        DetailedState.DISCONNECTED -> {
                            //                    modifyWifi(SSID, "已断开");
                            modifyWifi()
                            handler.sendEmptyMessage(WIFI_STATE_UNCONNECTED)
                        }
                        DetailedState.FAILED -> {
                            modifyWifi(SSID, "连接失败")
                        }
                        DetailedState.BLOCKED -> {
                            modifyWifi(SSID, "wifi无效")
                        }
                        DetailedState.VERIFYING_POOR_LINK -> {
                            modifyWifi(SSID, "信号差")
                        }
                        DetailedState.CAPTIVE_PORTAL_CHECK -> {
                            modifyWifi(SSID, "强制登陆门户")
                        }
                        else -> {
                        }
                    }
                }
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> {

                    //当前环境Android4.4.2
                    //1.当输入错误的密码会返回消息
                    //2.当前已连接
                    if (intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1) == 1) {
                        Log.d(TAG, "onReceive: 密码错误")
                        handler.sendEmptyMessage(WIFI_STATE_CONNECT_FAIL)
                    }
                }
            }
        }
    }

    protected fun modifyWifi() {
        synchronized(wifis) {
            val results = manager.scanResults
            val wifiList = LinkedList<Wifi>()
            var mergeList = ArrayList<Wifi>()
            val configurations = manager.configuredNetworks
            val connectedSSID = manager.connectionInfo.ssid
            val ipAddress = manager.connectionInfo.ipAddress
            for (result in results) {
                val mergeObj =
                    Wifi.create(result, configurations, connectedSSID, ipAddress) ?: continue
                mergeList.add(mergeObj)
            }
            mergeList = WifiHelper.removeDuplicate(mergeList)
            for (merge in mergeList) {
                var isMerge = false
                for (wifi in wifis) {
                    if (wifi == merge) {
                        wifiList.add(wifi.merge(merge))
                        isMerge = true
                    }
                }
                if (!isMerge) wifiList.add(merge)
            }
            wifis.clear()
            wifis.addAll(wifiList)
            handler.sendEmptyMessage(WIFI_STATE_MODIFY)
        }
    }

    protected fun modifyWifi(SSID: String, state: String) {
        synchronized(wifis) {
            val wifiList: MutableList<Wifi> = ArrayList()
            for (wifi in wifis) {
                if (SSID == wifi.SSID) {
                    wifi.state = state
                    wifiList.add(0, wifi)
                } else {
                    wifi.state = ""
                    wifiList.add(wifi)
                }
            }
            wifis.clear()
            wifis.addAll(wifiList)
            handler.sendEmptyMessage(WIFI_STATE_MODIFY)
        }
    }

}