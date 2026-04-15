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
    private lateinit var binding: AlertWidgetBinding
    var ratio = 0.4f
    var marginHorizontalDp = 30
    var showConfirm = true
    var showCancel = true
    var cancelText = "取消"
    var confirmText = "确认"
    var title = ""

    var onConfirmListener: (() -> Unit)? = null

    var onCancelListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 不推荐设置 DialogFragment 的 isCancelable，它会禁止系统返回键关闭 Dialog、以及禁止点击外部关闭 Dialog
        // isCancelable = false
        val spaceHorizontal = CalculateUtils.dpToPx(marginHorizontalDp, requireContext()).toInt()
        binding = AlertWidgetBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertWidgetDialogTheme).create()
        dialog.setView(
            binding.root,
            spaceHorizontal,
            0,
            spaceHorizontal,
            0,
        )

        val displayMetrics = requireContext().resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        // 同时禁止返回键关闭（可选）
        // dialog.setCancelable(false)
        // 禁止点击外部关闭
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.apply {
            val distY = screenHeight - alertContentH - CalculateUtils.statusBarHeight
            // 当 Gravity 为 TOP 或 BOTTOM 时，可以通过 attributes.y 设置距离顶部或底部的偏移量
            // 最后，还需要调用 setAttributes()，该方法内部回调用 requestLayout() 方法刷新 UI 界面
            attributes.y = (distY * ratio).toInt()

            setGravity(Gravity.TOP)
            setAttributes(attributes)
            // 设置 Dialog 背景遮罩层的透明度
            setDimAmount(0.5f)

            // 设置 Dialog Window 的内容背景（即对话框内部布局背后的背景）
            setWindowAnimations(R.style.AlertWidgetAnimation)
            // setBackgroundDrawableResource(android.R.color.transparent)
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

        /**
         * title       - 标题
         * fraction    - Alert 在垂直方向上的位置，0.5-居中，0-顶部对齐、1-底部对齐
         * marginDp    - Alert 距离屏幕的外边距（水平方向），单位 Dp
         * showCancel  - 是否展示取消按钮，默认 true
         * showConfirm - 是否展示确认按钮，默认 true
         * cancelText  - 取消按钮展示文本
         * confirmText - 确认按钮展示文本
         * onCancel    - 取消监听
         * onConfirm   - 确认监听
         */
        fun show(
            fragmentManager: FragmentManager,
            title: String,
            marginDp: Int = 30,
            fraction: Float = 0.4f,
            cancelText: String = "取消",
            confirmText: String = "确认",
            showCancel: Boolean = true,
            showConfirm: Boolean = true,
            onCancel: (() -> Unit)? = null,
            onConfirm: (() -> Unit)? = null,
        ) {
            createInstance().apply {
                this.title = title
                this.cancelText = cancelText
                this.confirmText = confirmText
                this.showCancel = showCancel
                this.showConfirm = showConfirm

                ratio = fraction
                onCancelListener = onCancel
                onConfirmListener = onConfirm
                marginHorizontalDp = marginDp

                show(fragmentManager, TAG)
            }
        }
    }
}