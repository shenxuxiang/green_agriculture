package com.example.green_agriculture.http

import androidx.navigation.NavOptions
import com.example.green_agriculture.R
import com.example.green_agriculture.entity.ResponseData
import com.example.green_agriculture.toolkit.LogUtils
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.Toast
import com.example.green_agriculture.toolkit.TokenManager
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Call<T>.await() = suspendCoroutine { continuation ->
    enqueue(object : retrofit2.Callback<T> {
        override fun onResponse(call: Call<T?>, response: Response<T?>) {
            if (response.isSuccessful) {
                var message = ""
                val responseData = response.body()

                if (responseData is ResponseData<*>) {
                    val code = responseData.code
                    when (code) {
                        0 -> continuation.resume(responseData)
                        401 -> {
                            TokenManager.token = null
                            message = "用户登录凭证已失效"
                            HttpToolkit.cancelAllPendingRequest()
                            continuation.resumeWithException(RuntimeException(message))
                            // 跳转到登录页
                            Navigator.navigate(
                                resId = R.id.login_nav_graph,
                                navOptions = NavOptions.Builder().run {
                                    setLaunchSingleTop(true)
                                    setPopUpTo(
                                        destinationId = R.id.mainFragment,
                                        inclusive = true,
                                        saveState = false,
                                    )
                                    build()
                                }
                            )
                        }

                        else -> {
                            message = responseData.message
                            continuation.resumeWithException(RuntimeException(message))
                        }
                    }
                } else {
                    continuation.resume(responseData)
                }

                if (message.isNotEmpty()) Toast.showWarn(message)
            } else {
                val httpException = HttpException(response)
                continuation.resumeWithException(httpException)
                Toast.showWarn(HttpToolkit.getBadResponseMsg(httpException))
            }
        }

        override fun onFailure(call: Call<T?>, t: Throwable) {
            if (call.isCanceled) {
                LogUtils.d("请求已被取消")
            } else {
                val message = when (t) {
                    is ConnectException -> "网络异常"
                    is SocketTimeoutException -> "请求超时"
                    else -> t.message ?: "未知异常"
                }
                LogUtils.d(t)
                Toast.showWarn(message)
            }

            continuation.resumeWithException(t)
        }
    })
}