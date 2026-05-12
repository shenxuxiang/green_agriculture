package com.example.green_agriculture.pages.login.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.green_agriculture.databinding.FragmentAccountLoginPanelBinding

class AccountLoginPanelFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAccountLoginPanelBinding.inflate(inflater, container, false)

        return binding.root
    }
}