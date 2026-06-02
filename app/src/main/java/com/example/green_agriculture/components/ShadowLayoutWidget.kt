package com.example.green_agriculture.components

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.example.green_agriculture.R

class ShadowLayoutWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        context.withStyledAttributes(attrs, R.styleable.ShadowLayoutWidget, defStyleAttr) {
            val cornerRadius = getDimension(R.styleable.ShadowLayoutWidget_cornerRadius, 0f)
            val topLeftCornerRadius =
                getDimension(R.styleable.ShadowLayoutWidget_topLeftCornerRadius, cornerRadius)
            val topRightCornerRadius =
                getDimension(R.styleable.ShadowLayoutWidget_topRightCornerRadius, cornerRadius)
            val bottomLeftCornerRadius =
                getDimension(R.styleable.ShadowLayoutWidget_bottomLeftCornerRadius, cornerRadius)
            val bottomRightCornerRadius =
                getDimension(R.styleable.ShadowLayoutWidget_bottomRightCornerRadius, cornerRadius)

            val shadowColor = getColor(R.styleable.ShadowLayoutWidget_shadowColor, R.color.black3)
            val shadowAlpha = getFloat(R.styleable.ShadowLayoutWidget_shadowAlpha, 1f)

            clipToOutline = true
            // 设置 View 的聚光阴影颜色（spot shadow）。聚光阴影是由光源方向性产生的较暗、方向感强的阴影。
            outlineSpotShadowColor = shadowColor
            // 设置 View 的环境阴影颜色（ambient shadow）。环境阴影是物体在环境中产生的柔和、漫反射阴影。
            outlineAmbientShadowColor = Color.WHITE
            // 自定义 View 的轮廓（Outline）。轮廓决定了阴影的形状，以及当 clipToOutline = true 时的裁剪形状。
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.apply {
                        // 设置轮廓的透明度，因为阴影就在轮廓中，也就是说这会影响到阴影的透明度。
                        alpha = shadowAlpha
                        val width = (view?.width ?: 0).toFloat()
                        val height = (view?.height ?: 0).toFloat()

                        val path = Path().apply {
                            moveTo(0f, topLeftCornerRadius)
                            // 绘制左上角圆弧
                            if (topLeftCornerRadius > 0) arcTo(
                                RectF(
                                    0f,
                                    0f,
                                    topLeftCornerRadius * 2,
                                    topLeftCornerRadius * 2,
                                ),
                                180f,
                                90f,
                            )

                            lineTo(width - topRightCornerRadius, 0f)

                            // 绘制右上角圆弧
                            if (topRightCornerRadius > 0) arcTo(
                                RectF(
                                    width - topRightCornerRadius * 2,
                                    0f,
                                    width,
                                    topRightCornerRadius * 2,
                                ),
                                -90f,
                                90f,
                            )

                            lineTo(width, height - bottomRightCornerRadius)

                            // 绘制右下角圆弧
                            if (bottomRightCornerRadius > 0) arcTo(
                                RectF(
                                    width - bottomRightCornerRadius * 2,
                                    height - bottomRightCornerRadius * 2,
                                    width,
                                    height,
                                ),
                                0f,
                                90f,
                            )

                            lineTo(bottomLeftCornerRadius, height)

                            // 绘制左下角圆弧
                            if (bottomLeftCornerRadius > 0) arcTo(
                                RectF(
                                    0f,
                                    height - bottomLeftCornerRadius * 2,
                                    bottomLeftCornerRadius * 2,
                                    height,
                                ),
                                90f,
                                90f,
                            )
                            close()
                        }

                        setPath(path)
                    }
                }
            }
        }

//        // 投射阴影的大小和模糊程度：高度越大，阴影越扩散、越模糊。
//        elevation = 3.dp
//        // 将 View 的裁剪区域设置为由 outlineProvider 定义的轮廓（Outline）。开启后，View 的内容（包括子 View）会被裁剪到轮廓形状内。
//        clipToOutline = true
//
    }
}