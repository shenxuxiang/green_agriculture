package com.example.green_agriculture.pages.mine

import androidx.fragment.app.viewModels
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineFragment : BaseFragment<FragmentMineBinding>() {
    override val layoutID = R.layout.fragment_mine

    private val viewModel by viewModels<MineViewModel>()

    override fun initData() {
        super.initData()
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
    }
}