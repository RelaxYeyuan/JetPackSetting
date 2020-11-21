package com.neusoft.mc2.setting.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import com.neusoft.mc2.setting.utils.PairedDevicesManager
import com.neusoft.xui.adaptapi.bt.Bt
import java.util.*

class BtInformationDialog(
    context: Context,
    private val bluetoothEntity: BluetoothEntity,
    private val btCommand: Bt,
    private val listener: onRefreshListener
) :
    Dialog(context) {

    private val pairedDevicesManager by lazy { PairedDevicesManager.getInstance(context) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_bt_informaion)
        findViewById<TextView>(R.id.tvDialogBtName).text = bluetoothEntity.name

        val tvDialogBtConnect = findViewById<TextView>(R.id.tvDialogBtConnect)
        if (bluetoothEntity.isConnected) {
            tvDialogBtConnect.text = "DisConnect"
        } else {
            tvDialogBtConnect.text = "Connect"
        }
        tvDialogBtConnect.setOnClickListener {
            if (bluetoothEntity.isConnected) {
                btCommand.btSettings.reqBtDisconnectAll()
            } else {
                btCommand.btSettings.reqBtConnectHfpA2dp(bluetoothEntity.address)
            }
            dismiss()
        }
        findViewById<TextView>(R.id.tvDialogBtIgnore).setOnClickListener {
            listener.refreshList(pairedDevicesManager.removeBtPairDevice(bluetoothEntity.address))
            dismiss()
        }
        findViewById<TextView>(R.id.tvDialogBtCancel).setOnClickListener {
            dismiss()
        }
    }

    interface onRefreshListener {
        fun refreshList(removeBtPairDevice: LinkedList<BluetoothEntity>)
    }
}