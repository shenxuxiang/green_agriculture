package com.example.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DebugLog(val tag: String = "DEBUG")