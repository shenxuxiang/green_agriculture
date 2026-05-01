package com.example.green_agriculture.pages.mine

import com.example.green_agriculture.R
import com.example.green_agriculture.entity.MenuItemOption
import com.example.green_agriculture.entity.UserCheckStatus

data class UiState(
    val userCheckStatus: UserCheckStatus? = null,
    val mineServiceMenus: List<MenuItemOption> = listOf(
        MenuItemOption(title = "我的菜单", resId = R.mipmap.mine_service_1),
        MenuItemOption(title = "我的需求", resId = R.mipmap.mine_service_2),
        MenuItemOption(title = "我的提问", resId = R.mipmap.mine_service_3),
        MenuItemOption(title = "我的农场", resId = R.mipmap.mine_service_4),
        MenuItemOption(title = "我的贷款", resId = R.mipmap.mine_service_5),
        MenuItemOption(title = "我的保险", resId = R.mipmap.mine_service_6),
        MenuItemOption(title = "我的奖补", resId = R.mipmap.mine_service_7),
        MenuItemOption(title = "客服热线", resId = R.mipmap.mine_service_8),
    ),
)