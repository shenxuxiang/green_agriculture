package com.example.green_agriculture.pages.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.components.AlertWidget
import com.example.green_agriculture.databinding.FragmentHomeBinding
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutID = R.layout.fragment_home
    val viewModel by viewModels<HomeViewModel>()
    val mainViewModel by hiltNavGraphViewModels<MainViewModel>(R.id.nav_graph)

    override fun initData() {
        super.initData()
        binding.outerViewPager = mainViewModel.uiState.value.viewPager2
        Log.d("GA_APP", "outerViewPager:${binding.outerViewPager}")
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
    }

    override fun onEventBinding() {
        super.onEventBinding()
        binding.button.setOnClickListener {
            AlertWidget.show(
                this@HomeFragment,
                title = "你好！真的要删除内容吗？",
                hapticFeedbackEnabled = false,
                onConfirm = {
                    Toast.show("xxx")
                    Toast.show("yyy")
                }
            )
        }
    }
}