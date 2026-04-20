package com.example.green_agriculture.entity

data class ResponseData<T>(val code: Int, val message: String, val data: T)