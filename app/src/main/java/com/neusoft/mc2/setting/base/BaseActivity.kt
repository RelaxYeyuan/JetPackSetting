package com.neusoft.mc2.setting.base

import android.content.pm.ApplicationInfo
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import com.neusoft.mc2.setting.ui.MainActivity
import com.neusoft.mc2.setting.ui.dialog.BtInformationDialog
import com.neusoft.mc2.setting.ui.dialog.DialogFragmentManager
import com.neusoft.mc2.setting.ui.dialog.MessageDialogFragment
import com.neusoft.mc2.setting.ui.dialog.WifiDialogFragment
import com.neusoft.mc2.setting.utils.Utils
import com.neusoft.mc2.setting.wifi.IWifi
import com.neusoft.mc2.setting.wifi.IWifiManager
import com.neusoft.xui.adaptapi.bt.Bt

abstract class BaseActivity : DataBindingActivity() {

    private val TAG = "BaseActivity"

    protected var mCurrentRightFragment: BaseFragment? = null
    protected var mCurrentLeftFragment: Fragment? = null
    private val mFragmentManager by lazy { supportFragmentManager }
    private val mDialogFragmentManager by lazy { DialogFragmentManager(supportFragmentManager) }

    protected fun switchRightFragment(
        targetFragment: BaseFragment,
        tag: String
    ) {
        if (targetFragment == mCurrentRightFragment) {
            return
        }

        if (mFragmentManager.isDestroyed) {
            return
        }

        val transaction: FragmentTransaction = mFragmentManager.beginTransaction()

        //&& null == fragmentManager.findFragmentByTag(tag)
        if (!targetFragment.isAdded && !mFragmentManager.fragments.contains(targetFragment)
        ) {
            mCurrentRightFragment?.let {
                transaction.hide(it)
            }
            transaction
                .add(R.id.rightFragment, targetFragment, tag)
                .commitAllowingStateLoss()
            //FragmentManager has been destroyed
            mFragmentManager.executePendingTransactions()
        } else {
            mCurrentRightFragment?.let {
                transaction.hide(it)
            }
            transaction
                .show(targetFragment)
                .commitAllowingStateLoss()
            mFragmentManager.executePendingTransactions()
        }
        targetFragment.showDataAnimation()
        mCurrentRightFragment = targetFragment
    }

    protected fun switchLeftFragment(
        targetFragment: Fragment,
        tag: String
    ) {

        if (targetFragment == mCurrentLeftFragment) {
            return
        }

        if (mFragmentManager.isDestroyed) {
            return
        }

        val transaction: FragmentTransaction = mFragmentManager.beginTransaction()

        //&& null == fragmentManager.findFragmentByTag(tag)
        if (!targetFragment.isAdded && !mFragmentManager.fragments.contains(targetFragment)
        ) {
            mCurrentLeftFragment?.let {
                transaction.hide(it)
            }
            transaction
                .add(R.id.leftFragment, targetFragment, tag)
                .commitAllowingStateLoss()
            //FragmentManager has been destroyed
            mFragmentManager.executePendingTransactions()

        } else {
            mCurrentLeftFragment?.let {
                transaction.hide(it)
            }
            transaction
                .show(targetFragment)
                .commitAllowingStateLoss()
            mFragmentManager.executePendingTransactions()
        }
        mCurrentLeftFragment = targetFragment
        Log.d(TAG, "switchLeftFragment: $mCurrentLeftFragment")
    }

    open fun showDialog(
        message: String, title: String, layoutStyle: MessageDialogFragment.LayoutStyle,
        clickConfirmListener: MessageDialogFragment.ClickConfirmListener?,
        isCancelable: Boolean, isDismiss: Boolean, timeout: Int
    ) {
        if (!isFinishing && Utils.isForeground(this, MainActivity::class.java.name)) {
            mDialogFragmentManager.showDialog(
                message, title, layoutStyle, clickConfirmListener,
                isCancelable, isDismiss, timeout
            )
        }
    }

    open fun showWifiInformation(wifi: IWifi, wifiManager: IWifiManager) {
        if (!isFinishing && Utils.isForeground(this, MainActivity::class.java.name)) {
            WifiDialogFragment(this, wifi, wifiManager).run {
                show()
            }
        }
    }

    open fun showBtInformation(
        bluetoothEntity: BluetoothEntity,
        bt: Bt,
        listener: BtInformationDialog.onRefreshListener
    ) {
        if (!isFinishing && Utils.isForeground(this, MainActivity::class.java.name)) {
            BtInformationDialog(this, bluetoothEntity, bt, listener).run {
                show()
            }
        }
    }

    open fun dismissDialog() {
        mDialogFragmentManager.dismissDialog()
    }

    open fun isShowDialog(): Boolean {
        return mDialogFragmentManager.isShowDialog
    }

    fun isDebug(): Boolean {
        return applicationContext.applicationInfo != null &&
                applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    fun showLongToast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    fun showShortToast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(stringRes: Int) {
        showLongToast(applicationContext.getString(stringRes))
    }

    fun showShortToast(stringRes: Int) {
        showShortToast(applicationContext.getString(stringRes))
    }

}