package com.example.green_agriculture.pages.register.components

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.green_agriculture.R
import com.example.green_agriculture.components.IconWidget
import com.example.green_agriculture.entity.HandlerRef
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InputBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    val divider: View
    val label: TextView
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
        LayoutInflater.from(context).inflate(R.layout.layout_register_input_box, this, true)
            .apply {
                label = findViewById(R.id.label)
                divider = findViewById(R.id.divider)
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
            // 记录光标位置
            val selectionEnd = inputEditText.selectionEnd
            val selectionStart = inputEditText.selectionStart

            if (visiblePasswdFlag) {
                method = PasswordTransformationMethod.getInstance()
                iconName = ContextCompat.getString(context, R.string.icon_visible)
            } else {
                method = HideReturnsTransformationMethod.getInstance()
                iconName = ContextCompat.getString(context, R.string.icon_invisible)
            }
            
            /**
             * 修改 transformationMethod 后，光标会自动移动到文本起始位置
             * 所以这里要手动恢复光标的原始位置
             */
            visiblePasswdFlag = !visiblePasswdFlag
            visiblePasswdButton.iconName = iconName
            inputEditText.transformationMethod = method
            inputEditText.setSelection(selectionStart, selectionEnd)
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
            // XML 布局文件中自带的节点
            val childNodes = listOf(container, label, divider)
            childrenToMove.forEach { child ->
                // 插件 child 是不是已经存在的节点
                if (child !in childNodes) {
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
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imeOptions")
        fun bindImeOptions(view: InputBox, imeOptions: String) {
            view.imeOptions = imeOptions
        }

        @JvmStatic
        @BindingAdapter("maxLength")
        fun bindMaxLength(view: InputBox, maxLength: Int) {
            view.maxLength = maxLength
        }

        @JvmStatic
        @BindingAdapter("allowClear")
        fun bindAllowClear(view: InputBox, allowClear: Boolean) {
            view.allowClear = allowClear
        }

        @JvmStatic
        @BindingAdapter("placeholderText")
        fun bindPlaceholderText(view: InputBox, placeholderText: String) {
            view.placeholderText = placeholderText
        }

        /**
         * @param inputType: password | number | phone | text
         */
        @JvmStatic
        @BindingAdapter("inputType")
        fun bindInputType(view: InputBox, inputType: String) {
            view.inputType = inputType
        }

        @JvmStatic
        @BindingAdapter("value")
        fun bindValue(view: InputBox, value: String) {
            view.inputValue = value
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
        fun bindGetValue(view: InputBox): String {
            return view.inputValue
        }

        @JvmStatic
        @BindingAdapter("valueAttrChanged")
        fun bindValueAttrChange(view: InputBox, onChange: InverseBindingListener) {
            view.onInputChangeListener = onChange
        }

        @JvmStatic
        @BindingAdapter("ref")
        fun bindRef(view: InputBox, ref: HandlerRef) {
            ref.current = view.inputEditText
        }

        @JvmStatic
        @BindingAdapter("label")
        fun bindLabel(view: InputBox, label: String) {
            view.label.text = label
        }
    }
}