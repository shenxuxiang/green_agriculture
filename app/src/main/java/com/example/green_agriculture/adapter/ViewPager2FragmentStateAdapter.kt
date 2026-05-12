package com.example.green_agriculture.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class ViewPager2FragmentItemOption {
    abstract val fragment: Fragment
}

class ViewPager2FragmentStateAdapter<T : ViewPager2FragmentItemOption>(
    fragment: Fragment,
    private val fragmentList: List<T>,
) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int) = fragmentList[position].fragment

    override fun getItemCount() = fragmentList.size
}