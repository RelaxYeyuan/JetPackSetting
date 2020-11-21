package com.neusoft.mc2.setting.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * dataBinding管理类
 * 1.初始化DataBinding
 * 2.提供DataBinding实例，默认不向子类暴露，通过这样的方式，来彻底解决视图调用的一致性问题
 */
abstract class DataBindingFragment : Fragment() {

    private val HANDLER = Handler()
    protected lateinit var mActivity: AppCompatActivity
    protected var mAnimationLoaded = false
    private lateinit var mActivityProvider: ViewModelProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        mActivityProvider = ViewModelProvider(mActivity, getAppFactory(mActivity))
    }

    protected abstract fun initViewModel()

    protected abstract fun getDataBindingConfig(): DataBindingConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBindingConfig = getDataBindingConfig()

        DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            dataBindingConfig.getLayout(),
            container,
            false
        ).also {
            it.lifecycleOwner = this
            it.setVariable(
                dataBindingConfig.getVmVariableId(),
                dataBindingConfig.getStateViewModel()
            )

            val bindingParams: SparseArray<Any> = dataBindingConfig.getBindingParams()
            for (key in 0..bindingParams.size()) {
                it.setVariable(bindingParams.keyAt(key), bindingParams.valueAt(key))
            }
            return it.root
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        //TODO 错开动画转场与 UI 刷新的时机，避免掉帧卡顿的现象
        HANDLER.postDelayed({
            if (!mAnimationLoaded) {
                mAnimationLoaded = true
                loadInitData()
            }
        }, 280)
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    protected fun loadInitData() {}

    protected fun <T : ViewModel?> getActivityViewModel(modelClass: Class<T>): T {
        return mActivityProvider[modelClass]
    }

    protected fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(
            (mActivity.applicationContext as BaseApplication),
            getAppFactory(mActivity)
        )
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        checkActivity(this)
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    private fun checkActivity(fragment: Fragment) {
        fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

}