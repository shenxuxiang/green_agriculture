package com.example.green_agriculture.pages.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val repository: RegisterRepository) : ViewModel() {
    val checkedUserAgreement = MutableStateFlow(false)

    val phone = MutableStateFlow("")

    val passwd = MutableStateFlow("")

    val username = MutableStateFlow("")

    val code = MutableStateFlow("")

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update { it.block() }
    }
}