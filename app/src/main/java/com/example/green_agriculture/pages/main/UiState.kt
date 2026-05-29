package com.example.green_agriculture.pages.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.green_agriculture.adapter.ViewPager2FragmentItemOption
import com.example.green_agriculture.pages.main.components.BottomNavigationBarItemOption

data class ViewPager2ListItem(
    override val fragment: Fragment,
    val selectedIcon: Int,
    val label: String,
    val icon: Int,
) : ViewPager2FragmentItemOption()

data class UiState(
    val tabIndex: Int = 0,
    val viewPager2: ViewPager2? = null,
    val viewPager2List: List<ViewPager2ListItem> = emptyList(),
    val bottomNavigationBarList: List<BottomNavigationBarItemOption> = emptyList(),
)