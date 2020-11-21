package com.neusoft.mc2.setting.ui.fragment

import androidx.fragment.app.FragmentManager
import com.neusoft.mc2.setting.BR
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseFragment
import com.neusoft.mc2.setting.base.DataBindingConfig
import com.neusoft.mc2.setting.ui.state.DisplayViewModel

class DisplayFragment(supportFragmentManager: FragmentManager) : BaseFragment(supportFragmentManager) {

    private lateinit var viewModel: DisplayViewModel
    override fun fragmentHide() {

    }

    override fun fragmentShow() {
    }

    override fun showDataAnimation() {
    }

    override fun initViewModel() {
        viewModel = getAppViewModelProvider()[DisplayViewModel::class.java]
    }

    override fun getDataBindingConfig(): DataBindingConfig =
        DataBindingConfig(R.layout.display_fragment, BR.vm, viewModel)

}