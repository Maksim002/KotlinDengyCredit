package com.example.kotlincashloan.ui.registration.recovery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincashloan.R
import kotlinx.android.synthetic.main.activity_password_recovery.*

class ContactingServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacting_service)
        initToolBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initToolBar() {
        setSupportActionBar(password_recovery_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Обращение в службу поддержки"
    }
}