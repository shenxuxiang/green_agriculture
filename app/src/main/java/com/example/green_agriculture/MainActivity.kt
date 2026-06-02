package com.example.green_agriculture

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.green_agriculture.aidl.BinderPool
import com.example.green_agriculture.aidl.BinderPoolConnectListener
import com.example.green_agriculture.aidl.Book
import com.example.green_agriculture.aidl.IBookManager
import com.example.green_agriculture.aidl.IOnNewBookArrivedListener
import com.example.green_agriculture.pages.main.MainViewModel
import com.example.green_agriculture.toolkit.CalculateUtils
import com.example.green_agriculture.toolkit.LogUtils
import com.example.green_agriculture.toolkit.Navigator
import com.example.green_agriculture.toolkit.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        lifecycleScope.launch(Dispatchers.IO) {
            val binderPool = BinderPool.getInstance(this@MainActivity)
            LogUtils.d("=============binderPool: $binderPool")
            binderPool.connect(3000, object : BinderPoolConnectListener {
                override fun onConnected() {
                    val bookManager = IBookManager.Stub.asInterface(binderPool.queryBinder(0))
                    LogUtils.d("=============bookManager.bookList: ${bookManager.bookList}")

                    bookManager.register(onNewBookListener)
                }

                override fun onConnectFail() {
                    LogUtils.d("=====================连接成功")
                }
            })

        }
    }

    private val MESSAGE_NEW_BOOK_ARRIVED = 1

    private val handler = Handler(
        Looper.getMainLooper(),
        object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                if (msg.what == MESSAGE_NEW_BOOK_ARRIVED) {
                    LogUtils.d("======================receive new book:${msg.obj}")
                    return true
                } else {
                    return true
                }
            }
        }
    )

    private val onNewBookListener = object : IOnNewBookArrivedListener.Stub() {
        override fun onNewBookArrived(book: Book?) {
            LogUtils.d("======================receive new book")
            handler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget()
        }
    }
}