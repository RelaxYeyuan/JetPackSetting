package com.neusoft.mc2.setting.wifi

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import java.util.*
import kotlin.collections.ArrayList

object WifiHelper {
    const val WEP = "WEP"
    const val PSK = "PSK"
    const val EAP = "EAP"
    const val WPA = "WPA"

    fun configOrCreateWifi(manager: WifiManager, wifi: Wifi, password: String?): Int {
        val configurations =
            manager.configuredNetworks
        for (configuration in configurations) {
            if (configuration.SSID == wifi.SSID) return configuration.networkId
        }
        val configuration = createWifiConfiguration(wifi, password)
        return saveWifiConfiguration(manager, configuration)
    }

    fun deleteWifiConfiguration(manager: WifiManager, ssid: String): Boolean {
        val configurations =
            manager.configuredNetworks
        for (configuration in configurations) {
            if (configuration.SSID == ssid) {
                var ret = manager.removeNetwork(configuration.networkId)
                ret = ret and manager.saveConfiguration()
                return ret
            }
        }
        return false
    }

    private fun createWifiConfiguration(wifi: Wifi, password: String?): WifiConfiguration {
        val configuration = WifiConfiguration()
        if (password.isNullOrEmpty()) {
            configuration.hiddenSSID = false
            configuration.status = WifiConfiguration.Status.ENABLED
            configuration.SSID = wifi.SSID
            when {
                wifi.capabilities.contains(WEP) -> {
                    configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                    configuration.wepTxKeyIndex = 0
                    configuration.wepKeys[0] = ""
                }
                wifi.capabilities.contains(PSK) -> {
                    configuration.preSharedKey = ""
                }
                wifi.capabilities.contains(EAP) -> {
                    configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
                    configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                    configuration.preSharedKey = ""
                }
                else -> {
                    configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    configuration.preSharedKey = null
                }
            }
        } else {
            configuration.allowedAuthAlgorithms.clear()
            configuration.allowedGroupCiphers.clear()
            configuration.allowedKeyManagement.clear()
            configuration.allowedPairwiseCiphers.clear()
            configuration.allowedProtocols.clear()
            configuration.SSID = wifi.SSID
            if (wifi.capabilities.contains(WEP)) {
                configuration.preSharedKey = "\"" + password + "\""
                configuration.hiddenSSID = true
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                configuration.wepTxKeyIndex = 0
            } else if (wifi.capabilities.contains(WPA)) {
                configuration.hiddenSSID = true
                configuration.preSharedKey = "\"" + password + "\""
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            } else {
                configuration.wepKeys[0] = ""
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                configuration.wepTxKeyIndex = 0
            }
        }
        return configuration
    }

    private fun saveWifiConfiguration(manager: WifiManager, configuration: WifiConfiguration): Int {
        val networkId = manager.addNetwork(configuration)
        manager.saveConfiguration()
        return networkId
    }

    fun removeDuplicate(list: ArrayList<Wifi>): ArrayList<Wifi> {
        list.sortWith(Comparator { l, r -> r.level - l.level })
        val set: ArrayList<Wifi> = ArrayList()
        for (wifi in list) {
            if (!set.contains(wifi)) {
                if (wifi.isConnected) set.add(0, wifi) else set.add(wifi)
            }
        }
        return set
    }
}