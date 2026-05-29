package com.example.green_agriculture.pages.mine

import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.annotation.AutoBinding
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentMineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MineFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentMineBinding

    private val viewModel by viewModels<MineViewModel>()

    override fun initView() {
        super.initView()
        binding.viewModel = viewModel
    }

    override fun onDataObserve() {
        super.onDataObserve()
        val context = requireContext()
        Glide.with(context)
            .load(context.getDrawable(R.mipmap.default_avatar))
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .into(binding.userAvatar)
    }

    override fun onResume() {
        super.onResume()
        // 每次页面可见时都刷新一次用户的认证状态
        viewModel.updateUserCheckStatus()
        viewModel.queryUserInformation()
    }
}