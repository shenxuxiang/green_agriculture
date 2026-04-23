package com.example.green_agriculture.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.CalculateUtils
import com.example.green_agriculture.toolkit.Navigator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.internal.ViewUtils.dpToPx

class ToolbarWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppBarLayout(context, attrs, defStyleAttr) {
    val toolbarTitle: TextView
    val toolbarBackIcon: IconWidget
    val toolbarActions: LinearLayout
    val toolbarX: FrameLayout

    // toolbar 标题
    var title: String = ""
        set(value) {
            if (value == field) return

            field = value
            toolbarTitle.text = value
        }

    // toolbar 内容高度
    var contentHeight: Int = 48
        set(value) {
            if (value == field) return
            field = value
            val toolbarXLP = toolbarX.layoutParams as Toolbar.LayoutParams
            toolbarXLP.height = dpToPx(context, value).toInt()
            toolbarX.layoutParams = toolbarXLP
        }

    init {
        elevation = 0f
        stateListAnimator = null
        setBackgroundColor(Color.TRANSPARENT)

        LayoutInflater.from(context).inflate(R.layout.layout_toolbar_widget, this, true).apply {
            toolbarX = findViewById(R.id.toolbar_x)
            toolbarTitle = findViewById(R.id.toolbarTitle)
            toolbarActions = findViewById(R.id.toolbarActions)
            toolbarBackIcon = findViewById(R.id.toolbarBackIcon)
        }

        /**
         * 根据 Navigator.canPop() 决定是否展示 toolbarBackIcon
         */
        toolbarBackIcon.visibility = if (Navigator.canPop()) VISIBLE else GONE
        toolbarBackIcon.setOnClickListener { Navigator.popBackStack() }

        /**
         * 给 toolbarX 添加一个高度和 StatusBar 高度一致的外边距
         */
        val statusBarH = CalculateUtils.statusBarHeight.toInt()
        val toolbarXLP = toolbarX.layoutParams as Toolbar.LayoutParams
        toolbarXLP.setMargins(0, statusBarH, 0, 0)
        toolbarX.layoutParams = toolbarXLP
    }

    /**
     * 添加 Actions
     */
    fun addAction(view: View) {
        toolbarActions.addView(view)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("title")
        fun bindTitle(view: ToolbarWidget, title: String) {
            view.title = title
        }

        @JvmStatic
        @BindingAdapter("height")
        fun bindHeight(view: ToolbarWidget, height: Int) {
            view.contentHeight = height
        }
    }
}