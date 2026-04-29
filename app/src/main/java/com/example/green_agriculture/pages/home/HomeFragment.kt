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
import com.example.green_agriculture.databinding.FragmentHomeBinding
import com.example.green_agriculture.pages.main.MainViewModel
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
        initPolicyInformationList()
        binding.refreshLayout.setEnableRefresh(true)
    }

    override fun onEventBinding() {
        super.onEventBinding()
        binding.refreshLayout.setOnRefreshListener {
            it.finishRefresh(2000, true, true)
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

    /**
     * 初始化资讯列表
     */
    private fun initPolicyInformationList() {
        binding.policyInformationRecyclerView.apply {
            adapter = policyInformationAdapter
            addItemDecoration(PolicyInformationListItemDecoration())
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}