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

    override fun initView() {
        super.initView()
        binding.event = this
        binding.viewModel = viewModel
        binding.panelController.isUserInputEnabled = false
        binding.panelController.adapter =
            ViewPager2FragmentStateAdapter(this@LoginFragment, viewModel.panelList)
    }

    val onTabChanged: (Int) -> Unit = {
        viewModel.updatePanelIndex(it)
        binding.panelController.setCurrentItem(it, false)
    }
}