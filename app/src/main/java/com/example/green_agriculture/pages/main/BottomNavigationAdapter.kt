package com.example.green_agriculture.pages.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

data class BottomNavigationItem(val label: String, val fragment: Fragment)

class BottomNavigationAdapter(
    activity: Fragment,
    private val fragments: List<BottomNavigationItem>,
) :
    FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}