package com.example.green_agriculture.entity

import com.example.green_agriculture.adapter.StringToIntAdapter
import com.google.gson.annotations.JsonAdapter

data class PaginationData<T>(
    val list: T,
    @JsonAdapter(StringToIntAdapter::class) val total: Int,
    @JsonAdapter(StringToIntAdapter::class) val pageNum: Int,
    @JsonAdapter(StringToIntAdapter::class) val pageSize: Int,
)