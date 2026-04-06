package com.example.green_agriculture.toolkit

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.green_agriculture.GAApplication

/**
 * 振动工具
 */
object VibrateUtils {
    private val vibrator: Vibrator

    init {
        val vibratorManager =
            GAApplication.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator = vibratorManager.defaultVibrator
    }

    fun oneShot() {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}