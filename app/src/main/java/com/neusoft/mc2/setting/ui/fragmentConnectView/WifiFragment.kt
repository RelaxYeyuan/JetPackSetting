package com.neusoft.mc2.setting.ui.fragmentConnectView

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.adapter.WifiPairedAdapter
import com.neusoft.mc2.setting.adapter.WifiSearchAdapter
import com.neusoft.mc2.setting.ui.MainActivity
import com.neusoft.mc2.setting.ui.custom.FullyLinearLayoutManager
import com.neusoft.mc2.setting.ui.dialog.EditInputDialogFragment
import com.neusoft.mc2.setting.utils.Utils
import com.neusoft.mc2.setting.wifi.BaseWifiManager.WIFI_STATE_CONNECTED
import com.neusoft.mc2.setting.wifi.BaseWifiManager.WIFI_STATE_CONNECT_FAIL
import com.neusoft.mc2.setting.wifi.IWifi
import com.neusoft.mc2.setting.wifi.NewWifiManager
import com.neusoft.mc2.setting.wifi.State
import java.lang.ref.WeakReference
import java.util.*

private val TAG = "WifiFragment"
private val SEATCH_WIFI = 0x01

class WifiFragment : Fragment() {

    private val pairedList = LinkedList<IWifi>()
    private val searchList = LinkedList<IWifi>()
    private val iWifiManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        NewWifiManager.create(requireContext())
    }
    private val wifiManager by lazy {
        requireContext().applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    private var switchBtEnable: SwitchCompat? = null
    private var llPairedWifiList: LinearLayout? = null
    private var llSearchWifiList: LinearLayout? = null
    private val wifiHandler by lazy { WifiHandler(this) }

    private var wifiPairListDataAdapter: WifiPairedAdapter? = null
    private var wifiDataSearchListAdapter: WifiSearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchBtEnable = view.findViewById(R.id.switchBtEnable)
        llPairedWifiList = view.findViewById(R.id.llPairedWifiList)
        llSearchWifiList = view.findViewById(R.id.llSearchWifiList)

        val recyclerViewPairedWifi = view.findViewById<RecyclerView>(R.id.recyclerViewPairedWifi)
        val recyclerViewSearchWifi = view.findViewById<RecyclerView>(R.id.recyclerViewSearchWifi)
        wifiPairListDataAdapter =
            WifiPairedAdapter(
                requireContext(),
                R.layout.item_paired_wifi,
                pairedList,
                object :
                    WifiPairedAdapter.WifiPairListener {
                    override fun onWifiPairClick(wifiEntry: IWifi) {
                        val mainActivity = activity as MainActivity
                        mainActivity.showWifiInformation(wifiEntry, iWifiManager)
                    }
                })
        wifiDataSearchListAdapter =
            WifiSearchAdapter(
                requireContext(),
                R.layout.item_connect_wifi,
                searchList,
                object :
                    WifiSearchAdapter.ItemConnectListener {
                    override fun onSearchConnectClick(wifi: IWifi) {
                        if (!wifi.isEncrypt) {
                            iWifiManager.connectOpenWifi(wifi)
                        } else {
                            pupPasswordDialog(wifi)
                        }
                    }
                })
        recyclerViewPairedWifi.adapter = wifiPairListDataAdapter
        recyclerViewSearchWifi.adapter = wifiDataSearchListAdapter

        recyclerViewPairedWifi.layoutManager = FullyLinearLayoutManager(requireContext())
        recyclerViewSearchWifi.layoutManager = FullyLinearLayoutManager(requireContext())

        initWifiListener()
    }

    private fun initWifiListener() {
        //wifi搜索列表
        iWifiManager.setOnWifiChangeListener { wifis ->
            //如果有正在连接，isConnected false
            //"semisky-5.8G" false 开始连接...
            //"semisky-2.4G" true 已连接
            for (wifi in wifis) {
                if (iWifiManager.isConnecting) {
                    wifi.isConnected = false
                }
                Log.d(
                    TAG,
                    "onWifiChanged:name ${wifi.name()} isConnected ${wifi.isConnected} isSaved ${wifi.isSaved}${wifi.description()}"
                )
            }
            iWifiManager.isConnecting = false

            wifiPairListDataAdapter?.setData(wifis)
            wifiDataSearchListAdapter?.setData(wifis, wifiManager.configuredNetworks)
        }
        //wifi连接状态
        iWifiManager.setOnWifiConnectListener { status ->
            Log.d(TAG, "onConnectChanged: %status")
            iWifiManager.scanWifi()
            when (status) {
                WIFI_STATE_CONNECTED -> {
                }
                WIFI_STATE_CONNECT_FAIL -> {
                }
            }
        }
        //wifi开关状态
        iWifiManager.setOnWifiStateChangeListener { state ->
            Log.d(TAG, "onStateChanged: $state")
            if (state === State.ENABLED) {
                switchBtEnable?.isChecked = true
                llPairedWifiList?.visibility = View.VISIBLE
                llSearchWifiList?.visibility = View.VISIBLE
            } else if (state === State.DISABLED) {
                switchBtEnable?.isChecked = false
                llPairedWifiList?.visibility = View.GONE
                llSearchWifiList?.visibility = View.GONE
            }
        }

        switchBtEnable?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                iWifiManager.openWifi()
            } else {
                iWifiManager.closeWifi()
            }
        }

        wifiHandler.sendEmptyMessage(SEATCH_WIFI)
    }

    private fun pupPasswordDialog(wifi: IWifi) {
        val dialog = EditInputDialogFragment.newInstance("")
        val listener: EditInputDialogFragment.ClickConfirmListener =
            object : EditInputDialogFragment.ClickConfirmListener {
                override fun clickConfirm() {}
                override fun clickCancel() {}
                override fun dialogDisMiss() {}
                override fun clicknameConfirm(password: String) {
                    if ("" != password) {
                        iWifiManager.connectEncryptWifi(wifi, password)
                    }
                    dialog.dismiss()
                }
            }
        if (!requireActivity().isFinishing && Utils.isForeground(
                requireContext(),
                MainActivity::class.java.name
            )
        ) {
            dialog.confirmListener = listener
            dialog.show(activity!!.supportFragmentManager.beginTransaction(), "input")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            wifiHandler.removeMessages(SEATCH_WIFI)
        } else {
            wifiHandler.sendEmptyMessage(SEATCH_WIFI)
        }
    }

    private class WifiHandler(wifiFragment: WifiFragment) : Handler() {
        private val reference: WeakReference<WifiFragment> = WeakReference(wifiFragment)
        override fun handleMessage(msg: Message) {
            val wifiFragment = reference.get()
            when (msg.what) {
                SEATCH_WIFI -> {
                    if (!wifiFragment?.isHidden!!) {
                        wifiFragment.iWifiManager?.scanWifi()
                        wifiFragment.wifiHandler.sendEmptyMessageDelayed(SEATCH_WIFI, 2000)
                    }
                }
            }
        }

    }
}