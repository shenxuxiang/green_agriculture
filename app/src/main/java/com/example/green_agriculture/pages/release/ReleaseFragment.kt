package com.example.green_agriculture.pages.release

import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentReleaseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReleaseFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentReleaseBinding
}