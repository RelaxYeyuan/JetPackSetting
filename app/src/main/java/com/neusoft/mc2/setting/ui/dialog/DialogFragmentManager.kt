package com.neusoft.mc2.setting.ui.dialog

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * @author by chenhongrui on 2019-09-27
 *
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
class DialogFragmentManager(private val fragmentManager: FragmentManager) {

    private val TAG = "DialogFragmentManager"
    private var fragmentDialog: MessageDialogFragment? = null

    fun showDialog(
        message: String,
        title: String,
        layoutStyle: MessageDialogFragment.LayoutStyle,
        clickConfirmListener: MessageDialogFragment.ClickConfirmListener?,
        isCancelable: Boolean,
        isDismiss: Boolean,
        timeout: Int
    ) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        val fragmentTransaction = removeDialogFragment() ?: return

        // Create and show the dialog.
        fragmentDialog = MessageDialogFragment.newInstance(message, title)
        //外边不消失，返回键也不消失
        fragmentDialog?.isCancelable = isCancelable
        if (isDismiss) {
            fragmentDialog?.showTime = timeout
        } else {
            fragmentDialog?.showTime = 0
        }
        fragmentDialog?.confirmListener = clickConfirmListener
        fragmentDialog?.layoutStyle = layoutStyle
        fragmentDialog?.show(fragmentTransaction, "dialogFormFragment")
    }

    private fun removeDialogFragment(): FragmentTransaction? {
        return run {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialogFormFragment")
            if (prev != null) {
                fragmentTransaction.remove(prev)
                Log.e(TAG, "removeDialogFragment: remove dialog")
            } else {
                Log.d(TAG, "removeDialogFragment: 没有dialog")
            }
            fragmentTransaction
        }
    }

    fun dismissDialog() {
        try {
            val fragmentTransaction = removeDialogFragment()
            fragmentTransaction?.commitAllowingStateLoss()
        } catch (e: Exception) {
            Log.e(TAG, "dismissDialog: " + e.localizedMessage)
        }
    }

    val isShowDialog: Boolean
        get() {
            val prev = fragmentManager.findFragmentByTag("dialogFormFragment")
            return prev != null
        }

    fun cancelTimer() {
        fragmentDialog?.cancelTimer()
    }
}