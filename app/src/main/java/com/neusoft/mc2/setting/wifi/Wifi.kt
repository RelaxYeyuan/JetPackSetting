package com.neusoft.mc2.setting.wifi

import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.text.TextUtils
import java.util.*

class Wifi {

    var name: String = ""
    var SSID: String = ""
    var isEncrypt = false
    var isSaved = false
    var isConnected = false
    var encryption: String = ""
    var description: String = ""
    var capabilities: String = ""
    var ip: String = ""
    var state: String = ""
    var level = 0
    var isConnecting = false
    var isClickSecond = false

    fun description(): String {
        return if (state.isEmpty()) description else state
    }

    fun description2(): String {
        return if (isConnected) String.format("%s(%s)", description(), ip) else description()
    }

    fun merge(merge: Wifi): Wifi {
        isSaved = merge.isSaved
        isConnected = merge.isConnected
        ip = merge.ip
        state = merge.state
        level = merge.level
        description = merge.description
        return this
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj == null || obj !is Wifi) false else obj.SSID == SSID
    }

    override fun hashCode(): Int {
        return SSID.hashCode()
    }

    companion object {
        fun create(
            result: ScanResult,
            configurations: List<WifiConfiguration>,
            connectedSSID: String,
            ipAddress: Int
        ): Wifi? {
            if (TextUtils.isEmpty(result.SSID)) return null
            return Wifi().apply {
                isConnected = false
                isSaved = false
                name = result.SSID
                SSID = "\"" + result.SSID + "\""
                isConnected = SSID.equals(connectedSSID) && ipAddress > 0;
                isConnected = SSID == connectedSSID
                capabilities = result.capabilities
                isEncrypt = true
                encryption = ""
                level = result.level
                ip = if (isConnected) {
                    String.format(
                        "%d.%d.%d.%d",
                        ipAddress and 0xff,
                        ipAddress shr 8 and 0xff,
                        ipAddress shr 16 and 0xff,
                        ipAddress shr 24 and 0xff
                    )
                } else {
                    ""
                }
                capabilities.toUpperCase(Locale.CHINA).run {
                    if (contains("WPA2-PSK") && contains("WPA-PSK")) {
                        encryption = "WPA/WPA2"
                    } else if (contains("WPA-PSK")) {
                        encryption = "WPA"
                    } else if (contains("WPA2-PSK")) {
                        encryption = "WPA2"
                    } else {
                        isEncrypt = false
                    }
                }

                description = encryption
                for (configuration in configurations) {
                    if (configuration.SSID == SSID) {
                        isSaved = true
                        break
                    }
                }
                if (isSaved) {
                    description = "已保存"
                }
                if (isConnected) {
                    description = "已连接"
                }
            }
        }
    }
}