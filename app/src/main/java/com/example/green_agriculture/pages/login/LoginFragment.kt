package com.example.green_agriculture.pages.login

import android.util.Log
import com.example.green_agriculture.R
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val layoutID = R.layout.fragment_login

    override fun onEventBinding() {
        super.onEventBinding()
        binding.title.setOnClickListener {
            Log.d("GA", "Hello World")
        }
    }
}