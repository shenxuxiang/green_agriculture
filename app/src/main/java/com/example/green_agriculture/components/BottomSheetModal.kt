package com.example.green_agriculture.components

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.LayoutBottomSheetModalBinding

class BottomSheetModal : DialogFragment() {
    lateinit var binding: LayoutBottomSheetModalBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = LayoutBottomSheetModalBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertWidgetDialogTheme).create()

        dialog.setView(binding.root)

        dialog.window?.apply {
            attributes.y = 0
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomSheetMadalAnimation)
        }

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        return dialog
    }
}