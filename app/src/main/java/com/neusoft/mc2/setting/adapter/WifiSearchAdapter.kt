package com.neusoft.mc2.setting.adapter

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.view.View
import android.widget.ImageView
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseRecyclerAdapter
import com.neusoft.mc2.setting.base.BaseViewHolder
import com.neusoft.mc2.setting.utils.Utils
import com.neusoft.mc2.setting.wifi.Wifi
import java.util.*

class WifiSearchAdapter(
    context: Context, id: Int, private val dataList: LinkedList<Wifi>,
    private val listener: ItemConnectListener
) :
    BaseRecyclerAdapter<Wifi>(context, id, dataList) {

    private val listTemp = ArrayList<Wifi>()

    fun setData(
        wifiList: List<Wifi>,
        configuredNetworks: List<WifiConfiguration>
    ) {
        listTemp.clear()
        dataList.clear()
        dataList.addAll(wifiList)
        for (i in wifiList.indices) {
            for (j in configuredNetworks.indices) {
                if (wifiList[i].SSID == configuredNetworks[j].SSID) {
                    listTemp.add(wifiList[i])
                }
            }
        }
        dataList.removeAll(listTemp)
        notifyDataSetChanged()
    }

    override fun bindData(holder: BaseViewHolder, data: Wifi, position: Int) {
        val scanSSID = data.SSID
        val capabilities = data.capabilities
        val level = WifiManager.calculateSignalLevel(data.level, 5)
        holder.setText(R.id.tvItemWifiStatus, data.description())
        holder.setText(R.id.tvItemWifiName, Utils.checkWifiNameQuotes(scanSSID))

        val ivWifiSignal: ImageView = holder.getView(R.id.ivWifiSignal)
//        if (capabilities.contains("WEP") || capabilities.contains("PSK") || capabilities.contains("EAP")) {
//            viewHolder.ivWifiLock.setVisibility(View.VISIBLE)
//        } else {
//            viewHolder.ivWifiLock.setVisibility(View.GONE)
//        }

        when (level) {
            5 -> ivWifiSignal.setImageResource(R.drawable.set_wifi_icon_4)
            4 -> ivWifiSignal.setImageResource(R.drawable.set_wifi_icon_3)
            3 -> ivWifiSignal.setImageResource(R.drawable.set_wifi_icon_2)
            else -> ivWifiSignal.setImageResource(R.drawable.set_wifi_icon_1)
        }

        holder.setOnItemClickListener(View.OnClickListener {
            listener.onSearchConnectClick(data)
        })
    }

    interface ItemConnectListener {
        //当点击了连接
        fun onSearchConnectClick(wifi: Wifi)
    }
}
