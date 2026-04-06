package com.example.green_agriculture.pages.main

import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMainBinding
import com.example.green_agriculture.pages.main.components.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    override val layoutID = R.layout.fragment_main
    private val viewModel by viewModels<MainViewModel>()

    override fun initData() {
        super.initData()
        binding.fragment = this
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.onEventBinding()
        binding.viewPager.adapter = ViewPagerAdapter(this, viewModel.viewPagerList)
    }

    override fun onDataObserve() {
        super.onDataObserve()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 监听 tabIndex 数据状态，及时更新 BottomNavigation
                viewModel.tabIndex.collect {
                    if (it != binding.viewPager.currentItem) {
                        binding.viewPager.setCurrentItem(it, false)
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
                viewModel.updateTabIndex(position)
            }
        })
    }

    val onTabClick = ObservableField<(Int) -> Unit> {
        viewModel.updateTabIndex(it)
    }

}