package com.example.green_agriculture.pages.main

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.annotation.AutoBinding
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.ViewPager2FragmentStateAdapter
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMainBinding
import com.example.green_agriculture.pages.agricultural.AgriculturalFragment
import com.example.green_agriculture.pages.home.HomeFragment
import com.example.green_agriculture.pages.main.components.BottomNavigationBarItemOption
import com.example.green_agriculture.pages.mine.MineFragment
import com.example.green_agriculture.pages.release.ReleaseFragment
import com.example.green_agriculture.pages.service.ServiceFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val fragmentList = listOf(
        ViewPager2ListItem(
            label = "首页",
            fragment = HomeFragment(),
            icon = R.string.icon_home,
            selectedIcon = R.string.icon_fill_home,
        ),
        ViewPager2ListItem(
            label = "服务",
            fragment = ServiceFragment(),
            icon = R.string.icon_service,
            selectedIcon = R.string.icon_fill_service,
        ),
        ViewPager2ListItem(
            label = "发布",
            fragment = ReleaseFragment(),
            icon = R.string.icon_release,
            selectedIcon = R.string.icon_fill_release,
        ),
        ViewPager2ListItem(
            label = "农技",
            fragment = AgriculturalFragment(),
            icon = R.string.icon_agriculture,
            selectedIcon = R.string.icon_fill_agriculture,
        ),
        ViewPager2ListItem(
            label = "我的",
            fragment = MineFragment(),
            icon = R.string.icon_mine,
            selectedIcon = R.string.icon_fill_mine,
        ),
    )

    val bottomNavList = fragmentList.map {
        BottomNavigationBarItemOption(
            icon = it.icon,
            label = it.label,
            selectedIcon = it.selectedIcon
        )
    }

    override fun initView() {
        super.initView()
        viewModel.updateUIState {
            copy(
                bottomNavigationBarList = bottomNavList,
                viewPager2 = binding.viewPager,
                viewPager2List = fragmentList,
            )
        }

        binding.fragment = this
        binding.viewModel = viewModel
        // ViewPager2 初始化设置
        binding.viewPager.apply {
            setCurrentItem(viewModel.uiState.value.tabIndex, false)
            adapter = ViewPager2FragmentStateAdapter(this@MainFragment, fragmentList)
            offscreenPageLimit = fragmentList.size
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
}