package com.example.green_agriculture.pages.home

import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.PolicyInformationListAdepter
import com.example.green_agriculture.adapter.PolicyInformationListItemDecoration
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.components.AlertWidget
import com.example.green_agriculture.databinding.FragmentHomeBinding
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutID = R.layout.fragment_home
    val viewModel by viewModels<HomeViewModel>()
    val mainViewModel by hiltNavGraphViewModels<MainViewModel>(R.id.nav_graph)

    val policyInformationAdapter = PolicyInformationListAdepter()

    override fun initData() {
        super.initData()
        binding.outerViewPager = mainViewModel.uiState.value.viewPager2
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
    }

    override fun onEventBinding() {
        super.onEventBinding()
        binding.button.setOnClickListener {
            AlertWidget.show(
                this@HomeFragment,
                title = "你好！真的要删除内容吗？系统异常，请联系管理员，接口出现异常，请立即修复",
                hapticFeedbackEnabled = false,
                onConfirm = {
                    Toast.show("系统异常，请联系管理员，接口出现异常，请立即修复")
                    Toast.showSuccess("操作成功，即将跳转至首页")
                    Toast.showWarn("系统异常，请联系管理员")
                }
            )
        }
    }

    override fun onDataObserve() {
        super.onDataObserve()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                var prevValue = viewModel.uiState.value.policyInformationList

                viewModel.uiState.collect {
                    if (it.policyInformationList != prevValue) {
                        policyInformationAdapter.submitList(it.policyInformationList)
                        prevValue = it.policyInformationList
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.policyInformationRecyclerView.apply {
            adapter = policyInformationAdapter
            addItemDecoration(PolicyInformationListItemDecoration())
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}