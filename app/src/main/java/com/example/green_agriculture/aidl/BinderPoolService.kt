package com.example.green_agriculture.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.SparseArray

class BinderPoolService : Service() {
    companion object {
        private val sBinderMap = SparseArray<IBinder>().apply {
            put(0, BookManagerImpl())
        }
    }

    private val mBinderPool = object : IBinderPool.Stub() {
        override fun queryBinder(binderCode: Int): IBinder? {
            return sBinderMap.get(binderCode)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinderPool
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}