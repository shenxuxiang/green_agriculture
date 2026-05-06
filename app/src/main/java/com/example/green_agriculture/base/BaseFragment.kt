package com.example.green_agriculture.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>() : Fragment() {
    protected lateinit var binding: T

    @get:IdRes
    protected abstract val layoutID: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate<T>(inflater, layoutID, container, false)

        binding.lifecycleOwner = this

        initData()
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
     * 数据初始化、数据绑定，例如：绑定 ViewModel
     */
    open fun initData() {}

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
        binding.unbind()
    }


}