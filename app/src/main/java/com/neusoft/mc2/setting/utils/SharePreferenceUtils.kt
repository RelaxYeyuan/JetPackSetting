package com.neusoft.mc2.setting.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import java.util.*

/**
 *
 */
class SharePreferenceUtils private constructor(private val mContext: Context) {
    private val version_name = "wifiAp"
    private val BT_SAVE_DATA_NAME = "BT_Save_Data_SharedPreferences"

    private val sharedPreferences by lazy {
        mContext.getSharedPreferences(
            version_name,
            Context.MODE_PRIVATE
        )
    }
    private val editor by lazy { sharedPreferences.edit() }

    companion object : SingletonHolder<SharePreferenceUtils, Context>(::SharePreferenceUtils)

    fun saveIntData(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit() //提交修改
    }

    fun getIntData(key: String?, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    fun saveStringData(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit() //提交修改
    }

    fun getStringData(key: String?, defValue: String?): String {
        return sharedPreferences.getString(key, defValue)!!
    }

    fun saveFloatData(key: String?, value: Float) {
        editor.putFloat(key, value)
        editor.commit() //提交修改
    }

    fun getFloatData(key: String?, defValue: Float): Float {
        return sharedPreferences.getFloat(key, defValue)
    }

    private fun getSaveDataSP(): SharedPreferences {
        return mContext.getSharedPreferences(
            mContext.packageName + BT_SAVE_DATA_NAME,
            Context.MODE_PRIVATE
        )
    }

    /**
     * 存储历史配对设备
     *
     * @param context
     * @param data
     */
    fun putPairedDevicesData(data: LinkedList<BluetoothEntity>) {
        val sp = getSaveDataSP()
        val editor = sp.edit()
        editor.putInt("CollectionSize", data.size)
        for (i in data.indices) {
            editor.putString("item_address$i", data[i].address)
            editor.putString("item_name$i", data[i].name)
            editor.putBoolean("item_connectting$i", data[i].isConnecting)
            editor.putBoolean("item_connect$i", data[i].isConnected)
        }
        //提交
        editor.apply()
    }

    /**
     * 获取历史配对设备
     *
     * @param context
     */
    fun getPairedDevicesData(): LinkedList<BluetoothEntity> {
        val sp = getSaveDataSP()
        val linkedList: LinkedList<BluetoothEntity> = LinkedList()
        val listSize = sp.getInt("CollectionSize", 0)
        for (i in 0 until listSize) {
            val address = sp.getString("item_address$i", "")
            val name = sp.getString("item_name$i", "")
            val isConnecting = sp.getBoolean("item_connectting$i", false)
            val isConnect = sp.getBoolean("item_connect$i", false)
            linkedList.add(
                BluetoothEntity(
                    address!!,
                    name!!,
                    isConnecting,
                    isConnect
                )
            )
        }
        return linkedList
    }
}