package com.example.green_agriculture.toolkit

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.green_agriculture.GAApplication

object LocalStorage {
    private const val NAME = "ga_shared_pref"
    private const val MODE = Context.MODE_PRIVATE
    private val _prefs = GAApplication.context.getSharedPreferences(NAME, MODE)

    val prefs: SharedPreferences get() = _prefs

    inline fun <reified T> getItem(key: String, defValue: T): T? {
        return when (T::class) {
            Int::class -> prefs.getInt(key, defValue as Int) as T
            Long::class -> prefs.getLong(key, defValue as Long) as T
            Float::class -> prefs.getFloat(key, defValue as Float) as T
            String::class -> prefs.getString(key, defValue as String) as T
            Boolean::class -> prefs.getBoolean(key, defValue as Boolean) as T
            else -> null
        }
    }

    /**
     * 清除所有
     */
    fun clear() {
        prefs.edit { clear() }
    }

    fun setItem(key: String, value: String?) {
        prefs.edit {
            if (value == null) remove(key) else putString(key, value)
        }
    }

    fun setItem(key: String, value: Int?) {
        prefs.edit {
            if (value == null) remove(key) else putInt(key, value)
        }
    }

    fun setItem(key: String, value: Long?) {
        prefs.edit {
            if (value == null) remove(key) else putLong(key, value)
        }
    }

    fun setItem(key: String, value: Float?) {
        prefs.edit {
            if (value == null) remove(key) else putFloat(key, value)
        }
    }

    fun setItem(key: String, value: Boolean?) {
        prefs.edit {
            if (value == null) remove(key) else putBoolean(key, value)
        }
    }

}