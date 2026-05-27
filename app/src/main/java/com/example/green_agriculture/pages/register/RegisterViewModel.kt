package com.example.green_agriculture.pages.register

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.green_agriculture.components.AlertWidget
import com.example.green_agriculture.components.LoadingWidget
import com.example.green_agriculture.entity.HandlerRef
import com.example.green_agriculture.entity.SelectedRegionItemOption
import com.example.green_agriculture.toolkit.Encrypter
import com.example.green_agriculture.toolkit.PatternUtils
import com.example.green_agriculture.toolkit.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val repository: RegisterRepository) : ViewModel() {
    /**
     * 验证码输入框句柄
     * @property current TextInputEditText 实例
     */
    val phoneCodeEditTextRef = HandlerRef(null)
    val checkedUserAgreement = MutableStateFlow(false)

    val phone = MutableStateFlow("")

    val passwd = MutableStateFlow("")

    val username = MutableStateFlow("")

    val code = MutableStateFlow("")

    /**
     * 用户所在地区
     */
    val region = MutableStateFlow<List<SelectedRegionItemOption>>(emptyList())

    /**
     * 注册按钮是否可用
     */
    val registerButtonEnabled = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update { it.block() }
    }

    /**
     * 发送登录验证码
     * @return true-发送成功，false-发送失败
     */
    val sendPhoneCode: suspend (String) -> Boolean = { phone ->
        val json = JsonObject().apply {
            addProperty("type", 2)
            addProperty("phone", phone)
        }

        val result = repository.sendPhoneCode(json)

        withContext(Dispatchers.Main) {
            (phoneCodeEditTextRef.current as TextInputEditText).requestFocus()
        }

        result
    }

    val handleRegister: (View) -> Unit = { view ->
        if (!checkedUserAgreement.value) {
            AlertWidget.show(
                fragmentManager = view.findFragment<RegisterFragment>().childFragmentManager,
                title = "请您阅读并同意《用户协议》和《隐私协议》",
                onConfirm = {
                    checkedUserAgreement.value = true
                }
            )
        } else {
            val hideLoading = LoadingWidget.show(view.rootView as ViewGroup)
            val requestBody = JsonObject().apply {
                addProperty("code", code.value)
                addProperty("phone", phone.value)
                addProperty("userType", 5)
                addProperty("username", username.value)
                addProperty("regionCode", region.value.last().value)
                addProperty("regionName", region.value.last().label)
                addProperty("password", Encrypter.encrypt(passwd.value))
            }

            viewModelScope.launch {
                val success = repository.register(requestBody)
                delay(3000)
                hideLoading {
                    if (success) Toast.showSuccess("注册成功")
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(phone, passwd, username, region, code) {
                return@combine PatternUtils.phonePattern.matches(phone.value) &&
                        PatternUtils.passwordPattern.matches(passwd.value) &&
                        username.value.isNotEmpty() &&
                        region.value.isNotEmpty() &&
                        code.value.length == 6
            }.collect {
                registerButtonEnabled.value = it
            }
        }
    }
}