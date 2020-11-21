package com.neusoft.mc2.setting.adapter

import android.content.Context
import android.util.Log
import android.view.View
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseRecyclerAdapter
import com.neusoft.mc2.setting.base.BaseViewHolder
import com.neusoft.mc2.setting.utils.Utils
import com.neusoft.mc2.setting.wifi.Wifi
import java.util.*

private const val TAG = "WifiPairedAdapter"

class WifiPairedAdapter(
    context: Context,
    id: Int,
    private val dataList: LinkedList<Wifi>,
    val listener: WifiPairListener
) : BaseRecyclerAdapter<Wifi>(context, id, dataList) {

    fun setData(wifiList: List<Wifi>) {
        if (wifiList.isNotEmpty()) {
            dataList.clear()
            //筛选出已保存的设备
            for (wifi in wifiList) {
                if (wifi.isSaved) {
                    wifi.isConnecting = wifi.description() == "开始连接..."
                    wifi.isClickSecond = false
                    dataList.add(wifi)
                    Log.d(
                        TAG, "setData: ${wifi.SSID} ${wifi.isConnected}  " +
                                "${wifi.description()} ${wifi.isConnecting}"
                    )
                }
            }
        }
//        移动已保存的到首位
        for (i in dataList.indices) {
            val wifi: Wifi = dataList[i]
            if (wifi.isConnected) {
                dataList.removeAt(i)
                dataList.addFirst(wifi)
                break
            }
        }
        notifyDataSetChanged()
    }

    override fun bindData(holder: BaseViewHolder, data: Wifi, position: Int) {
        holder.setText(R.id.tvItemWifiName, Utils.checkWifiNameQuotes(data.SSID))
        holder.setText(R.id.tvItemWifiStatus, data.description())
        if (data.isConnected) {
            holder.setViewVisibility(R.id.ivWifiOk, View.VISIBLE)
        } else {
            holder.setViewVisibility(R.id.ivWifiOk, View.INVISIBLE)
        }

        holder.setOnItemClickListener(R.id.llWifiBg,
            View.OnClickListener { listener.onWifiPairClick(data) })
    }

    interface WifiPairListener {
        //当点击了连接
        fun onWifiPairClick(wifiEntry: Wifi)
    }
}
