package com.example.green_agriculture.toolkit

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import com.example.green_agriculture.R

object Navigator {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var context: Context

    fun initialize(navHostFragment: NavHostFragment) {
        this.navHostFragment = navHostFragment

        navController = navHostFragment.navController
        context = navHostFragment.context!!
    }

    fun navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
    ) {
        val options = navOptions ?: NavOptions.Builder().run {
            setLaunchSingleTop(true)
            setEnterAnim(R.anim.enter_anim)
            setExitAnim(R.anim.exit_anim)
            setPopEnterAnim(R.anim.pop_enter_anim)
            setPopExitAnim(R.anim.pop_exit_anim)
            build()
        }

        navController.navigate(resId, args, options, navigatorExtras)
    }

    fun popBackStack(result: Bundle? = null): Boolean {
        if (!(result?.isEmpty ?: true)) {
            navController.previousBackStackEntry?.let {
                it.savedStateHandle["result"] = result
            }
        }

        return navController.popBackStack()
    }

    fun popBackStack(
        @IdRes destinationId: Int,
        inclusive: Boolean = false,
        saveState: Boolean = false,
        result: Bundle? = null,
    ): Boolean {
        if (!(result?.isEmpty ?: true)) {
            navController.previousBackStackEntry?.let {
                it.savedStateHandle["result"] = result
            }
        }

        return navController.popBackStack(destinationId, inclusive, saveState)
    }
}