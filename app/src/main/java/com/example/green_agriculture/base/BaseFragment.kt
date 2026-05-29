package com.example.green_agriculture.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment() : Fragment() {
    protected abstract val binding: ViewDataBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        AutoBindingProcessor.bind(this, requireContext())

        binding.lifecycleOwner = this

        initView()
        onDataObserve()
        onEventBinding()

        return binding.root
    }

    /**
     * 页面初始化设置
     */
    open fun initView() {}

    /**
     * 数据监听，例如：对某个状态进行监听
     */
    open fun onDataObserve() {}

    /**
     * 事件绑定
     */
    open fun onEventBinding() {}

    /**
     * 数据结伴，如果你要自定义 onDestroyView，务必先执行 super.onDestroyView()
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
