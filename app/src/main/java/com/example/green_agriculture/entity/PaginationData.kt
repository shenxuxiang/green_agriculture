package com.example.green_agriculture.entity

data class PaginationData<T>(
    val list: T,
    val total: String,
    val pageNum: String,
    val pageSize: String,
)