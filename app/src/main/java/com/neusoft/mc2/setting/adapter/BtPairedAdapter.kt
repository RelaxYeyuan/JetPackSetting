package com.neusoft.mc2.setting.adapter

import android.content.Context
import android.util.Log
import android.view.View
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseRecyclerAdapter
import com.neusoft.mc2.setting.base.BaseViewHolder
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import com.neusoft.mc2.setting.utils.PairedDevicesManager
import com.neusoft.mc2.setting.utils.SharePreferenceUtils
import com.neusoft.xui.adaptapi.bt.common.BtDef
import com.neusoft.xui.adaptapi.bt.common.BtDevice
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "BtPairedAdapter"

class BtPairedAdapter(
    context: Context,
    id: Int,
    private val dataList: LinkedList<BluetoothEntity>,
    private val listener: IItemClickListener
) : BaseRecyclerAdapter<BluetoothEntity>(context, id, dataList) {

    private val sharePreferenceUtils by lazy { SharePreferenceUtils.getInstance(context) }
    private val pairedDevicesManager by lazy { PairedDevicesManager.getInstance(context) }

    override fun bindData(holder: BaseViewHolder, data: BluetoothEntity, position: Int) {
        Log.d(TAG, "bindData: ${data.name} ${data.isConnected}")

        if (data.name.isNullOrEmpty()) {
            holder.setText(R.id.tvItemBtName, data.address)
        } else {
            holder.setText(R.id.tvItemBtName, data.name)
        }

        if (data.isConnected) {
            holder.setViewVisibility(R.id.ivBtOk, View.VISIBLE)
        } else {
            holder.setViewVisibility(R.id.ivBtOk, View.INVISIBLE)
        }

        holder.setOnItemClickListener(View.OnClickListener { listener.itemClick(data) })
        holder.setOnItemClickListener(R.id.ivBtInformation, View.OnClickListener {
            listener.itemInformationClick(data)
        })
    }

    fun refreshDeviceStatus(device: BtDevice?, prevState: Int, newState: Int) {
        when (newState) {
            BtDef.STATE_READY -> {
            }
            BtDef.STATE_CONNECTING -> {
            }
            BtDef.STATE_DISCONNECTING -> {
                Log.d(TAG, "refreshDeviceStatus: 已断开 ")
                val pairedDevicesData = sharePreferenceUtils.getPairedDevicesData()
                dataList.clear()
                dataList.addAll(pairedDevicesData)
                for (data in dataList) {
                    data.isConnected = false
                }
                sharePreferenceUtils.putPairedDevicesData(dataList)
                notifyDataSetChanged()
            }
            BtDef.STATE_CONNECTED -> {
                Log.d(TAG, "refreshDeviceStatus: 已连接 ${device?.address} ")
                val saveDeviceInformation =
                    pairedDevicesManager.saveDeviceInformation(device?.address, device?.name)
                dataList.clear()
                dataList.addAll(saveDeviceInformation)
                notifyDataSetChanged()
            }
        }
    }

    interface IItemClickListener {
        fun itemClick(bluetoothEntity: BluetoothEntity)

        fun itemInformationClick(bluetoothEntity: BluetoothEntity)
    }
}