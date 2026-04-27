package com.example.green_agriculture.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update { it.block() }
    }

    init {
        viewModelScope.launch {
            val swiperList = repository.queryBannerList()
            updateUIState {
                copy(swiperList = swiperList)
            }
        }
    }
}