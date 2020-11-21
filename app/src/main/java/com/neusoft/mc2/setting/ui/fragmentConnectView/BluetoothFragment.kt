package com.neusoft.mc2.setting.ui.fragmentConnectView

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.adapter.BtPairedAdapter
import com.neusoft.mc2.setting.adapter.BtSearchAdapter
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import com.neusoft.mc2.setting.ui.MainActivity
import com.neusoft.mc2.setting.ui.custom.FullyLinearLayoutManager
import com.neusoft.mc2.setting.ui.custom.ProgressLoadingImageView
import com.neusoft.mc2.setting.ui.dialog.BtInformationDialog
import com.neusoft.mc2.setting.utils.SharePreferenceUtils
import com.neusoft.xui.adaptapi.bt.Bt
import com.neusoft.xui.adaptapi.bt.common.BtDef.*
import com.neusoft.xui.adaptapi.bt.common.BtDevice
import com.neusoft.xui.adaptapi.bt.common.IPairResponse
import com.neusoft.xui.adaptapi.bt.settings.IBtSettingsCallback
import java.lang.ref.WeakReference
import java.util.*

private const val SHOW_BT_ENABLE_VIEW = 0x01
private const val SHOW_BT_DISENABLE_VIEW = 0x02
private const val SHOW_BT_PAIRED_DEVICES = 0x03

class BluetoothFragment : Fragment(), View.OnClickListener {

    private val TAG = "BluetoothFragment"
    private val sharePreferenceUtils by lazy { SharePreferenceUtils.getInstance(requireContext()) }

