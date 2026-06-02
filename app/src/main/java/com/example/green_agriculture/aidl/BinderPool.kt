package com.example.green_agriculture.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import com.example.green_agriculture.toolkit.LogUtils
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

interface BinderPoolConnectListener {
    fun onConnected(): Unit
    fun onConnectFail(): Unit
}

enum class BinderPoolConnectState {
    IDLE, IN_PROGRESS, SUCCEED, FAILED
}

class BinderPool(private val context: Context) {
    @Volatile
    private var mBinderPool: IBinderPool? = null
    private var mConnectStatus = BinderPoolConnectState.IDLE
    private var mServiceConnection: ServiceConnection? = null
    private var mDeathRecipient: IBinder.DeathRecipient? = null

    companion object {
        @Volatile
        private var sInstance: BinderPool? = null

        fun getInstance(context: Context): BinderPool {
            return sInstance ?: synchronized(this) {
                BinderPool(context.applicationContext).also { sInstance = it }
            }
        }
    }

    fun connect(timeoutMils: Long = 3000L, listener: BinderPoolConnectListener? = null) {
        when (mConnectStatus) {
            BinderPoolConnectState.SUCCEED -> {
                listener?.onConnected()
                return
            }

            BinderPoolConnectState.IN_PROGRESS, BinderPoolConnectState.FAILED -> {
                LogUtils.d("BinderPool is already binding, skip")
                listener?.onConnectFail()
                return
            }

            BinderPoolConnectState.IDLE -> {
                connectBinderPoolService(timeoutMils, listener)
            }
        }
    }

    private fun connectBinderPoolService(
        timeoutMils: Long = 3000L,
        connectListener: BinderPoolConnectListener? = null,
    ) {
        // 这是一个倒计时锁，
        val latch = CountDownLatch(1)
        val intent = Intent(context, BinderPoolService::class.java)
        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                try {
                    mDeathRecipient = IBinder.DeathRecipient {
                        LogUtils.d("BinderPool service died, try to reconnect")
                        handleServiceDeath()
                    }

                    // 注册死亡代理
                    service?.linkToDeath(mDeathRecipient!!, 0)

                    mConnectStatus = BinderPoolConnectState.SUCCEED
                    mBinderPool = IBinderPool.Stub.asInterface(service)

                    // 执行一次倒计时，此时倒计时锁归零。
                    latch.countDown()
                    connectListener?.onConnected()
                } catch (e: RemoteException) {
                    LogUtils.d(e)
                    mConnectStatus = BinderPoolConnectState.FAILED
                    connectListener?.onConnectFail()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                LogUtils.d("BinderPool onServiceDisconnected")
                handleServiceDeath()
            }
        }

        val bindSuccess =
            context.bindService(intent, mServiceConnection!!, Context.BIND_AUTO_CREATE)

        if (!bindSuccess) {
            LogUtils.d("bindService failed")
            mConnectStatus = BinderPoolConnectState.FAILED
            connectListener?.onConnectFail()
            return
        }
        try {
            // 阻塞当前线程，直到倒计时锁归零或者超时，超时返回 false、正常返回 true
            val success = latch.await(timeoutMils, TimeUnit.MILLISECONDS)
            if (!success) {
                LogUtils.d("bindPool connect timeout after ${timeoutMils}ms")
                // 当前 bindService 超时，所以需要清理绑定
                context.unbindService(mServiceConnection!!)
                mConnectStatus = BinderPoolConnectState.FAILED
                connectListener?.onConnectFail()
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            connectListener?.onConnectFail()
        }
    }

    private fun handleServiceDeath() {
        synchronized(this) {
            (mBinderPool as? IBinder)?.unlinkToDeath(mDeathRecipient!!, 0)

            mBinderPool = null
            mDeathRecipient = null
            mConnectStatus = BinderPoolConnectState.FAILED

            // 死亡代理已触发，需要重新绑定
            // 先解绑旧的 ServiceConnection

            mServiceConnection?.let {
                try {
                    context.unbindService(it)
                } catch (e: IllegalArgumentException) {
                    // 可能已经解绑过
                }
                mServiceConnection = null
            }

            Handler(context.mainLooper) {
                connect()
                true
            }
        }
    }

    fun queryBinder(binderCode: Int): IBinder? {
        if (mConnectStatus != BinderPoolConnectState.SUCCEED || mBinderPool == null) return null

        return try {
            mBinderPool?.queryBinder(binderCode)
        } catch (e: RemoteException) {
            LogUtils.d(e)
            null
        }
    }
}