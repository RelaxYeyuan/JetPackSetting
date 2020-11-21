package com.neusoft.mc2.setting.ui.fragment

import androidx.fragment.app.FragmentManager
import com.neusoft.mc2.setting.BR
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseFragment
import com.neusoft.mc2.setting.base.DataBindingConfig
import com.neusoft.mc2.setting.data.config.SettingConstant
import com.neusoft.mc2.setting.ui.state.*

class SystemFragment(supportFragmentManager: FragmentManager) : BaseFragment(supportFragmentManager) {
    //二级画面
    val NULL = "NULL"

    private val clickProxy by lazy { ClickProxy() }
    private lateinit var viewModel: SystemViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    override fun fragmentHide() {

    }

    override fun fragmentShow() {
    }

    override fun showDataAnimation() {
    }

    override fun initViewModel() {
        viewModel = getAppViewModelProvider()[SystemViewModel::class.java]
        mSharedViewModel = getAppViewModelProvider()[SharedViewModel::class.java]
    }

    override fun getDataBindingConfig(): DataBindingConfig =
        DataBindingConfig(R.layout.system_fragment, BR.vm, viewModel)
            .addBindingParam(BR.click, clickProxy)

    inner class ClickProxy {
        fun itemTimeMode() {
            mSharedViewModel.currentSystemFragment.value = null
        }

        fun itemStorage() {
            mSharedViewModel.currentSystemFragment.value = SettingConstant.STORAGE_VIEW
        }

        fun itemAbout() {
            mSharedViewModel.currentSystemFragment.value = SettingConstant.ABOUT_VIEW
        }

        fun itemSoftwareUpdate() {
            mSharedViewModel.currentSystemFragment.value = SettingConstant.SOFTWARE_UPDATE_VIEW
        }

        fun itemRestoreSettings() {
            mSharedViewModel.currentSystemFragment.value = SettingConstant.RESTORE_SETTINGS_VIEW
        }
    }
}