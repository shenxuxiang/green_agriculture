package com.example.green_agriculture.http

import retrofit2.HttpException

object HttpToolkit {
    fun cancelAllPendingRequest() {
        val dispatcher = HttpRequest.client.dispatcher

        val queuedCalls = dispatcher.queuedCalls()

        for (call in queuedCalls) {
            call.cancel()
        }

        val runningCalls = dispatcher.runningCalls()
        for (call in runningCalls) {
            call.cancel()
        }
    }

    /**
     * 获取异常 HTTP 请求的响应提示
     * */
    fun getBadResponseMsg(httpException: HttpException): String {
        val code = httpException.code()
        return when (code) {
            400 -> "请求语法错误"
            403 -> "禁止访问"
            404 -> "找不到资源"
            405 -> "请求方法错误"
            500, 502, 503 -> "服务器异常"
            505 -> "不支持该协议"
            else -> "未知异常";
        }
    }
}