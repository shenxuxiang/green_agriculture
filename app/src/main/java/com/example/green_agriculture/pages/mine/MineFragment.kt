package com.example.green_agriculture.pages.mine

import androidx.fragment.app.viewModels
import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentMineBinding

    private val viewModel by viewModels<MineViewModel>()

    override fun initData() {
        super.initData()
        binding.viewModel = viewModel
    }

    override fun initView() {
        super.initView()
    }

}