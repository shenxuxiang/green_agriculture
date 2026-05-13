package com.example.green_agriculture.pages.login.components

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.green_agriculture.R
import com.example.green_agriculture.components.IconWidget
import com.example.green_agriculture.entity.HandlerRef
import com.example.green_agriculture.extend.dp
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InputWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialCardView(context, attrs, defStyleAttr) {
    val clearButton: IconWidget
    val container: LinearLayout
    val inputLayout: TextInputLayout
    val visiblePasswdButton: IconWidget
    val inputEditText: TextInputEditText

    var visiblePasswdFlag: Boolean = false

    var onInputChangeListener: InverseBindingListener? = null

    var placeholderText: String = ""
        set(value) {
            if (value == field) return
            field = value

            inputLayout.placeholderText = value
        }

    var inputValue: String = ""
        set(value) {
            if (value == field) return
            field = value

            if (value != inputEditText.text.toString()) inputEditText.setText(value)

            if (allowClear) {
                clearButton.visibility = if (value.isNotEmpty()) VISIBLE else INVISIBLE
            } else {
                clearButton.visibility = GONE
            }

            if (inputType == "password") {
                visiblePasswdButton.visibility = if (inputValue.isNotEmpty()) VISIBLE else INVISIBLE
            } else {
                visiblePasswdButton.visibility = GONE
            }
        }

    var inputType: String = "text"
        set(value) {
            if (value == field) return
            field = value

            val type = when (value) {
                "password" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                "number" -> InputType.TYPE_CLASS_NUMBER
                "phone" -> InputType.TYPE_CLASS_PHONE
                else -> InputType.TYPE_CLASS_TEXT
            }

            inputEditText.inputType = type

            if (value == "password") {
                visiblePasswdButton.visibility = if (inputValue.isNotEmpty()) VISIBLE else INVISIBLE
            } else {
                visiblePasswdButton.visibility = GONE
            }
        }

    var imeOptions: String = "done"
        set(value) {
            if (value == field) return
            field = value

            inputEditText.imeOptions = when (value) {
                "go" -> EditorInfo.IME_ACTION_GO
                "next" -> EditorInfo.IME_ACTION_NEXT
                "done" -> EditorInfo.IME_ACTION_DONE
                "send" -> EditorInfo.IME_ACTION_SEND
                "prev" -> EditorInfo.IME_ACTION_PREVIOUS
                "search" -> EditorInfo.IME_ACTION_SEARCH
                else -> EditorInfo.IME_ACTION_DONE
            }
        }

    var allowClear: Boolean = true
        set(value) {
            if (value == field) return
            field = value

            if (value) {
                clearButton.visibility = if (inputValue.isNotEmpty()) VISIBLE else INVISIBLE
            } else {
                clearButton.visibility = GONE
            }
        }

    var maxLength: Int = Int.MAX_VALUE
        set(value) {
            if (value == field) return
            field = value

            inputEditText.filters = arrayOf(InputFilter.LengthFilter(value))
        }

    init {
        radius = 18.dp
        cardElevation = 0f
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.primaryContainer))
        LayoutInflater.from(context).inflate(R.layout.layout_login_input_widget, this, true).apply {
            container = findViewById(R.id.container)
            clearButton = findViewById(R.id.clearButton)
            inputLayout = findViewById(R.id.inputLayout)
            inputEditText = findViewById(R.id.inputEditText)
            visiblePasswdButton = findViewById(R.id.visiblePasswdButton)
        }

        /**
         * 清空所有内容
         */
        clearButton.setOnClickListener {
            inputValue = ""
        }

        /**
         * 密码可视状态切换
         */
        visiblePasswdButton.setOnClickListener {
            val method: Any
            val iconName: String

            if (visiblePasswdFlag) {
                method = PasswordTransformationMethod.getInstance()
                iconName = ContextCompat.getString(context, R.string.icon_visible)
            } else {
                method = HideReturnsTransformationMethod.getInstance()
                iconName = ContextCompat.getString(context, R.string.icon_invisible)
            }

            visiblePasswdFlag = !visiblePasswdFlag
            visiblePasswdButton.iconName = iconName
            inputEditText.transformationMethod = method
        }

        /**
         * 监听 input 事件
         */
        inputEditText.addTextChangedListener {
            inputValue = it.toString()
            onInputChangeListener?.onChange()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount > 1) {
            val childrenToMove = (1 until childCount).map { getChildAt(it) }
            childrenToMove.forEach { child ->
                removeView(child)
                // 保留原有的宽高
                val oldParams = child.layoutParams
                container.addView(
                    child,
                    LinearLayout.LayoutParams(oldParams.width, oldParams.height)
                )
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imeOptions")
        fun bindImeOptions(view: InputWidget, imeOptions: String) {
            view.imeOptions = imeOptions
        }

        @JvmStatic
        @BindingAdapter("maxLength")
        fun bindMaxLength(view: InputWidget, maxLength: Int) {
            view.maxLength = maxLength
        }

        @JvmStatic
        @BindingAdapter("allowClear")
        fun bindAllowClear(view: InputWidget, allowClear: Boolean) {
            view.allowClear = allowClear
        }

        @JvmStatic
        @BindingAdapter("placeholderText")
        fun bindPlaceholderText(view: InputWidget, placeholderText: String) {
            view.placeholderText = placeholderText
        }

        @JvmStatic
        @BindingAdapter("inputType")
        fun bindInputType(view: InputWidget, inputType: String) {
            view.inputType = inputType
        }

        @JvmStatic
        @BindingAdapter("value")
        fun bindValue(view: InputWidget, value: String) {
            view.inputValue = value
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
        fun bindGetValue(view: InputWidget): String {
            return view.inputValue
        }

        @JvmStatic
        @BindingAdapter("valueAttrChanged")
        fun bindValueAttrChange(view: InputWidget, onChange: InverseBindingListener) {
            view.onInputChangeListener = onChange
        }

        @JvmStatic
        @BindingAdapter("ref")
        fun bindRef(view: InputWidget, ref: HandlerRef) {
            ref.current = view.inputEditText
        }
    }
}


