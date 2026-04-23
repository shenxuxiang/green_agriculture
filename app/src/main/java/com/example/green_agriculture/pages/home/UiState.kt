package com.example.green_agriculture.pages.home

import com.example.green_agriculture.R
import com.example.green_agriculture.components.SwiperWidgetOptionItem
import com.example.green_agriculture.entity.MenuItemOption

val options = listOf(
    MenuItemOption(resId = R.mipmap.home_1, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_2, title = "农机作业"),
    MenuItemOption(resId = R.mipmap.home_3, title = "病虫害识别"),
    MenuItemOption(resId = R.mipmap.home_4, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_5, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_6, title = "测量宝病虫害识别"),
    MenuItemOption(resId = R.mipmap.home_7, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_8, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_9, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_10, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_11, title = "测量宝"),
    MenuItemOption(resId = R.mipmap.home_12, title = "测量宝"),
)

data class UiState(
    val swiperList: List<SwiperWidgetOptionItem> = emptyList(),
    val logo: Int = R.mipmap.logo,
    val menuOptions: List<MenuItemOption> = options,
)