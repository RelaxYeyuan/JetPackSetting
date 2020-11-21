package com.neusoft.mc2.setting.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.wifi.IWifi
import com.neusoft.mc2.setting.wifi.IWifiManager

class WifiDialogFragment(context: Context, val wifi: IWifi, private val wifiManager: IWifiManager) :
    Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wifi_informaion)
        findViewById<TextView>(R.id.tvDialogWifiName).text = wifi.SSID()

        val tvDialogWifiConnect = findViewById<TextView>(R.id.tvDialogWifiConnect)
        if (wifi.isConnected) {
            tvDialogWifiConnect.text = "DisConnect"
        } else {
            tvDialogWifiConnect.text = "Connect"
        }
        tvDialogWifiConnect.setOnClickListener {
            if (wifi.isConnected) {
                wifiManager.disConnectWifi()
            } else {
                wifiManager.connectSavedWifi(wifi)
            }
            dismiss()
        }
        findViewById<TextView>(R.id.tvDialogWifiIgnore).setOnClickListener {
            wifiManager.removeWifi(wifi.SSID())
            dismiss()
        }
        findViewById<TextView>(R.id.tvDialogWifiCancel).setOnClickListener {
            dismiss()
        }
    }
}