package com.example.green_agriculture.pages.login

import android.util.Log
import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentLoginBinding

    override fun onEventBinding() {
        super.onEventBinding()
        binding.title.setOnClickListener {
            Log.d("GA", "Hello World")
        }
    }
}