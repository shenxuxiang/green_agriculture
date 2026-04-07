package com.example.green_agriculture.pages.home

import androidx.fragment.app.viewModels
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutID = R.layout.fragment_home
    val viewModel by viewModels<HomeViewModel>()

    override fun initData() {
        super.initData()
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
    }
}