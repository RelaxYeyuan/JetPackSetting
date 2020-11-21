package com.neusoft.mc2.setting.ui.fragment

import androidx.fragment.app.FragmentManager
import com.neusoft.mc2.setting.BR
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseFragment
import com.neusoft.mc2.setting.base.DataBindingConfig
import com.neusoft.mc2.setting.data.config.SettingConstant
import com.neusoft.mc2.setting.ui.state.ConnectViewModel
import com.neusoft.mc2.setting.ui.state.SharedViewModel

private const val TAG = "ConnectFragment"

class ConnectFragment(supportFragmentManager: FragmentManager) :
    BaseFragment(supportFragmentManager) {

    private val clickProxy by lazy { ClickProxy() }

    private lateinit var viewModel: ConnectViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    override fun fragmentHide() {

    }

    override fun fragmentShow() {
    }

    override fun showDataAnimation() {
    }

    override fun initViewModel() {
        viewModel = getAppViewModelProvider()[ConnectViewModel::class.java]
        mSharedViewModel = getAppViewModelProvider()[SharedViewModel::class.java]

    }

    override fun getDataBindingConfig(): DataBindingConfig =
        DataBindingConfig(R.layout.connect_fragment, BR.vm, viewModel)
            .addBindingParam(BR.click, clickProxy)

    inner class ClickProxy {

        fun bluetoothClick() {
            mSharedViewModel.currentConnectFragment.value = SettingConstant.BLUETOOTH_VIEW
        }

        fun wifiClick() {
            mSharedViewModel.currentConnectFragment.value = SettingConstant.WIFI_VIEW
        }

        fun hotSpotClick() {

        }

        fun mobileClick() {

        }
    }

}