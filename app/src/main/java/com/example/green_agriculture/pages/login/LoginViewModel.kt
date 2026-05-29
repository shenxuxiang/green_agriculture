package com.example.green_agriculture.pages.login

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.example.green_agriculture.R
import com.example.green_agriculture.components.AlertWidget
import com.example.green_agriculture.components.LoadingWidget
import com.example.green_agriculture.entity.HandlerRef
import com.example.green_agriculture.entity.UserInformation
import com.example.green_agriculture.pages.login.components.AccountLoginPanelFragment
import com.example.green_agriculture.pages.login.components.FastLoginPanelFragment
import com.example.green_agriculture.toolkit.ConstantUtils
import com.example.green_agriculture.toolkit.EncrypterUtils
import com.example.green_agriculture.toolkit.LocalStorage
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.PatternUtils
import com.example.green_agriculture.toolkit.Toast
import com.example.green_agriculture.toolkit.TokenManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
        (phoneCodeEditTextRef.current as TextInputEditText).requestFocus()

        val json = JsonObject().apply {
            addProperty("type", 1)
            addProperty("phone", phone)
        }

        val result = repository.sendPhoneCode(json)
        result
    }

    /**
     * 登录成功
     */
    suspend fun handleLoginSuccess(data: UserInformation) {
        TokenManager.token = data.token
        LocalStorage.setItem(ConstantUtils.USER_INFO, Gson().toJson(data))
        // 等待 Loading 关闭
        delay(300)
        Toast.showSuccess("登录成功")
        // 等待 Toast 关闭
        delay(2000)
        Navigator.navigate(
            resId = R.id.nav_graph,
            navOptions = NavOptions.Builder().run {
                setLaunchSingleTop(true)
                setPopUpTo(
                    destinationId = R.id.nav_graph,
                    inclusive = false,
                    saveState = false,
                )
                build()
            }
        )
    }

    /**
     * 手机验证码登录
     */
    val handleLoginForPhoneCode: (View) -> Unit = { view ->
        val fragmentManager =
            FragmentManager.findFragment<FastLoginPanelFragment>(view).parentFragment!!.childFragmentManager
        if (!fastLoginCheckedUserAgreement.value) {
            AlertWidget.show(
                fragmentManager,
                title = "请您阅读并同意《用户协议》和《隐私协议》",
                onConfirm = {
                    fastLoginCheckedUserAgreement.value = true
                }
            )
        } else {
            // 登录逻辑
            val rootView = view.rootView as ViewGroup
            val hideLoading = LoadingWidget.show(rootView)

            viewModelScope.launch {
                val requestBody = JsonObject().apply {
                    addProperty("code", code.value)
                    addProperty("phone", phone.value)
                }

                /**
                 * 登录成功
                 */
                val data = repository.queryLoginPhoneCode(requestBody)
                hideLoading(null)
                if (data != null) handleLoginSuccess(data)
            }
        }
    }

    /**
     * 用户密码登录
     */
    val handleLoginForPasswd: (View) -> Unit = { view ->
        val fragmentManager =
            FragmentManager.findFragment<AccountLoginPanelFragment>(view).parentFragment!!.childFragmentManager
        if (!accountLoginCheckedUserAgreement.value) {
            AlertWidget.show(
                fragmentManager,
                title = "请您阅读并同意《用户协议》和《隐私协议》",
                onConfirm = {
                    accountLoginCheckedUserAgreement.value = true
                }
            )
        } else {
            // 登录逻辑
            val rootView = view.rootView as ViewGroup
            val hideLoading = LoadingWidget.show(rootView)

            viewModelScope.launch {
                val requestBody = JsonObject().apply {
                    addProperty("username", account.value)
                    addProperty("clientType", "app")
                    addProperty("password", EncrypterUtils.encrypt(passwd.value))
                }

                /**
                 * 登录成功
                 */
                val data = repository.queryLoginPassword(requestBody)
                hideLoading(null)
                if (data != null) handleLoginSuccess(data)
            }
        }
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
                return@combine PatternUtils.phonePattern.matches(v1) &&
                        PatternUtils.phoneCodePattern.matches(v2)
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