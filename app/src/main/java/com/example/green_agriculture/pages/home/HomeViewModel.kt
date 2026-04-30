package com.example.green_agriculture.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update { it.block() }
    }

    fun pageRefresh(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            val resp1 = async {
                repository.queryBannerList()
            }
            val resp2 = async {
                repository.queryPolicyInformationList()
            }

            val swiperList = resp1.await()
            val policyInformationList = resp2.await()

            updateUIState {
                copy(
                    swiperList = swiperList ?: emptyList(),
                    policyInformationList = policyInformationList ?: emptyList()
                )
            }

            withContext(Dispatchers.Main) {
                block()
            }
        }
    }
}