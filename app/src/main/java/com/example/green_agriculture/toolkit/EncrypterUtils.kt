package com.example.green_agriculture.toolkit

import java.security.MessageDigest
import kotlin.io.encoding.Base64

object EncrypterUtils {
    fun encrypt(input: String): String {
        val instance = MessageDigest.getInstance("SHA-512")
        val bytes = instance.digest(input.toByteArray())
        return Base64.encode(bytes)
    }
}