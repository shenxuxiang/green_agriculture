package com.example.green_agriculture.components

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.CalculateUtils

class AlertWidget : DialogFragment() {
    private val ratio = 0.4f
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(requireContext()).run {
            setView(R.layout.alert_widget)
            create()
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setGravity(Gravity.TOP)
            val distY =
                requireContext().resources.displayMetrics.heightPixels - alertH - CalculateUtils.statusBarHeight
            attributes.y = (distY * ratio).toInt()
            setAttributes(attributes)
        }
    }


    private val alertH: Int
        get() {
            val myView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_widget, null)
            myView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            return myView.measuredHeight
        }

    companion object {
        private const val TAG = "alert_widget_###"
        fun createInstance(): AlertWidget {
            return AlertWidget()
        }

        fun show(fragmentManager: FragmentManager) {
            createInstance().apply {
                show(fragmentManager, TAG)
            }
        }
    }
}