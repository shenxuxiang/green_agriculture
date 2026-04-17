package com.example.green_agriculture

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.green_agriculture.toolkit.CalculateUtils
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.initialize(this)

        window.navigationBarColor = Color.BLACK
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).also {
            it.isAppearanceLightStatusBars = true
        }

        // 初始化 Navigator
        val nav = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        Navigator.initialize(nav as NavHostFragment)

        val rootView = findViewById<CoordinatorLayout>(R.id.root_view)
        rootView.setPadding(0, 0, 0, CalculateUtils.navigationBarHeight.toInt())
    }
}