package com.example.kotlinscreenscanner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
//    private val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        MainActivity.alert = LoadingAlert(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_loans,
                R.id.navigation_profile,
                R.id.navigation_notification,
                R.id.navigation_support,
                R.id.navigation_still
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

//    override fun onBackPressed() {
//        if (nav_host_fragment.)
//        super.onBackPressed()
//    }
}