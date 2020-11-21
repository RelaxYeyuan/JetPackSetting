package com.neusoft.mc2.setting.utils

import android.content.Context
import android.util.Log
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import java.util.*

/**
 * @author by chenhongrui on 2019-05-24
 *
 * 内容摘要: 匹配历史设备管理
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
class PairedDevicesManager private constructor(val context: Context) {

    private  val TAG = "PairedDevicesManager"

    private val sharePreferenceUtils by lazy { SharePreferenceUtils.getInstance(context) }

    companion object : SingletonHolder<PairedDevicesManager, Context>(::PairedDevicesManager)

    /**
     * 保存设备信息
     */
    fun saveDeviceInformation(address: String?, deviceName: String?): LinkedList<BluetoothEntity> {
        val pairedDevicesData: LinkedList<BluetoothEntity> = sharePreferenceUtils.getPairedDevicesData()

        //复原
        for (data in pairedDevicesData) {
            data.isConnected = false
        }

        //删除历史
        //如果是旧数据，要保存联系人和通话记录的下载状态
        for ((i, _) in pairedDevicesData.withIndex()) {
            if (pairedDevicesData[i].address.equals(address)) {
                pairedDevicesData.removeAt(i)
                break
            }
        }

        val newDevice = BluetoothEntity(
            address, deviceName,
            isConnecting = false, isConnected = true
        )
        Log.d(TAG, "saveDeviceInformation:$deviceName")
        //新设备排第一
        pairedDevicesData.addFirst(newDevice)
        return pairedDevicesDataSort(pairedDevicesData)
    }

    private fun pairedDevicesDataSort(pairedDevicesData: LinkedList<BluetoothEntity>): LinkedList<BluetoothEntity> {
//        for (devicesListEntity in pairedDevicesData) {
//            Log.d(TAG, "排序前: " + devicesListEntity.name)
//        }
//        Log.d(TAG, "----------------------: ")
//        if (pairedDevicesData.size > BtConstant.MAX_PAIR_DEVICES) {
//            pairedDevicesData.removeLast()
//        }
//        Log.d(TAG, "----------------------: ")
//        for (devicesListEntity in pairedDevicesData) {
//            Log.d(TAG, "排序后: " + devicesListEntity.name)
//        }
        sharePreferenceUtils.putPairedDevicesData(pairedDevicesData)
        return pairedDevicesData
    }

    fun removeBtPairDevice(address: String?): LinkedList<BluetoothEntity> {
        val newPair = pairedDevicesData
        for ((index, device) in newPair.withIndex()) {
            if (device.address == address) {
                newPair.removeAt(index)
                break
            }
        }
        for (device in newPair) {
            device.isConnected = address == device.address
        }
        sharePreferenceUtils.putPairedDevicesData(newPair)
        Log.d(TAG, "removeBtPairDevice:$newPair")
        return newPair
    }

    private val pairedDevicesData: LinkedList<BluetoothEntity>
        get() = sharePreferenceUtils.getPairedDevicesData()

}