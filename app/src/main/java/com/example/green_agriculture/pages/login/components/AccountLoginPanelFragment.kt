package com.example.green_agriculture.pages.login.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.green_agriculture.databinding.FragmentAccountLoginPanelBinding
import com.example.green_agriculture.pages.login.LoginViewModel

class AccountLoginPanelFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAccountLoginPanelBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }
}