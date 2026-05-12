package com.example.green_agriculture.pages.mine

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    init {
//        viewModelScope.launch {
//            val checkStatus = repository.queryUserCheckStatus()
//            updateUIState { copy(userCheckStatus = checkStatus) }
//        }
    }
}