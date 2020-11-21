package com.neusoft.mc2.setting.utils

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class WifiHotUtil(context: Context) {

    private val mWifiManager by lazy { context.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    companion object : SingletonHolder<WifiHotUtil, Context>(::WifiHotUtil)

    fun startWifiAp(ssid: String, passwd: String, type: Int) {
        Log.i("swm", "----ssid:$ssid----passwd:$passwd----type:$type")
        // wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
        if (mWifiManager.isWifiEnabled) {
            mWifiManager.isWifiEnabled = false
        }

//		if (!isWifiApEnabled()) {
        stratWifiAp(ssid, passwd, type)
        //		}
    }

    /**
     * 设置热点名称及密码，并创建热点
     *
     * @param mSSID
     * @param mPasswd
     */
    private fun stratWifiAp(mSSID: String, mPasswd: String, type: Int) {
        Log.i("swm", "stratWifiAp")
        var method1: Method? = null
        try {
            // 通过反射机制打开热点
            method1 = mWifiManager.javaClass.getMethod(
                "setWifiApEnabled",
                WifiConfiguration::class.java,
                Boolean::class.javaPrimitiveType
            )
            val netConfig = WifiConfiguration()
            netConfig.SSID = mSSID
            netConfig.preSharedKey = mPasswd
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            netConfig.allowedKeyManagement.set(type) //WifiConfiguration.KeyMgmt.WPA_PSK
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            method1.invoke(mWifiManager, netConfig, true)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }

    /**
     * 热点开关是否打开
     *
     * @return
     */
    val isWifiApEnabled: Boolean
        get() {
            try {
                val method =
                    mWifiManager.javaClass.getMethod("isWifiApEnabled")
                method.isAccessible = true
                return method.invoke(mWifiManager) as Boolean
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /**
     * 关闭WiFi热点
     */
    fun closeWifiAp() {
        if (isWifiApEnabled) {
            try {
                val method =
                    mWifiManager.javaClass.getMethod("getWifiApConfiguration")
                method.isAccessible = true
                val config = method.invoke(mWifiManager) as WifiConfiguration
                val method2 = mWifiManager.javaClass.getMethod(
                    "setWifiApEnabled", WifiConfiguration::class.java,
                    Boolean::class.javaPrimitiveType
                )
                method2.invoke(mWifiManager, config, false)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }

}