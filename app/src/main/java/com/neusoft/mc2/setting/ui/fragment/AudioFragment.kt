package com.neusoft.mc2.setting.ui.fragment

import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.FragmentManager
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseFragment
import com.neusoft.mc2.setting.base.DataBindingConfig
import com.neusoft.mc2.setting.ui.state.AudioViewModel

class AudioFragment(supportFragmentManager: FragmentManager) : BaseFragment(supportFragmentManager) {

    private lateinit var viewModel: AudioViewModel

    override fun fragmentHide() {
    }

    override fun fragmentShow() {
    }

    override fun showDataAnimation() {
    }

    override fun initViewModel() {
        viewModel = getAppViewModelProvider()[AudioViewModel::class.java]
    }

    override fun getDataBindingConfig(): DataBindingConfig =
        DataBindingConfig(R.layout.audio_fragment, BR.vm, viewModel)

}