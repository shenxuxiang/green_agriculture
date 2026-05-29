package com.example.green_agriculture.toolkit

object TokenManager {
    private var _token: String? = null
    private const val TOKEN = "USER_TOKEN"

    init {
        _token = LocalStorage.getItem(TOKEN, "")
    }

    var token: String?
        get() = _token
        set(value) {
            if (value == _token) return
            _token = value
            LocalStorage.setItem(TOKEN, value)
        }
}