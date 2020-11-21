package com.neusoft.mc2.setting.base

import android.content.pm.ApplicationInfo
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.neusoft.mc2.setting.R

private const val TAG = "BaseFragment"

abstract class BaseFragment(private val mFragmentManager: FragmentManager) : DataBindingFragment() {

    protected abstract fun fragmentHide()

    protected abstract fun fragmentShow()

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            fragmentHide()
        } else {
            fragmentShow()
        }
    }

    fun isDebug(): Boolean {
        return mActivity.applicationContext.applicationInfo != null &&
                mActivity.applicationContext
                    .applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    protected fun showLongToast(text: String?) {
        Toast.makeText(mActivity.applicationContext, text, Toast.LENGTH_LONG).show()
    }

    protected fun showShortToast(text: String?) {
        Toast.makeText(mActivity.applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    protected fun showLongToast(stringRes: Int) {
        showLongToast(mActivity.applicationContext.getString(stringRes))
    }

    protected fun showShortToast(stringRes: Int) {
        showShortToast(mActivity.applicationContext.getString(stringRes))
    }

    /**
     * 列表的动画展示
     */
    abstract fun showDataAnimation()


    protected var mCurrentFragment: Fragment? = null

    protected fun switchRightFragment(
        targetFragment: Fragment,
        tag: String
    ) {
        Log.d(TAG, "switchRightFragment: $targetFragment")
        if (targetFragment == mCurrentFragment) {
            return
        }

        if (mFragmentManager.isDestroyed) {
            return
        }

        val transaction: FragmentTransaction = mFragmentManager.beginTransaction()

        //&& null == fragmentManager.findFragmentByTag(tag)
        if (!targetFragment.isAdded && !mFragmentManager.fragments.contains(targetFragment)
        ) {
            mCurrentFragment?.let {
                transaction.hide(it)
            }
            transaction
                .add(R.id.leftFragment, targetFragment, tag)
                .commitAllowingStateLoss()
            //FragmentManager has been destroyed
            mFragmentManager.executePendingTransactions()
        } else {
            mCurrentFragment?.let {
                transaction.hide(it)
            }
            transaction
                .show(targetFragment)
                .commitAllowingStateLoss()
            mFragmentManager.executePendingTransactions()
        }
        mCurrentFragment = targetFragment
    }
}