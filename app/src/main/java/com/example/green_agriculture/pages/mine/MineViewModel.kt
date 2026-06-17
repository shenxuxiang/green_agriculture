package com.example.green_agriculture.pages.mine

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.LogUtils
import com.example.green_agriculture.toolkit.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val repository: MineRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update {
            it.block()
        }
    }

    fun updateUserCheckStatus() {
        viewModelScope.launch {
            val checkStatus = repository.queryUserCheckStatus()

            LogUtils.d("==========status: $checkStatus")
            updateUIState { copy(userCheckStatus = checkStatus) }
        }
    }

    fun queryUserInformation() {
        viewModelScope.launch {
            val data = repository.queryUserInformation()
            updateUIState { copy(userInformation = data) }
        }
    }

    val handleCheckStatus: (View) -> Unit = { view ->
        Navigator.navigate(R.id.action_mainFragment_to_choiceAuthFragment)
        
//        val userCheckStatus = uiState.value.userCheckStatus
//        when (userCheckStatus?.checkStatus) {
//            0 -> {
//                // "立即认证"
//                Navigator.navigate(R.id.action_mainFragment_to_choiceAuthFragment)
//            }
//
//            1 -> "查看认证"
//            2 -> "重新认证"
//            3 -> "查看认证"
//            else -> "--"
//        }

    }


}