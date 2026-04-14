package com.example.green_agriculture.components

import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.AlertWidgetBinding
import com.example.green_agriculture.toolkit.CalculateUtils

class AlertWidget : DialogFragment() {
    private val ratio = 0.4f
    private lateinit var binding: AlertWidgetBinding

    private var showConfirm = true
    private var showCancel = true
    private var cancelText = "取消"
    private var confirmText = "确认"
    private var title = ""

    private var onConfirmListener: (() -> Unit)? = null

    private var onCancelListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 不推荐设置 DialogFragment 的 isCancelable，它会取消系统返回键、以及禁止点击外部关闭
        // isCancelable = false
        binding = AlertWidgetBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext()).run {
            setView(binding.root)
            create()
        }

        // 同时禁止返回键关闭（可选）
        // dialog.setCancelable(false)
        // 禁止点击外部关闭
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.apply {
            val distY =
                requireContext().resources.displayMetrics.heightPixels - alertContentH - CalculateUtils.statusBarHeight
            // 当 Gravity 为 TOP 或 BOTTOM 时，可以通过 attributes.y 设置距离顶部或底部的偏移量
            // 最后，还需要调用 setAttributes()，该方法内部回调用 requestLayout() 方法刷新 UI 界面
            attributes.y = (distY * ratio).toInt()

            setGravity(Gravity.TOP)
            setAttributes(attributes)
            // 设置 Dialog 背景遮罩层的透明度
            setDimAmount(0.5f)
            // 设置 Dialog Window 的内容背景（即对话框内部布局背后的背景）
            setWindowAnimations(R.style.AlertWidgetAnimation)
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        onInitView()
        onDataBinding()
        onEventBinding()
        return dialog
    }

    private fun onInitView() {
        binding.root.background = GradientDrawable().apply {
            setColor(0xFFFFFFFF.toInt())
            cornerRadius = CalculateUtils.dpToPx(10, requireContext())
        }
    }

    private fun onDataBinding() {
        binding.title = title
        binding.showCancel = showCancel
        binding.showConfirm = showConfirm
        binding.cancelText = cancelText
        binding.confirmText = confirmText
    }

    private fun onEventBinding() {
        binding.cancelButton.setOnClickListener {
            onCancelListener?.invoke()
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            onConfirmListener?.invoke()
            dismiss()
        }
    }

    /**
     * 获取 Alert Content 实际高度；
     * measure() 使用 MeasureSpec.UNSPECIFIED 模式规格，此时没有任何限制；
     * 所以，获取到的结果是 Alert Content 实际内容的高度；
     */
    private val alertContentH: Int
        get() {
            binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            return binding.root.measuredHeight
        }

    companion object {
        private const val TAG = "alert_widget_###"
        fun createInstance(): AlertWidget {
            return AlertWidget()
        }

        fun show(fragmentManager: FragmentManager, title: String) {
            createInstance().apply {
                this.title = title
                show(fragmentManager, TAG)
            }
        }
    }
}