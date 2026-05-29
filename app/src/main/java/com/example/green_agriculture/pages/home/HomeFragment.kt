package com.example.green_agriculture.pages.home

import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.annotation.AutoBinding
import com.example.green_agriculture.adapter.PolicyInformationListAdepter
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.components.RefreshHeaderWidget
import com.example.green_agriculture.databinding.FragmentHomeBinding
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.pages.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentHomeBinding

    val viewModel: HomeViewModel by viewModels()
    val mainViewModel: MainViewModel by activityViewModels()
    val policyInformationAdapter = PolicyInformationListAdepter()

    override fun initView() {
        super.initView()
        binding.nestedScrollView.scrollY = viewModel.uiState.value.pageScrollYOffset
        binding.outerViewPager = mainViewModel.uiState.value.viewPager2
        binding.viewModel = viewModel
        initRefreshLayout()
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
     * 初始化 SmartRefreshLayout 的下拉刷新功能
     */
    private fun initRefreshLayout() {
        binding.refreshLayout.apply {
            setRefreshHeader(RefreshHeaderWidget(requireContext()))
            setReboundInterpolator(DecelerateInterpolator())
            setHeaderHeightPx(60.dp.toInt())
            setReboundDuration(300)
            setEnableRefresh(true)

            setOnRefreshListener {
                viewModel.pageRefresh {
                    it.finishRefresh(1000, true, false)
                }
            }
        }

        // 如果已经刷新过了，就不要重复的刷新了。
        if (!viewModel.uiState.value.isFinishedRefresh) {
            binding.refreshLayout.post {
                binding.refreshLayout.autoRefresh()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // 记录页面滚动位置
        viewModel.updateUIState { copy(pageScrollYOffset = binding.nestedScrollView.scrollY) }
    }
}
