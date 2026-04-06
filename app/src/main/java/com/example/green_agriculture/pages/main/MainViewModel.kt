package com.example.green_agriculture.pages.main

import androidx.lifecycle.ViewModel
import com.example.green_agriculture.R
import com.example.green_agriculture.pages.agricultural.AgriculturalFragment
import com.example.green_agriculture.pages.home.HomeFragment
import com.example.green_agriculture.pages.main.components.BottomNavigationItem
import com.example.green_agriculture.pages.main.components.TabNavigationItemOption
import com.example.green_agriculture.pages.mine.MineFragment
import com.example.green_agriculture.pages.release.ReleaseFragment
import com.example.green_agriculture.pages.service.ServiceFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val viewPagerList = listOf(
        BottomNavigationItem(
            label = "首页",
            fragment = HomeFragment(),
            icon = R.string.icon_home,
            selectedIcon = R.string.icon_fill_home,
        ),
        BottomNavigationItem(
            label = "服务",
            fragment = ServiceFragment(),
            icon = R.string.icon_service,
            selectedIcon = R.string.icon_fill_service,
        ),
        BottomNavigationItem(
            label = "发布",
            fragment = ReleaseFragment(),
            icon = R.string.icon_release,
            selectedIcon = R.string.icon_fill_release,
        ),
        BottomNavigationItem(
            label = "农技",
            fragment = AgriculturalFragment(),
            icon = R.string.icon_agriculture,
            selectedIcon = R.string.icon_fill_agriculture,
        ),
        BottomNavigationItem(
            label = "我的",
            fragment = MineFragment(),
            icon = R.string.icon_mine,
            selectedIcon = R.string.icon_fill_mine,
        ),
    )

    private val _tabIndex = MutableStateFlow(0)

    val tabIndex = _tabIndex.asStateFlow()

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    val tabList = MutableStateFlow(viewPagerList.map {
        TabNavigationItemOption(
            icon = it.icon,
            label = it.label,
            selectedIcon = it.selectedIcon
        )
    }).asStateFlow()
}