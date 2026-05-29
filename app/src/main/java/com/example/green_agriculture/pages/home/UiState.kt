package com.example.green_agriculture.pages.home

import com.example.green_agriculture.R
import com.example.green_agriculture.entity.MenuItemOption
import com.example.green_agriculture.entity.PolicyInformationItemOption
import com.example.green_agriculture.entity.SwiperWidgetItemOption

data class UiState(
    val pageScrollYOffset: Int = 0,
    val isFinishedRefresh: Boolean = false,
    val swiperList: List<SwiperWidgetItemOption> = emptyList(),
    val policyInformationList: List<PolicyInformationItemOption> = emptyList(),
    val menuOptions: List<MenuItemOption> = listOf(
        MenuItemOption(resId = R.mipmap.home_1, title = "测量宝"),
        MenuItemOption(resId = R.mipmap.home_2, title = "农机作业"),
        MenuItemOption(resId = R.mipmap.home_3, title = "病虫害识别"),
        MenuItemOption(resId = R.mipmap.home_4, title = "我的农场"),
        MenuItemOption(resId = R.mipmap.home_5, title = "找农资"),
        MenuItemOption(resId = R.mipmap.home_6, title = "找农机"),
        MenuItemOption(resId = R.mipmap.home_7, title = "找植保"),
        MenuItemOption(resId = R.mipmap.home_8, title = "找农技"),
        MenuItemOption(resId = R.mipmap.home_9, title = "找贷款"),
        MenuItemOption(resId = R.mipmap.home_10, title = "找保险"),
        MenuItemOption(resId = R.mipmap.home_11, title = "找政策"),
        MenuItemOption(resId = R.mipmap.home_12, title = "找专家"),
    ),
)