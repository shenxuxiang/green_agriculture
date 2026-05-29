package com.example.green_agriculture.pages.home

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.Navigator
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
class HomeViewModel @Inject constructor(val repository: HomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update { it.block() }
    }

    val swiperIndex = MutableStateFlow(0)

    fun pageRefresh(block: () -> Unit) {
        viewModelScope.launch {
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
                    isFinishedRefresh = true,
                    swiperList = swiperList ?: emptyList(),
                    policyInformationList = policyInformationList ?: emptyList()
                )
            }
            // 执行回调
            withContext(Dispatchers.Main) { block() }
        }
    }

    val handleClickMoreText: (View) -> Unit = {
        Navigator.navigate(R.id.action_mainFragment_to_identityAuthFragment2)
    }
}