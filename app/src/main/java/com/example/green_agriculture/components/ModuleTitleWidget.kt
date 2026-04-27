package com.example.green_agriculture.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R

class ModuleTitleWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    lateinit var titleView: TextView

    lateinit var moreView: LinearLayout

    var title: String = ""
        set(value) {
            if (value == field) return

            field = value

            titleView.text = value
        }

    var hasMore: Boolean = true
        set(value) {
            if (field == value) return

            field = value

            moreView.visibility = if (value) VISIBLE else GONE
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        createTitleView()
        createMoreView()
    }

    private fun createTitleView() {
        titleView = TextView(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(context.getColor(R.color.black3))
        }
        addView(titleView)
    }

    private fun createMoreView() {
        moreView = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
            orientation = HORIZONTAL
        }

        val loadMoreText = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(context.getColor(R.color.black4))
            text = "更多"
        }

        val forwardIcon = IconWidget(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            iconSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                context.resources.displayMetrics
            )
            iconColor = context.getColor(R.color.black4)
            iconName = context.getString(R.string.icon_forward)
        }

        moreView.addView(loadMoreText)
        moreView.addView(forwardIcon)
        addView(moreView)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("title")
        fun bindTitle(view: ModuleTitleWidget, title: String) {
            view.title = title
        }

        @JvmStatic
        @BindingAdapter("hasMore")
        fun bindHasMore(view: ModuleTitleWidget, hasMore: Boolean) {
            view.hasMore = hasMore
        }
    }
}