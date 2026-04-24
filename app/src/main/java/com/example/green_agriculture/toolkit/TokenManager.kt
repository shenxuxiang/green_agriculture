package com.example.green_agriculture.toolkit

object TokenManager {
    private const val TOKEN = "USER_TOKEN"

    var token: String? = null
        get() = LocalStorage.getItem(TOKEN, "")
        set(value) {
            if (value == field) return

            LocalStorage.setItem(TOKEN, value)
        }
}