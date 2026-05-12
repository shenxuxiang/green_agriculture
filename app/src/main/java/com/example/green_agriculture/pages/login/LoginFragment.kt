package com.example.green_agriculture.pages.login

import androidx.fragment.app.viewModels
import com.example.annotation.AutoBinding
import com.example.green_agriculture.adapter.ViewPager2FragmentStateAdapter
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentLoginBinding

    val viewModel by viewModels<LoginViewModel>()

    override fun initData() {
        super.initData()
        binding.event = this
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
        binding.panelController.adapter =
            ViewPager2FragmentStateAdapter(this@LoginFragment, viewModel.uiState.value.panelList)
        binding.panelController.isUserInputEnabled = false
    }

    override fun onEventBinding() {
        super.onEventBinding()
    }

    val onTabChanged: (tabIndex: Int) -> Unit = {
        binding.panelController.setCurrentItem(it, false)
    }
}