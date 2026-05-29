package com.example.green_agriculture.pages.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.green_agriculture.GAApplication
import com.example.green_agriculture.entity.RegionData
import com.example.green_agriculture.toolkit.LogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    /**
     * 省、市、区、
     */
    private val _regionData = MutableStateFlow<List<RegionData>>(emptyList())
    val regionData = _regionData.asStateFlow()

    /**
     * 加载 RegionData 的状态，true-正在加载，false-加载完成、或未开始
     */
    private var _loadingStatusForRegionData = false

    /**
     * 加载省、市、区、数据
     */
    fun loadRegionData() {
        // 避免重复执行加载
        if (_loadingStatusForRegionData) return

        _loadingStatusForRegionData = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val sb = StringBuilder()
                val type = object : TypeToken<List<RegionData>>() {}.type

                GAApplication.context.assets.open("region_data.json").reader().use { reader ->
                    reader.forEachLine { sb.append(it) }
                }

                _regionData.value = Gson().fromJson(sb.toString(), type)
            } catch (t: Throwable) {
                LogUtils.d(t)
            } finally {
                _loadingStatusForRegionData = false
            }
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun updateUIState(block: UiState.() -> UiState) {
        _uiState.update {
            it.block()
        }
    }

    val handleTabIndexChanged: (Int) -> Unit = {
        updateUIState { copy(tabIndex = it) }
    }
}