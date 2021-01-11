package com.example.kotlincashloan.ui.still

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences

class ExitDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(activity, R.style.CustomDialogTheme)
            builder
                .setCancelable(false)
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да") { dialog, id ->
                    val intent = Intent(context, HomeActivity::class.java)
                    AppPreferences.isPinCode = false
                    AppPreferences.token = ""
                    MainActivity.timer.timeStop()
                    startActivity(intent)
                    dialog.cancel()
                }
                .setNegativeButton("Нет"){ dialog, id ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
