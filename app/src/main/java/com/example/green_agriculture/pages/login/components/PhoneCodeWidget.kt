package com.example.green_agriculture.pages.login.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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

    var enabled: Boolean = false
        set(value) {
            if (value == field) return
            field = value

            this.setTextColor(primaryColor)
        }

    /**
     * 倒计时进行中
     */
    var isInProgress = MutableStateFlow(false)

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        text = textValue
        setTextColor(defaultColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, 11.sp)

        setOnClickListener {
            if (!enabled || isInProgress.value) return@setOnClickListener

            isInProgress.value = true

            coroutineScope.launch {
                var count = 60
                while (count-- > 0) {
                    withContext(Dispatchers.Main) {
                        textValue = "$count S"
                    }

                    delay(1000)
                }
                isInProgress.value = false
                textValue = "获取验证码"
                setTextColor(defaultColor)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()?.let {
            it.lifecycleScope.launch {
                it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    isInProgress.collect {
                        if ()
                    }
                }
            }
        }
    }
}