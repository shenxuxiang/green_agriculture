package com.example.green_agriculture.pages.login.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.sp
import com.example.green_agriculture.toolkit.PatternUtils
import com.example.green_agriculture.toolkit.Toast
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

    var phone: String = ""
        set(value) {
            if (value == field) return
            field = value

            enabledGetPhoneCode = PatternUtils.phonePattern.matches(phone)
        }

    var enabledGetPhoneCode: Boolean = false
        set(value) {
            if (value == field) return
            field = value
            if (value) {
                this.setTextColor(primaryColor)
            } else {
                this.setTextColor(if (isInProgress) primaryColor else defaultColor)
            }
        }

    var sendPhoneCode: (suspend (phone: String) -> Boolean)? = null

    /**
     * 倒计时进行中
     */
    var isInProgress = false

    init {
        text = textValue
        setTextColor(defaultColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, 11.sp)

        setOnClickListener {
            if (phone.isEmpty()) Toast.showWarn("请输入手机号")
            if (!enabledGetPhoneCode || isInProgress) return@setOnClickListener

            isInProgress = true

            findViewTreeLifecycleOwner()?.let {
                it.lifecycleScope.launch {
                    // 发送验证码
                    if (sendPhoneCode?.invoke(phone) ?: false) {
                        Toast.showSuccess("验证码已发送")

                        // 开始倒计时
                        var count = 60
                        while (count-- > 0) {
                            withContext(Dispatchers.Main) { textValue = "$count S" }
                            delay(1000)
                        }

                        withContext(Dispatchers.Main) {
                            isInProgress = false
                            textValue = "获取验证码"
                            setTextColor(if (enabledGetPhoneCode) primaryColor else defaultColor)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("phone")
        fun bindPhone(view: PhoneCodeWidget, phone: String) {
            view.phone = phone
        }

        @JvmStatic
        @BindingAdapter("sendPhoneCode")
        fun bindSendPhoneCode(
            view: PhoneCodeWidget,
            sendPhoneCode: suspend (phone: String) -> Boolean,
        ) {
            view.sendPhoneCode = sendPhoneCode
        }
    }
}