    private val btPairedList = LinkedList<BluetoothEntity>()
    private val btSearchList = arrayListOf<BluetoothEntity>()
    private val btCommand by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Bt.create(requireContext()) }
    private var switchBtEnable: SwitchCompat? = null
    private var llSearchBtList: LinearLayout? = null
    private var llPairedBtList: LinearLayout? = null
    private var recyclerViewPairedBt: RecyclerView? = null
    private var recyclerViewSearchBt: RecyclerView? = null
    private var btSearchAdapter: BtSearchAdapter? = null
    private var btPairedAdapter: BtPairedAdapter? = null
    private var ivRefreshBt: ProgressLoadingImageView? = null
    private val btHandler: BtHandler by lazy { BtHandler(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewSearchBt = view.findViewById(R.id.recyclerViewSearchBt)
        recyclerViewPairedBt = view.findViewById(R.id.recyclerViewPairedBt)
        switchBtEnable = view.findViewById(R.id.switchBtEnable)
        llSearchBtList = view.findViewById(R.id.llSearchBtList)
        llPairedBtList = view.findViewById(R.id.llPairedBtList)
        ivRefreshBt = view.findViewById(R.id.ivRefreshBt)

        ivRefreshBt?.setOnClickListener(this)
        ivRefreshBt?.performClick()

        initCallback()
        initView()
    }

    private fun initView() {
        btSearchAdapter = BtSearchAdapter(
            requireContext(),
            R.layout.item_connect_bt,
            btSearchList,
            object : BtSearchAdapter.IItemBtClickListener {
                override fun itemClickListener(bluetoothEntity: BluetoothEntity) {
                    btCommand.btSettings.reqBtConnectHfpA2dp(bluetoothEntity.address)
                }
            })
        recyclerViewSearchBt?.adapter = btSearchAdapter
        recyclerViewSearchBt?.layoutManager = FullyLinearLayoutManager(requireContext())

        btPairedAdapter = BtPairedAdapter(requireContext(), R.layout.item_connect_bt, btPairedList,
            object : BtPairedAdapter.IItemClickListener {
                override fun itemClick(bluetoothEntity: BluetoothEntity) {
//                    if (!bluetoothEntity.isConnected) {
//                        btCommand.btSettings.reqBtConnectHfpA2dp(bluetoothEntity.address)
//                    }

                    val mainActivity = activity as MainActivity
                    mainActivity.showBtInformation(
                        bluetoothEntity,
                        btCommand,
                        object : BtInformationDialog.onRefreshListener {
                            override fun refreshList(removeBtPairDevice: LinkedList<BluetoothEntity>) {
                                btPairedList.clear()
                                btPairedList.addAll(removeBtPairDevice)
                                btPairedAdapter?.notifyDataSetChanged()
                            }
                        })
                }

                override fun itemInformationClick(bluetoothEntity: BluetoothEntity) {

                }
            })
        recyclerViewPairedBt?.layoutManager = FullyLinearLayoutManager(requireContext())
        recyclerViewPairedBt?.adapter = btPairedAdapter

        switchBtEnable?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d(TAG, "initView: setBtEnable true")
                btCommand.btSettings.setBtEnable(true)
            } else {
                Log.d(TAG, "initView: setBtEnable false")
                btCommand.btSettings.setBtEnable(false)
            }
        }
        val btEnabled = btCommand.btSettings.isBtEnabled

        Log.d(TAG, "onViewCreated: $btEnabled")

        if (btEnabled) {
            btHandler.sendEmptyMessage(SHOW_BT_ENABLE_VIEW)
            btHandler.sendEmptyMessage(SHOW_BT_PAIRED_DEVICES)
        } else {
            btHandler.sendEmptyMessage(SHOW_BT_DISENABLE_VIEW)
        }
    }

    private fun showEnableView() {
        switchBtEnable?.isChecked = true
        llSearchBtList?.visibility = View.VISIBLE
        llPairedBtList?.visibility = View.VISIBLE
    }

    private fun showDisEnableView() {
        switchBtEnable?.isChecked = false
        llSearchBtList?.visibility = View.GONE
        llPairedBtList?.visibility = View.GONE
    }

    private fun initCallback() {
        btCommand.btSettings.registerBtCallback(object : IBtSettingsCallback {
            override fun onAdapterStateChanged(prevState: Int, newState: Int) {
                Log.d(TAG, "onAdapterStateChanged: $newState")
                when (newState) {
                    BT_STATE_OFF -> btHandler.sendEmptyMessage(SHOW_BT_DISENABLE_VIEW)
                    BT_STATE_ON -> {
                        btCommand.btSettings.startBtDiscovery()
                        btHandler.sendEmptyMessage(SHOW_BT_ENABLE_VIEW)
                        btHandler.sendEmptyMessage(SHOW_BT_PAIRED_DEVICES)
                    }
                    BT_STATE_TURNING_ON -> btHandler.sendEmptyMessage(SHOW_BT_ENABLE_VIEW)
                    BT_STATE_TURNING_OFF -> btHandler.sendEmptyMessage(SHOW_BT_DISENABLE_VIEW)
                }
            }

            override fun onPairedDevicesChanged(pairedList: MutableList<BtDevice>) {

            }

            override fun onDeviceUuidsUpdated(device: BtDevice?) {
            }

            override fun onPairingRequest(code: String?, response: IPairResponse?) {
            }

            override fun onBtRoleModeChanged(mode: Int) {
            }

            override fun onDeviceFoundChanged(scanState: Int, btDevice: BtDevice?) {
                Log.d(
                    TAG,
                    "onDeviceFoundChanged:scanState $scanState name ${btDevice?.name} address ${btDevice?.address}"
                )
                when (scanState) {
                    BT_SCAN_START -> {
                        btSearchList.clear()
                        btSearchAdapter?.notifyDataSetChanged()
                    }
                    BT_SCAN_FINISH -> {
                        ivRefreshBt?.stopAnimation()
                        btSearchAdapter?.notifyDataSetChanged()
                    }
                    BT_SCAN_SCANNING -> {

                    }
                }
                if (!btDevice?.address.isNullOrEmpty()) {
                    btSearchList.add(
                        BluetoothEntity(
                            btDevice?.name,
                            btDevice?.address,
                            isConnecting = false,
                            isConnected = false
                        )
                    )
                }
            }

            override fun onConnectStateChanged(device: BtDevice?, prevState: Int, newState: Int) {
                Log.d(TAG, "onConnectStateChanged: $newState")
                btPairedAdapter?.refreshDeviceStatus(device, prevState, newState)
            }

            override fun onAdapterDiscoverableModeChanged(prevState: Int, newState: Int) {
            }

            override fun onLocalAdapterNameChanged(name: String?) {
            }

            override fun onDeviceBondStateChanged(
                device: BtDevice?,
                prevState: Int,
                newState: Int
            ) {
            }

            override fun onBluetoothServiceReady() {
                Log.d(TAG, "onBluetoothServiceReady: ")
            }

            override fun onDeviceOutOfRange(String: String?) {
            }
        })
    }

    private class BtHandler(fragment: BluetoothFragment) : Handler() {

        private val TAG = "BluetoothFragment"

        private val reference: WeakReference<BluetoothFragment> = WeakReference(fragment)
        override fun handleMessage(msg: Message) {
            val bluetoothFragment = reference.get()
            when (msg.what) {
                SHOW_BT_ENABLE_VIEW -> bluetoothFragment?.showEnableView()
                SHOW_BT_DISENABLE_VIEW -> bluetoothFragment?.showDisEnableView()
                SHOW_BT_PAIRED_DEVICES -> {
                    val pairedDevicesData =
                        bluetoothFragment?.sharePreferenceUtils?.getPairedDevicesData()
                    Log.d(TAG, "handleMessage: ${pairedDevicesData?.size}")
                    bluetoothFragment?.btPairedList?.clear()
                    bluetoothFragment?.btPairedList?.addAll(pairedDevicesData!!)
                    bluetoothFragment?.btPairedAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivRefreshBt -> {
                btCommand.btSettings.startBtDiscovery()
                ivRefreshBt?.showAnimation()
            }
        }
    }
}