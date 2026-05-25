package com.example.green_agriculture.pages.register.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.findFragment
import com.example.green_agriculture.R
import com.example.green_agriculture.components.RegionSelectModal
import com.example.green_agriculture.entity.SelectedRegionItemOption
import com.example.green_agriculture.pages.register.RegisterFragment

class RegionSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    val selectedView: TextView
    val hintText: TextView

    var selectedValue: List<SelectedRegionItemOption>? = null
        set(value) {
            if (value == field) return

            field = value

            selectedView.text = value?.lastOrNull()?.fullName ?: ""
            hintText.visibility = if (value?.isEmpty() ?: true) VISIBLE else GONE
            selectedView.visibility = if (value?.isEmpty() ?: true) GONE else VISIBLE
        }

    var onChangedListener: InverseBindingListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_register_region_selector, this, true)
            .apply {
                hintText = findViewById(R.id.hintText)
                selectedView = findViewById(R.id.selectedView)
            }

        setOnClickListener {
            RegionSelectModal.show(
                initialValue = selectedValue,
                fragmentManager = this.findFragment<RegisterFragment>().childFragmentManager,
            ) {
                selectedValue = it
                onChangedListener?.onChange()
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("value")
        fun bindValue(view: RegionSelector, value: List<SelectedRegionItemOption>) {
            view.selectedValue = value
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
        fun bindGetValue(view: RegionSelector): List<SelectedRegionItemOption>? {
            return view.selectedValue
        }

        @JvmStatic
        @BindingAdapter("valueAttrChanged")
        fun bindValueAttrChanged(view: RegionSelector, onChanged: InverseBindingListener) {
            view.onChangedListener = onChanged
        }
    }
}