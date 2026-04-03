package com.example.green_agriculture.pages.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update {
            it.block()
        }
    }

    private val _tabIndex = MutableStateFlow(0)
    
    val tabIndex = _tabIndex.asStateFlow()

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }
}