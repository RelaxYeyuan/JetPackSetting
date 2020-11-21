package com.neusoft.mc2.setting.base

import android.util.SparseArray
import androidx.lifecycle.ViewModel

/**
 * 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
 * 通过这样的方式，来彻底解决 视图调用的一致性问题，
 * 如此，视图刷新的安全性将和基于函数式编程的 Jetpack Compose 持平。
 * 而 DataBindingConfig 就是在这样的背景下，用于为 base 页面中的 DataBinding 提供绑定项。
 * <p>
 * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910
 * <p>
 * Create by KunMinX at 20/4/18
 */
class DataBindingConfig(
    private var layout: Int,
    private var vmVariableId: Int,
    private var stateViewModel: ViewModel
) {

    private val bindingParams: SparseArray<Any> = SparseArray<Any>()

    fun getLayout(): Int {
        return layout
    }

    fun getVmVariableId(): Int {
        return vmVariableId
    }

    fun getStateViewModel(): ViewModel? {
        return stateViewModel
    }

    fun getBindingParams(): SparseArray<Any> {
        return bindingParams
    }

    fun addBindingParam(variableId: Int, any: Any): DataBindingConfig {
        if (bindingParams[variableId] == null) {
            bindingParams.put(variableId, any)
        }
        return this
    }
}