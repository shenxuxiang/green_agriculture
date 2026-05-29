package com.example.green_agriculture.toolkit

object PatternUtils {
    val phonePattern = """^1[3456789][0-9]{9}$""".toRegex()

    val passwordPattern =
        """^(?=.*\d+)(?=.*[a-zA-Z]+)(?=.*[~!@#$%^&*\.]+)[~!@#$%^&*\.0-9a-zA-Z]{6,18}""".toRegex()

    val phoneCodePattern = """^\d{6}$""".toRegex()
}