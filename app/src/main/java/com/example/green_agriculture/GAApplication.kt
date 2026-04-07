package com.example.green_agriculture

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.green_agriculture.pages.main.MainViewModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GAApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        private var _MainViewModel: MainViewModel? = null

        val mainViewModel: MainViewModel?
            get() = _MainViewModel

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}