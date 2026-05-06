package com.example.green_agriculture.pages.home

import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.PolicyInformationListAdepter
import com.example.green_agriculture.annotation.BindingProcessor
import com.example.green_agriculture.annotation.LayoutViewBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.components.RefreshHeaderWidget
import com.example.green_agriculture.databinding.FragmentHomeBinding
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.LogUtils
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

        BindingProcessor.processBinding(this, requireContext())

        LogUtils.d("=========initData: ${bindingSxx.root}")
    }

    override fun initView() {
        super.initView()
        initSmartRefreshLayout()
        initPolicyInformationList()
    }

    override fun onEventBinding() {
        super.onEventBinding()
        binding.refreshLayout.setOnRefreshListener {
            viewModel.pageRefresh {
                it.finishRefresh(1000, true, false)
            }
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
//        binding.policyInformationRecyclerView.apply {
//            adapter = policyInformationAdapter
//            addItemDecoration(PolicyInformationListItemDecoration())
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        }
    }

    /**
     * 初始化 SmartRefreshLayout 的下拉刷新功能
     */
    private fun initSmartRefreshLayout() {
        binding.refreshLayout.apply {
            setRefreshHeader(RefreshHeaderWidget(requireContext()))
            setHeaderHeightPx(60.dp.toInt())
            setReboundDuration(300)
            setReboundInterpolator(DecelerateInterpolator())
            setEnableRefresh(true)
        }
        binding.refreshLayout.post {
            binding.refreshLayout.autoRefresh()
        }
    }


    @LayoutViewBinding("fragment_home")
    private lateinit var bindingSxx: FragmentHomeBinding
}