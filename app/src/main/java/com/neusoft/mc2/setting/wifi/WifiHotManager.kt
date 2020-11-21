package com.neusoft.mc2.setting.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration.KeyMgmt.NONE
import android.net.wifi.WifiConfiguration.KeyMgmt.WPA_PSK
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.data.config.SettingConstant.KEY_WIFI_HOT_STATUS
import com.neusoft.mc2.setting.data.config.SettingConstant.KEY_WIFI_NAME
import com.neusoft.mc2.setting.data.config.SettingConstant.KEY_WIFI_PASSWORD
import com.neusoft.mc2.setting.data.config.SettingConstant.KEY_WIFI_TYPE
import com.neusoft.mc2.setting.data.config.SettingConstant.WIFI_AP_ACTION
import com.neusoft.mc2.setting.data.config.SettingConstant.WIFI_HOT_NAME
import com.neusoft.mc2.setting.data.config.SettingConstant.WIFI_HOT_PASSWORD
import com.neusoft.mc2.setting.utils.SharePreferenceUtils
import com.neusoft.mc2.setting.utils.WifiHotUtil



class WifiHotManager(private val context: Context) {

    private val TAG = "WifiHotManager"

    //修改名称协议SSID后重启热点
    private var isChangeWifiAp = false
    private var wifiHotIsOn: Boolean
    private var wifiHotName: String
    private var wifiHotPassword: String
    private var wifiHotWapType = WPA_PSK
    private val sharePreferenceUtils by lazy { SharePreferenceUtils.getInstance(context) }

    //是否正在打开热点
    var isWifiHotEnabling = false
    private var wifiHotUtil: WifiHotUtil = WifiHotUtil.getInstance(context)

    init {
        registerWifiReceiver()

        wifiHotIsOn = sharePreferenceUtils.getIntData(KEY_WIFI_HOT_STATUS, 0) == 1
        wifiHotName = sharePreferenceUtils.getStringData(KEY_WIFI_NAME, WIFI_HOT_NAME)
        wifiHotPassword = sharePreferenceUtils.getStringData(KEY_WIFI_PASSWORD, WIFI_HOT_PASSWORD)
        wifiHotWapType = sharePreferenceUtils.getIntData(KEY_WIFI_TYPE, wifiHotWapType)
    }

    // wifi热点开关
    fun setWifiApEnabled(enabled: Boolean) {
        if (enabled) {
            Log.d(TAG, "打开热点")
            wifiHotUtil.startWifiAp(wifiHotName, wifiHotPassword, wifiHotWapType)
            isWifiHotEnabling = true
        } else {
            Log.d(TAG, "关闭热点")
            wifiHotUtil.closeWifiAp()
        }
    }

    private fun showReopenWifiHotDialog() {
        wifiHotNameListener?.onShowWifiHotReopenDialog()
    }

    fun getWifiHotName() {
        sharePreferenceUtils.getStringData(KEY_WIFI_NAME, WIFI_HOT_NAME).also { name ->
            wifiHotNameListener?.onUpdateHotName(name)
        }
    }

    var wifiHotNameListener: WifiHotNameListener? = null

    interface WifiHotNameListener {
        fun onUpdateHotName(name: String)

        fun onUpdateHotWifiOpenView()

        fun onUpdateHotWifiCloseView()

        fun onUpdateHotProtocolView(typeStatus: String?)

        fun onUpdateHotSSIDView(ssid: String?)

        fun onShowWifiHotReopenDialog()
    }

    private fun registerWifiReceiver() {
        Log.d(TAG, "registerWifiReceiver")
        val mFilter = IntentFilter()
        mFilter.addAction(WIFI_AP_ACTION)
        context.registerReceiver(wifiReceiver, mFilter)
    }

    fun unregisterWifiReceiver() {
        context.unregisterReceiver(wifiReceiver)
    }

    fun getWifiTextByWpaType() {
        var typeStatus: String? = ""
        wifiHotWapType = sharePreferenceUtils.getIntData(KEY_WIFI_TYPE, wifiHotWapType)
        when (wifiHotWapType) {
            NONE -> {
                typeStatus = context.getString(R.string.text_wifi_hot_none)
            }
            WPA_PSK -> {
                typeStatus = context.getString(R.string.text_wifi_hot_wpa)
            }
            4 -> {
                typeStatus = context.getString(R.string.text_wifi_hot_wpa2)
            }
        }

        wifiHotNameListener?.onUpdateHotProtocolView(typeStatus)
    }

    fun getWifiSSIDStatus() {
        sharePreferenceUtils.getStringData(KEY_WIFI_PASSWORD, WIFI_HOT_PASSWORD).also {
            wifiHotNameListener?.onUpdateHotSSIDView(it)
        }
    }

    private val wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WIFI_AP_ACTION == action) { // 便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                val state = intent.getIntExtra("wifi_state", 0)
                Log.e("lzj", "hot---------state = $state")
                isWifiHotEnabling = false
                when (state) {
                    11 -> if (isChangeWifiAp) {
                        setWifiApEnabled(true)
                    } else {
                        wifiHotIsOn = false
                        sharePreferenceUtils.saveIntData(KEY_WIFI_HOT_STATUS, 0)
                        wifiHotNameListener?.onUpdateHotWifiOpenView()
                        getWifiHotName()
                        getWifiTextByWpaType()
                        getWifiSSIDStatus()
                    }
                    13 -> if (isChangeWifiAp) {
                        isChangeWifiAp = false
                    } else {
                        wifiHotIsOn = true
                        sharePreferenceUtils.saveIntData(KEY_WIFI_HOT_STATUS, 1)
                        wifiHotNameListener?.onUpdateHotWifiCloseView()
                    }
                    else -> {
                    }
                }
            }
        }
    }
}