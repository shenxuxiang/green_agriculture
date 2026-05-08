package com.example.green_agriculture.pages.service

import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentServiceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentServiceBinding
}