package com.example.green_agriculture.pages.register

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentRegisterBinding
import com.example.green_agriculture.toolkit.CalculateUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentRegisterBinding

    private val viewModel by viewModels<RegisterViewModel>()

    /**
     * 监听键盘状态
     */
    val listener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        val context = requireContext()
        val activity = requireActivity()
        val screenH = context.resources.displayMetrics.heightPixels

        // 获取软键盘弹出的高度
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)

        val marginBottom: Int
        val keyboardHidden: Boolean
        val panelLayoutParams = binding.registerPanel.layoutParams as ViewGroup.MarginLayoutParams

        if (screenH - rect.bottom > screenH * 0.15) {
            keyboardHidden = false
            marginBottom = screenH - rect.bottom + CalculateUtils.statusBarHeight.toInt()
        } else {
            keyboardHidden = true
            marginBottom = 0
        }

        panelLayoutParams.setMargins(0, 0, 0, marginBottom)
        binding.registerPanel.layoutParams = panelLayoutParams
        binding.footerText.visibility = if (keyboardHidden) View.VISIBLE else View.GONE
    }

    override fun initData() {
        super.initData()

        binding.viewModel = viewModel
    }

    override fun onEventBinding() {
        super.onEventBinding()

        val activity = requireActivity()
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        val activity = requireActivity()
        activity.window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}