package com.example.green_agriculture.pages.agricultural

import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentAgriculturalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgriculturalFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentAgriculturalBinding
}