package com.example.green_agriculture.pages.choice_auth

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.bumptech.glide.Glide
import com.example.annotation.AutoBinding
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentChoiceAuthBinding
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.Navigator

class ChoiceAuthFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentChoiceAuthBinding

    override fun initView() {
        super.initView()
        binding.eventHandler = this
        binding.moduleIndicator.background = GradientDrawable().apply {
            cornerRadius = 2.dp
            color = ColorStateList.valueOf(0xFF3AC786.toInt())
        }

        Glide.with(requireContext())
            .load(R.mipmap.user_auth_1)
            .into(binding.avatar)
    }

    fun onTap(view: View) {
        Navigator.navigate(R.id.action_choiceAuthFragment_to_nongHuFragment)
    }
}