package com.example.green_agriculture.components

import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.VibratorUtils

class CheckboxWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val innerView: CheckboxWidgetInner

    var checkedValue: Boolean = false
        set(value) {
            if (value == field) return
            field = value

            innerView.checkedValue = value
        }

    var size: Float = 18.dp
        set(value) {
            if (value == field) return
            field = value

            innerView.layoutParams.apply {
                width = value.toInt()
                height = value.toInt()
            }

            innerView.requestLayout()
        }

    var corner: Float = 4.dp
        set(value) {
            if (value == field) return
            field = value

            innerView.corner = value
        }

    var inverseBindingListener: InverseBindingListener? = null
    var onCheckedChange: ((Boolean) -> Unit)? = null

    init {
        innerView = CheckboxWidgetInner(context).apply {
            layoutParams = LayoutParams(
                this@CheckboxWidget.size.toInt(),
                this@CheckboxWidget.size.toInt(),
            ).apply {
                gravity = Gravity.CENTER
            }
        }
        innerView.corner = corner
        addView(innerView)

        setOnClickListener {
            VibratorUtils.oneShot()
            checkedValue = !checkedValue
            innerView.startAnimationTo(checkedValue)
            onCheckedChange?.invoke(checkedValue)
            inverseBindingListener?.onChange()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("checked")
        fun bindingChecked(view: CheckboxWidget, checked: Boolean) {
            view.checkedValue = checked
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "checked", event = "checkedAttrChanged")
        fun bindGetCheckedValue(view: CheckboxWidget): Boolean {
            return view.checkedValue
        }

        @JvmStatic
        @BindingAdapter("checkedAttrChanged")
        fun bindCheckedAttrChanged(view: CheckboxWidget, onChange: InverseBindingListener) {
            view.inverseBindingListener = onChange
        }

        @JvmStatic
        @BindingAdapter("size")
        fun bindSize(view: CheckboxWidget, size: Int) {
            view.size = size.dp
        }

        @JvmStatic
        @BindingAdapter("corner")
        fun bindCorner(view: CheckboxWidget, corner: Int) {
            view.corner = corner.dp
        }

        @JvmStatic
        @BindingAdapter("onCheckedChange")
        fun bindOnCheckedChange(view: CheckboxWidget, onCheckedChange: (Boolean) -> Unit) {
            view.onCheckedChange = onCheckedChange
        }
    }

    class CheckboxWidgetInner(context: Context) : View(context) {
        companion object {
            private val strokeWidth = 1.dp
            private const val ANIMATION_DURATION = 250L
        }

        private var animationProgress: Float = 0f

        /**
         * 动画估值器
         */
        @SuppressLint("RestrictedApi")
        private val argbEvaluator = ArgbEvaluator()
        private val pointFEvaluator = PointFEvaluator()
        private val whiteColor = ContextCompat.getColor(context, R.color.white)
        private val black9Color = ContextCompat.getColor(context, R.color.black9)
        private val primaryColor = ContextCompat.getColor(context, R.color.primary)

        var corner: Float = 4.dp
            set(value) {
                if (value == field) return
                field = value

                invalidate()
            }

        private var animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ANIMATION_DURATION
            interpolator = LinearInterpolator()

            addUpdateListener {
                animationProgress = it.animatedValue as Float
                invalidate()
            }
        }

        var checkedValue: Boolean = false
            set(value) {
                if (value == field) return

                field = value
                animationProgress = if (value) 1f else 0f
                invalidate()
            }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            animator.cancel()
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            /**
             * 绘制的矩形区域
             * 注意，这个矩形的大小为什么不是 RectF(0, 0, size, size) 呢？？？
             * 这是因为 Canvas 在绘制矩形边框时（假设此时的 strokeWidth 为 2），边框的一半绘制在内部，一半绘制在外部。
             * 那么实际绘制出来的矩形大小为 RectF(-1, -1, size + 1, size + 1)
             * 这就导致实际绘制出来的图形比开发时设定的尺寸要大。所以这才使用下面的方式设置 rectF。
             */
            val rectF = RectF(
                strokeWidth / 2,
                strokeWidth / 2,
                width - strokeWidth,
                height - strokeWidth,
            )

            drawOutline(canvas, animationProgress, rectF)
            var progress = animationProgress
            if (progress <= 0.5f) {
                progress /= 0.5f

                // 先绘制背景、再绘制边框
                drawBG(canvas, progress, rectF)
                drawOutline(canvas, progress, rectF)
            } else {
                drawBG(canvas, 1f, rectF)
                drawOutline(canvas, 1f, rectF)
                drawPath(canvas, (progress - 0.5f) / 0.5f, rectF)
            }
        }

        /**
         * 绘制边框
         */
        @SuppressLint("RestrictedApi")
        private fun drawOutline(canvas: Canvas, progress: Float, rectF: RectF) {
            val strokeColor = argbEvaluator.evaluate(progress, black9Color, primaryColor)

            val paint = Paint(ANTI_ALIAS_FLAG).apply {
                strokeWidth = 1.dp
                color = strokeColor as Int
                style = Paint.Style.STROKE
            }

            canvas.drawRoundRect(rectF, corner, corner, paint)
        }

        /**
         * 绘制背景
         */
        @SuppressLint("RestrictedApi")
        private fun drawBG(canvas: Canvas, progress: Float, rectF: RectF) {
            val bgColor = argbEvaluator.evaluate(progress, whiteColor, primaryColor)

            val paint = Paint(ANTI_ALIAS_FLAG).apply {
                color = bgColor as Int
                style = Paint.Style.FILL
            }

            canvas.drawRoundRect(rectF, corner, corner, paint)
        }

        /**
         * 构建路径
         */
        private fun buildPath(progress: Float, rectF: RectF): Path {
            val width = rectF.width()
            val height = rectF.height()
            val point1 = PointF((width / 3.6).toFloat(), (height / 1.9).toFloat())
            val point2 = PointF((width / 2.3).toFloat(), (height - height / 3.2).toFloat())
            val point3 = PointF((width - width / 3.6).toFloat(), (height / 2.8).toFloat())

            return if (progress <= 0.4f) {
                val endPoint = pointFEvaluator.evaluate(progress / 0.4f, point1, point2)
                Path().apply {
                    moveTo(point1.x, point1.y)
                    lineTo(endPoint.x, endPoint.y)
                }
            } else {
                val endPoint = pointFEvaluator.evaluate((progress - 0.4f) / 0.6f, point2, point3)
                Path().apply {
                    moveTo(point1.x, point1.y)
                    lineTo(point2.x, point2.y)
                    lineTo(endPoint.x, endPoint.y)
                }
            }
        }

        /**
         * 绘制路径的画笔
         */
        private val pathPaint = Paint(ANTI_ALIAS_FLAG).apply {
            strokeWidth = 2.dp
            color = whiteColor
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        /**
         * 绘制路径
         */
        private fun drawPath(canvas: Canvas, progress: Float, rectF: RectF) {
            val path = buildPath(progress, rectF)
            canvas.drawPath(path, pathPaint)
        }

        /**
         * 开始动画
         */
        fun startAnimationTo(checked: Boolean) {
            // 如果动画正在进行中，则直接取消动画即可。
            if (animator.isStarted) {
                animator.reverse()
                return
            }

            if (checked) animator.start() else animator.reverse()
        }
    }
}
