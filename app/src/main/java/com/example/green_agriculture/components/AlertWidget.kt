package com.example.green_agriculture.components

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.AlertWidgetBinding
import com.example.green_agriculture.toolkit.CalculateUtils
import com.example.green_agriculture.toolkit.CommonUtils
import com.example.green_agriculture.toolkit.VibratorUtils

class AlertWidget : DialogFragment() {
    private lateinit var binding: AlertWidgetBinding

    // 定义 Alert 在垂直方向的位置，0-1，0.5 表示垂直居中
    var ratio = 0.4f

    // 定义 Alert 与屏幕两侧的之间的间距
    var margin = 30

    // 是否展示确认按钮
    var showConfirm = true

    // 是否展示取消按钮
    var showCancel = true

    // 取消按钮的文本
    var cancelText = "取消"

    // 确认按钮的文本
    var confirmText = "确认"

    // Alert 弹框展示的内容
    var title = ""

    // 点击 Alert 外部关闭弹框
    var isCanceledOnTouchOutsideFlag = true

    // 点击物理返回键关闭弹框
    var isCancelableFlag = true

    // 是否启用触觉反馈
    var hapticFeedbackEnabled = true

    // 添加点击确认按钮的回调事件
    var onConfirmListener: (() -> Unit)? = null

    // 添加点击取消按钮的回调事件
    var onCancelListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        binding = AlertWidgetBinding.inflate(LayoutInflater.from(context))

        val screenHeight = context.resources.displayMetrics.heightPixels
        val viewSpacing = CalculateUtils.dpToPx(margin, context).toInt()
        val dialog = AlertDialog.Builder(context, R.style.AlertWidgetDialogTheme).create()

        dialog.setView(
            binding.root,
            viewSpacing,
            0,
            viewSpacing,
            0,
        )

        // 禁止物理返回键关闭弹框
        dialog.setCancelable(isCancelableFlag)

        // 点击外部关闭弹框
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutsideFlag)

        dialog.window?.apply {
            val distY = screenHeight - alertContentH - CalculateUtils.statusBarHeight
            // 当 Gravity 为 TOP 或 BOTTOM 时，可以通过 attributes.y 设置距离顶部或底部的偏移量
            // 最后，还需要调用 setAttributes()，该方法内部回调用 requestLayout() 方法刷新 UI 界面
            attributes.y = (distY * ratio).toInt()

            setDimAmount(0.5f)
            setGravity(Gravity.TOP)
            setAttributes(attributes)
            setWindowAnimations(R.style.AlertWidgetAnimation)
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
            if (hapticFeedbackEnabled) VibratorUtils.oneShot()
            onCancelListener?.invoke()
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            if (hapticFeedbackEnabled) VibratorUtils.oneShot()
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

        /**
         * @param title                    标题
         * @param fraction                 Alert 在垂直方向上的位置，0.5-居中，0-顶部对齐、1-底部对齐
         * @param marginDp                 Alert 距离屏幕的外边距（水平方向），单位 Dp
         * @param showCancel               是否展示取消按钮，默认 true
         * @param showConfirm              是否展示确认按钮，默认 true
         * @param cancelText               取消按钮展示文本
         * @param confirmText              确认按钮展示文本
         * @param onCancel                 取消监听
         * @param onConfirm                确认监听
         * @param isCancelable             点击物理返回键关闭弹框
         * @param isCanceledOnTouchOutside 点击 Alert 外部关闭弹框
         * @param hapticFeedbackEnabled    是否启用触觉反馈
         */
        fun show(
            context: Context,
            title: String,
            marginDp: Int = 30,
            fraction: Float = 0.4f,
            cancelText: String = "取消",
            confirmText: String = "确认",
            showCancel: Boolean = true,
            showConfirm: Boolean = true,
            onCancel: (() -> Unit)? = null,
            onConfirm: (() -> Unit)? = null,
            isCancelable: Boolean = true,
            isCanceledOnTouchOutside: Boolean = true,
            hapticFeedbackEnabled: Boolean = true,
        ) {
            CommonUtils.getActivity(context)?.let {
                val ac = it as AppCompatActivity
                val fragmentManager = ac.supportFragmentManager

                AlertWidget().apply {
                    this.title = title
                    this.cancelText = cancelText
                    this.confirmText = confirmText
                    this.showCancel = showCancel
                    this.showConfirm = showConfirm
                    this.hapticFeedbackEnabled = hapticFeedbackEnabled
                    
                    ratio = fraction
                    margin = marginDp
                    onCancelListener = onCancel
                    onConfirmListener = onConfirm
                    isCancelableFlag = isCancelable
                    isCanceledOnTouchOutsideFlag = isCanceledOnTouchOutside

                    show(fragmentManager, TAG)
                }
            }
        }
    }
}