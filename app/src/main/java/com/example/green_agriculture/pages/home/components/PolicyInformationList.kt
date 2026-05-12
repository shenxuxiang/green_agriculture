package com.example.green_agriculture.pages.home.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isNotEmpty
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.green_agriculture.databinding.LayoutPolicyInformationItemBinding
import com.example.green_agriculture.entity.PolicyInformationItemOption
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.CommonUtils

class PolicyInformationList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    var resourceData: List<PolicyInformationItemOption> = emptyList()
        set(value) {
            if (value == field) return
            field = value

            if (this.isNotEmpty()) removeAllViews()

            val backgroundDrawable = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt())
                cornerRadius = 10.dp
            }
            value.forEach { option ->
                val binding = LayoutPolicyInformationItemBinding.inflate(
                    LayoutInflater.from(context),
                    this,
                    false
                )
                val childView = binding.root
                val childViewLP = childView.layoutParams as LayoutParams

                childViewLP.bottomMargin = 12.dp.toInt()
                childView.background = backgroundDrawable
                bind(binding, option)
                addView(childView)
            }
        }

    init {
        orientation = VERTICAL
        gravity = Gravity.TOP or Gravity.START
    }

    /**
     * 内容绑定
     */
    fun bind(binding: LayoutPolicyInformationItemBinding, option: PolicyInformationItemOption) {
        val ctx = binding.root.context
        val corner = RoundedCorners(6.dp.toInt())
        Glide.with(ctx)
            .load(CommonUtils.networkImageUrl(option.url))
            .transform(corner)
            .into(binding.avatar)

        binding.title.text = option.title
        binding.updateTime.text = option.updateTime
    }

    companion object {
        @JvmStatic
        @BindingAdapter("resourceData")
        fun bindResourceData(
            view: PolicyInformationList,
            resourceData: List<PolicyInformationItemOption>,
        ) {
            view.resourceData = resourceData
        }
    }
}
