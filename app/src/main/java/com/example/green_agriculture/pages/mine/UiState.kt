package com.example.green_agriculture.pages.mine

import com.example.green_agriculture.R
import com.example.green_agriculture.entity.MenuItemOption
import com.example.green_agriculture.entity.UserCheckStatus
import com.example.green_agriculture.entity.UserInformation

data class UiState(
    val defaultAvatar: Int = R.mipmap.default_avatar,
    val userInformation: UserInformation? = null,
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
) {
    val displayUserAvatar: String
        get() = userInformation?.avatar ?: "--"

    val displayUserPhone: String
        get() = userInformation?.phone ?: "--"

    val displayUserName: String
        get() = userInformation?.username ?: "--"

    val displayUserAddress: String
        get() = userInformation?.address ?: "--"

    val displayCheckStatusName: String
        get() {
            // 0-未审核、1-待审核、2-审核不通过、3-审核通过
            return when (userCheckStatus?.checkStatus) {
                0, 1, 2 -> userCheckStatus.checkStatusName
                3 -> "${userCheckStatus.userTypeName} | 已认证"
                else -> "--"
            }
        }

    val displayCheckButtonText: String
        get() {
            // 0-未审核、1-待审核、2-审核不通过、3-审核通过
            return when (userCheckStatus?.checkStatus) {
                0 -> "立即认证"
                1 -> "查看认证"
                2 -> "重新认证"
                3 -> "查看认证"
                else -> "--"
            }
        }

    /**
     *
     */
    val displayCheckColorResId: Int
        get() = if (userCheckStatus?.checkStatus == 3) R.color.primary else R.color.black3
}