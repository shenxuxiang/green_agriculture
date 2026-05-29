package com.example.green_agriculture.pages.register

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.annotation.AutoBinding
import com.example.green_agriculture.base.BaseFragment
import com.example.green_agriculture.databinding.FragmentRegisterBinding
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.CalculateUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {
    @AutoBinding
    override lateinit var binding: FragmentRegisterBinding

    private val viewModel by viewModels<RegisterViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels()

    /**
     * 底部导航栏的高度
     */
    private val navigationBarH = CalculateUtils.navigationBarHeight.toInt()

    override fun initView() {
        super.initView()

        binding.event = this
        binding.viewModel = viewModel
    }

    override fun onEventBinding() {
        super.onEventBinding()
        /**
         * 监听键盘状态
         */
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeH = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val panelLayoutParams =
                binding.registerPanel.layoutParams as ViewGroup.MarginLayoutParams


            val marginBottom = if (imeVisible) {
                imeH - navigationBarH
            } else {
                0
            }

            panelLayoutParams.setMargins(0, 0, 0, marginBottom)
            binding.registerPanel.layoutParams = panelLayoutParams
            binding.footerText.visibility = if (imeVisible) View.GONE else View.VISIBLE

            ViewCompat.onApplyWindowInsets(v, insets)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView, null)
    }

    val handleRegister: (View) -> Unit = {

    }
}