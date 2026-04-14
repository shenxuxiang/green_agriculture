package com.example.green_agriculture.pages.home

import androidx.lifecycle.ViewModel
import com.example.green_agriculture.components.SwiperWidgetOptionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _bannerList = MutableStateFlow(
        listOf(
            SwiperWidgetOptionItem(url = "https://ww4.sinaimg.cn/mw690/001MabKgly1hvj8dymyywj63402c01ky02.jpg"),
            SwiperWidgetOptionItem(url = "https://pic.rmb.bdstatic.com/bjh/events/261575d21475efcc63ca7639302a6490633.png@h_1280"),
            SwiperWidgetOptionItem(url = "https://bce.bdstatic.com/doc/bce-doc/qianfan-docs/image%20%2819%29_50da488.png"),
            SwiperWidgetOptionItem(url = "https://gss0.baidu.com/7Po3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/6d81800a19d8bc3eee5771608e8ba61ea8d34538.jpg"),
        )
    )

    val bannerList = _bannerList.asStateFlow()

    val bannerIndex = MutableStateFlow(0)
    // val bannerIndex = _bannerIndex.asStateFlow()

    fun increment() {
        bannerIndex.value++
    }
}