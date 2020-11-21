package com.neusoft.mc2.setting.ui

import android.util.Log
import androidx.lifecycle.Observer
import com.neusoft.mc2.setting.BR
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseActivity
import com.neusoft.mc2.setting.base.DataBindingConfig
import com.neusoft.mc2.setting.data.config.SettingConstant
import com.neusoft.mc2.setting.ui.fragment.AudioFragment
import com.neusoft.mc2.setting.ui.fragment.ConnectFragment
import com.neusoft.mc2.setting.ui.fragment.DisplayFragment
import com.neusoft.mc2.setting.ui.fragment.SystemFragment
import com.neusoft.mc2.setting.ui.fragmentConnectView.BluetoothFragment
import com.neusoft.mc2.setting.ui.fragmentConnectView.WifiFragment
import com.neusoft.mc2.setting.ui.fragmentSystemView.AboutFragment
import com.neusoft.mc2.setting.ui.fragmentSystemView.RestoreSettingsFragment
import com.neusoft.mc2.setting.ui.fragmentSystemView.SoftwareUpdateFragment
import com.neusoft.mc2.setting.ui.fragmentSystemView.StorageFragment
import com.neusoft.mc2.setting.ui.state.*

private const val TAG = "MainActivity"

class MainActivity : BaseActivity() {

    private lateinit var mMainActivityViewModel: MainActivityViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    private val mConnectFragment: ConnectFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ConnectFragment(
            supportFragmentManager
        )
    }
    private val mAudioFragment: AudioFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AudioFragment(
            supportFragmentManager
        )
    }
    private val mDisplayFragment: DisplayFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        DisplayFragment(
            supportFragmentManager
        )
    }
    private val mSystemFragment: SystemFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SystemFragment(
            supportFragmentManager
        )
    }

    private val mBluetoothFragment: BluetoothFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BluetoothFragment() }
    private val mWifiFragment: WifiFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WifiFragment() }

    private val mStorageFragment: StorageFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StorageFragment() }
    private val mAboutFragment: AboutFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AboutFragment() }
    private val mSoftwareUpdateFragment: SoftwareUpdateFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SoftwareUpdateFragment() }
    private val mRestoreSettingsFragment: RestoreSettingsFragment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RestoreSettingsFragment() }


    private val clickProxy by lazy { ClickProxy() }

    override fun initViewModel() {
        mMainActivityViewModel = getAppViewModelProvider()[MainActivityViewModel::class.java]
        mSharedViewModel = getAppViewModelProvider()[SharedViewModel::class.java]
    }

    override fun initData() {
        mSharedViewModel.currentShowRightFragment.observe(this,
            Observer<String> {
                Log.d(TAG, "onPropertyChanged: ${mSharedViewModel.currentShowRightFragment.value}")
                reqSwitchRightFragment(mSharedViewModel.currentShowRightFragment.value)
            })

        mSharedViewModel.currentConnectFragment.observe(this,
            Observer<String> {
                Log.d(TAG, "onPropertyChanged: ${mSharedViewModel.currentConnectFragment.value}")
                switchConnectFragment(mSharedViewModel.currentConnectFragment.value)
            })

        mSharedViewModel.currentSystemFragment.observe(this,
            Observer<String> {
                Log.d(TAG, "onPropertyChanged: ${mSharedViewModel.currentSystemFragment.value}")
                switchSystemFragment(mSharedViewModel.currentSystemFragment.value)
            })

        clickProxy.tabConnect()
    }

    private fun reqSwitchRightFragment(fragmentFlag: String?) {
        when (fragmentFlag) {
            CONNECT_LIST_TAG -> switchRightFragment(
                mConnectFragment,
                CONNECT_LIST_TAG
            )
            AUDIO_LIST_TAG -> switchRightFragment(
                mAudioFragment,
                AUDIO_LIST_TAG
            )
            DISPLAY_LIST_TAG -> switchRightFragment(
                mDisplayFragment,
                DISPLAY_LIST_TAG
            )
            SYSTEM_LIST_TAG -> switchRightFragment(
                mSystemFragment,
                SYSTEM_LIST_TAG
            )
            else -> throw Exception("WHAT FXXK WAS HAPPEN")
        }
    }

    private fun switchConnectFragment(fragmentFlag: String?) {
        Log.d(TAG, "switchLeft: $fragmentFlag")
        when (fragmentFlag) {
            SettingConstant.BLUETOOTH_VIEW -> switchLeftFragment(
                mBluetoothFragment,
                SettingConstant.BLUETOOTH_VIEW
            )
            SettingConstant.WIFI_VIEW -> switchLeftFragment(
                mWifiFragment,
                SettingConstant.WIFI_VIEW
            )
            else -> {
                //加载背景画面
            }
        }
    }

    private fun switchSystemFragment(fragmentFlag: String?) {
        when (fragmentFlag) {
            SettingConstant.STORAGE_VIEW -> switchLeftFragment(
                mStorageFragment,
                SettingConstant.STORAGE_VIEW
            )
            SettingConstant.ABOUT_VIEW -> switchLeftFragment(
                mAboutFragment,
                SettingConstant.ABOUT_VIEW
            )
            SettingConstant.SOFTWARE_UPDATE_VIEW -> switchLeftFragment(
                mSoftwareUpdateFragment,
                SettingConstant.SOFTWARE_UPDATE_VIEW
            )
            SettingConstant.RESTORE_SETTINGS_VIEW -> switchLeftFragment(
                mRestoreSettingsFragment,
                SettingConstant.RESTORE_SETTINGS_VIEW
            )
            else -> {
                //加载背景画面
            }
        }
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(
            R.layout.activity_main,
            BR.vm, mMainActivityViewModel
        )
            .addBindingParam(BR.click, clickProxy)
    }

    inner class ClickProxy {
        fun tabConnect() {
            mSharedViewModel.currentShowRightFragment.value = CONNECT_LIST_TAG
        }

        fun tabAudio() {
            mSharedViewModel.currentShowRightFragment.value = AUDIO_LIST_TAG
        }

        fun tabDisplay() {
            mSharedViewModel.currentShowRightFragment.value = DISPLAY_LIST_TAG
        }

        fun tabSystem() {
            mSharedViewModel.currentShowRightFragment.value = SYSTEM_LIST_TAG
        }
    }
}