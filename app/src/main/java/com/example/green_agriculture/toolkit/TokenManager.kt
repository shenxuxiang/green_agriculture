package com.example.green_agriculture.toolkit

import android.content.Context
import androidx.core.content.edit
import com.example.green_agriculture.GAApplication

object TokenManager {
    private const val TOKEN = "USER_TOKEN"
    private const val NAME = "ga_shared_pref"
    private const val MODE = Context.MODE_PRIVATE

    var token: String? = null
        set(value) {
            if (value == field) return

            GAApplication.context.getSharedPreferences(NAME, MODE).edit {
                if (value == null) {
                    remove(TOKEN)
                } else {
                    putString(TOKEN, value)
                }
            }
        }
        get() = GAApplication.context.getSharedPreferences(NAME, MODE).getString(TOKEN, "")
}