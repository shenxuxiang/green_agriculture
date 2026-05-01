package com.example.green_agriculture.entity

import androidx.annotation.DrawableRes

data class MenuItemOption(
    @DrawableRes val resId: Int,
    val title: String,
    val onClick: (() -> Unit)? = null
)