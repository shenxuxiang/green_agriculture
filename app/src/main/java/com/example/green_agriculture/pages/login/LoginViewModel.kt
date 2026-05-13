package com.example.green_agriculture.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.green_agriculture.entity.HandlerRef
import com.example.green_agriculture.toolkit.LogUtils
import com.example.green_agriculture.toolkit.PatternUtils
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {
    /**
     * 验证码输入框句柄
     * @property current TextInputEditText 实例
     */
    val phoneCodeEditTextRef = HandlerRef(null)
    private val _uiState = MutableStateFlow(UiState())

    val uiState = _uiState.asStateFlow()

    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update { it.block() }
    }

    val phone = MutableStateFlow("")
    val code = MutableStateFlow("")

    /**
     * 发送登录验证码
     * @return true-发送成功，false-发送失败
     */
    val sendPhoneCode: suspend (phone: String) -> Boolean = {
        val result = repository.sendPhoneCode(mapOf("phone" to it, "type" to "1"))

        withContext(Dispatchers.Main) {
            (phoneCodeEditTextRef.current as TextInputEditText).requestFocus()
        }

        result
    }

    val handleLogin: () -> Unit = {
        LogUtils.d("=================Login")
    }

    init {
        /**
         * 监听：手机号 + 验证码
         * 实时验证是否可以登录
         */
        viewModelScope.launch {
            combine(phone, code) { v1, v2 ->
                return@combine PatternUtils.phonePattern.matches(v1) && v2.length >= 6
            }.collect {
                updateUIState { copy(enabledFastLogin = it) }
            }
        }
    }
}