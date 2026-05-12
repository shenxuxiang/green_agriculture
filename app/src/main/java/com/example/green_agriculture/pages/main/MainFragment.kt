package com.example.green_agriculture.pages.main

import androidx.databinding.ObservableField
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.annotation.AutoBinding
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.ViewPager2FragmentStateAdapter
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentMainBinding
    private val viewModel by hiltNavGraphViewModels<MainViewModel>(R.id.nav_graph)

    override fun initData() {
        super.initData()
        binding.fragment = this
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.onEventBinding()
        binding.viewPager.apply {
            adapter = ViewPager2FragmentStateAdapter(
                this@MainFragment,
                viewModel.uiState.value.viewPagerList
            )
            offscreenPageLimit = adapter!!.itemCount
            viewModel.updateUIState { copy(viewPager2 = this@apply) }
        }
    }

    override fun onDataObserve() {
        super.onDataObserve()

        var prevTabIndex = viewModel.uiState.value.tabIndex
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 监听 tabIndex 数据状态，及时更新 BottomNavigation
                viewModel.uiState.collect {
                    if (prevTabIndex != it.tabIndex) {
                        prevTabIndex = it.tabIndex
                        if (it.tabIndex != binding.viewPager.currentItem) {
                            binding.viewPager.setCurrentItem(it.tabIndex, false)
                        }
                    }
                }
            }
        }
    }

    override fun onEventBinding() {
        super.onEventBinding()

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.updateUIState {
                    copy(tabIndex = position)
                }
            }
        })
    }

    val onTabClick = ObservableField<(Int) -> Unit> {
        viewModel.updateUIState {
            copy(tabIndex = it)
        }
    }
}