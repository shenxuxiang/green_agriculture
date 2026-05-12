package com.example.green_agriculture.pages.login

import androidx.fragment.app.Fragment
import com.example.green_agriculture.adapter.ViewPager2FragmentItemOption
import com.example.green_agriculture.pages.login.components.AccountLoginPanelFragment
import com.example.green_agriculture.pages.login.components.FastLoginPanelFragment

data class PanelItem(override val fragment: Fragment) : ViewPager2FragmentItemOption()

data class UiState(
    val panelList: List<PanelItem> = listOf(
        PanelItem(fragment = FastLoginPanelFragment()),
        PanelItem(fragment = AccountLoginPanelFragment()),
    ),
)