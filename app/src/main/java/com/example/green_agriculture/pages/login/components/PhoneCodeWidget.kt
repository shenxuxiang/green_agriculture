package com.example.green_agriculture.pages.login.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.sp
import com.example.green_agriculture.toolkit.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneCodeWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {
    val defaultColor = ContextCompat.getColor(context, R.color.black9)
    val primaryColor = ContextCompat.getColor(context, R.color.primary)
    var textValue: String = "获取验证码"
        set(value) {
            if (value == field) return
            field = value

            this.text = value
        }

    var disabled: Boolean = true
        set(value) {
            if (value == field) return
            field = value
            if (value) {
                this.setTextColor(if (isInProgress) primaryColor else defaultColor)
            } else {
                this.setTextColor(primaryColor)
            }
        }

    /**
     * 倒计时进行中
     */
    var isInProgress = false

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        text = textValue
        setTextColor(defaultColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, 11.sp)

        setOnClickListener {
            if (disabled || isInProgress) return@setOnClickListener

            isInProgress = true

            coroutineScope.launch {
                var count = 60
                while (count-- > 0) {
                    withContext(Dispatchers.Main) {
                        textValue = "$count S"
                        LogUtils.d(textValue)
                    }

                    delay(1000)
                }

                withContext(Dispatchers.Main) {
                    isInProgress = false
                    textValue = "获取验证码"
                    setTextColor(if (disabled) defaultColor else primaryColor)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("disabled")
        fun bindDisabled(view: PhoneCodeWidget, disabled: Boolean) {
            view.disabled = disabled
        }
    }
}