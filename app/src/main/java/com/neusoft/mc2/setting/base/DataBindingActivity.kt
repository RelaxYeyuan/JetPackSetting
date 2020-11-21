package com.neusoft.mc2.setting.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider

/**
 * dataBinding管理类
 * 1.初始化DataBinding
 * 2.提供DataBinding实例，默认不向子类暴露，通过这样的方式，来彻底解决视图调用的一致性问题
 */
abstract class DataBindingActivity : AppCompatActivity() {

    protected abstract fun initViewModel()

    protected abstract fun initData()

    protected abstract fun getDataBindingConfig(): DataBindingConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        val dataBindingConfig = getDataBindingConfig()

        DataBindingUtil.setContentView<ViewDataBinding>(this, dataBindingConfig.getLayout()).also {
            it.lifecycleOwner = this
            it.setVariable(
                dataBindingConfig.getVmVariableId(),
                dataBindingConfig.getStateViewModel()
            )

            val bindingParams: SparseArray<Any> = dataBindingConfig.getBindingParams()
            for (key in 0..bindingParams.size()) {
                it.setVariable(bindingParams.keyAt(key), bindingParams.valueAt(key))
            }
        }

        initData()
    }

    protected open fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(
            this.applicationContext as BaseApplication,
            getAppFactory(this)
        )
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
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


}