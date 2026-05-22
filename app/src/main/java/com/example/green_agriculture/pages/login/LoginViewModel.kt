package com.example.green_agriculture.pages.login

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.green_agriculture.R
import com.example.green_agriculture.components.AlertWidget
import com.example.green_agriculture.entity.HandlerRef
import com.example.green_agriculture.pages.login.components.AccountLoginPanelFragment
import com.example.green_agriculture.pages.login.components.FastLoginPanelFragment
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.PatternUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    /**
     * 提供的快捷登录、账号登录面板
     */
    val panelList = listOf(
        PanelItem(fragment = FastLoginPanelFragment()),
        PanelItem(fragment = AccountLoginPanelFragment()),
    )

    private val _panelIndex = MutableStateFlow(0)
    val panelIndex = _panelIndex.asStateFlow()

    fun updatePanelIndex(index: Int) {
        _panelIndex.value = index
    }

    val phone = MutableStateFlow("")
    val code = MutableStateFlow("")
    val account = MutableStateFlow("")
    val passwd = MutableStateFlow("")

    // 登录按钮是否可用
    private val _fastLoginButtonEnabled = MutableStateFlow(false)
    private val _accountLoginButtonEnabled = MutableStateFlow(false)
    val fastLoginButtonEnabled = _fastLoginButtonEnabled.asStateFlow()
    val accountLoginButtonEnabled = _accountLoginButtonEnabled.asStateFlow()

    // 是否勾选用户协议
    val fastLoginCheckedUserAgreement = MutableStateFlow(false)
    val accountLoginCheckedUserAgreement = MutableStateFlow(false)

    /**
     * 发送登录验证码
     * @return true-发送成功，false-发送失败
     */
    val sendPhoneCode: suspend (String) -> Boolean = { phone ->
        val json = JsonObject().apply {
            addProperty("type", 1)
            addProperty("phone", phone)
        }
        val result = repository.sendPhoneCode(json)

        withContext(Dispatchers.Main) {
            (phoneCodeEditTextRef.current as TextInputEditText).requestFocus()
        }

        result
    }

    /**
     * 手机验证码登录
     */
    val handleLoginForPhoneCode: (View) -> Unit = { view ->
        val fragmentManager = FragmentManager.findFragment<LoginFragment>(view).childFragmentManager
        if (!fastLoginCheckedUserAgreement.value) {
            AlertWidget.show(
                fragmentManager,
                title = "请您阅读并同意《用户协议》和《隐私协议》",
                onConfirm = {
                    fastLoginCheckedUserAgreement.value = true
                }
            )
        }
        // 登录逻辑
    }

    /**
     * 用户密码登录
     */
    val handleLoginForPasswd: (View) -> Unit = { view ->
        val fragmentManager = FragmentManager.findFragment<LoginFragment>(view).childFragmentManager
        if (!accountLoginCheckedUserAgreement.value) {
            AlertWidget.show(
                fragmentManager,
                title = "请您阅读并同意《用户协议》和《隐私协议》",
                onConfirm = {
                    accountLoginCheckedUserAgreement.value = true
                }
            )
        }
        // 登录逻辑
    }

    val handleNavToRegisterPage: (View) -> Unit = {
        Navigator.navigate(R.id.action_loginFragment_to_registerFragment)
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
                _fastLoginButtonEnabled.value = it
            }
        }
        /**
         * 监听：账号 + 密码
         * 实时验证是否可以登录
         */
        viewModelScope.launch {
            combine(account, passwd) { v1, v2 ->
                return@combine v1.isNotEmpty() && PatternUtils.passwordPattern.matches(v2)
            }.collect {
                _accountLoginButtonEnabled.value = it
            }
        }
    }
}