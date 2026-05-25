package com.example.green_agriculture.pages.register

import androidx.lifecycle.ViewModel
import com.example.green_agriculture.entity.HandlerRef
import com.example.green_agriculture.entity.SelectedRegionItemOption
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    val region = MutableStateFlow<List<SelectedRegionItemOption>>(emptyList())

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
}