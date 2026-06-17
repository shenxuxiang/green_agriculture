package com.example.green_agriculture.pages.user_address_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.green_agriculture.R
import com.example.green_agriculture.entity.PaginationData
import com.example.green_agriculture.entity.UserAddressItemOption
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.Toast
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAddressViewModel @Inject constructor(val repository: UserAddressRepository) :
    ViewModel() {
    private val defaultAddressId = MutableStateFlow(-1L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = Pager(
        config = PagingConfig(
            pageSize = 4,
            prefetchDistance = 2,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { AddressPagingSource { repository.queryUserAddressList(it) } }
    ).flow.cachedIn(viewModelScope)

    val displayFlow =
        pagingDataFlow.combine(defaultAddressId) { pagingData, defaultAddressId ->
            // 对 pagingData 进行遍历，返回一个新的 pagingData 数据
            pagingData.map { item ->
                val defaultFlag =
                    if (defaultAddressId == -1L) item.defaultFlag else item.addressId == defaultAddressId
                item.copy(defaultFlag = defaultFlag)
            }
        }.cachedIn(viewModelScope)

    fun handleSetDefaultAddress(addressId: Long) {
        viewModelScope.launch {
            val oldDefaultAddressId = defaultAddressId.value
            val result = repository.queryUserAddressSetDefault(JsonObject().apply {
                addProperty("addressId", addressId.toString())
            })

            if (result) {
                Toast.showSuccess("设置成功~")
                defaultAddressId.value = addressId
            } else {
                defaultAddressId.value = oldDefaultAddressId
            }
        }
    }

    init {
        viewModelScope.launch {
            delay(5000)
            Navigator.navigate(R.id.action_userAddressListFragment_to_identityAuthFragment2)
        }
    }
}

class AddressPagingSource(private val onLoad: suspend (JsonObject) -> PaginationData<List<UserAddressItemOption>>) :
    PagingSource<Int, UserAddressItemOption>() {

    override fun getRefreshKey(state: PagingState<Int, UserAddressItemOption>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { pageInfo ->
                pageInfo.prevKey?.plus(1) ?: 1
            }
        } ?: 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserAddressItemOption> {
        return try {
            val pageNum = params.key ?: 1
            val requestBody = JsonObject().apply {
                addProperty("pageNum", pageNum)
                addProperty("pageSize", params.loadSize)
            }

            val pageData = onLoad(requestBody)
            LoadResult.Page(
                data = pageData.list,
                prevKey = if (pageNum > 1) pageNum - 1 else null,
                nextKey = if (pageData.list.isNotEmpty() && pageData.total > pageNum * params.loadSize) pageNum + 1 else null
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}