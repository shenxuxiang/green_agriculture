package com.example.green_agriculture.pages.main

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMainBinding
import com.example.green_agriculture.pages.agricultural.AgriculturalFragment
import com.example.green_agriculture.pages.home.HomeFragment
import com.example.green_agriculture.pages.mine.MineFragment
import com.example.green_agriculture.pages.release.ReleaseFragment
import com.example.green_agriculture.pages.service.ServiceFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    override val layoutID = R.layout.fragment_main
    private val viewModel by viewModels<MainViewModel>()
    private val tabList = listOf(
        BottomNavigationItem(label = "首页", fragment = HomeFragment()),
        BottomNavigationItem(label = "服务", fragment = ServiceFragment()),
        BottomNavigationItem(label = "发布", fragment = ReleaseFragment()),
        BottomNavigationItem(label = "农技", fragment = AgriculturalFragment()),
        BottomNavigationItem(label = "我的", fragment = MineFragment()),
    )
    private lateinit var parameters: MainFragmentArgs

    override fun initData() {
        super.initData()
        binding.viewModel = viewModel
        parameters = MainFragmentArgs.fromBundle(requireArguments())
    }

    override fun initView() {
        super.onEventBinding()
        binding.viewPager.adapter = BottomNavigationAdapter(this, tabList)
        //binding.viewPager.setCurrentItem(parameters.tabIndex, false)
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
}