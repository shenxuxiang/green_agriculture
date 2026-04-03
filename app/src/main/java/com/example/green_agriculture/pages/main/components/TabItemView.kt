package com.example.green_agriculture.pages.main.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.components.IconView
import com.google.android.material.internal.ViewUtils.dpToPx

class TabItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    val icon: IconView = findViewById(R.id.icon)
    val title: TextView = findViewById(R.id.title)

    init {
        LayoutInflater.from(context).inflate(R.layout.tab_item_view_layout, this, true)
        orientation = VERTICAL
        gravity = Gravity.CENTER
        layoutParams = LayoutParams(dpToPx(context, 80).toInt(), LayoutParams.WRAP_CONTENT)
    }

    companion object {
        @BindingAdapter("icon")
        fun setIcon(view: TabItemView, icon: String) {
            view.icon.setIconName(icon)
        }
    }
}