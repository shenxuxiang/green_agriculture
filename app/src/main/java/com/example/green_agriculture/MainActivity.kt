package com.example.green_agriculture

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.CalculateUtils
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.initialize(this)

        /**
         * 边到边，沉浸式布局沉浸式布局
         */
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(0xFF000000.toInt()))
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = false

        // 初始化 Navigator
        val nav = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        Navigator.initialize(nav as NavHostFragment)

        val rootView = findViewById<CoordinatorLayout>(R.id.root_view)
        rootView.setPadding(0, 0, 0, CalculateUtils.navigationBarHeight.toInt())

        // 加载 regionData
        mainViewModel.loadRegionData()
    }
}