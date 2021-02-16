package com.example.kotlinscreenscanner.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.android.navigationadvancedsample.ClickPushNotification
import com.example.android.navigationadvancedsample.setupWithNavController
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.TimerListener
import com.example.kotlincashloan.utils.TimerListenerLoan
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val handler = Handler()
    private var currentNavController: LiveData<NavController>? = null

    private lateinit var bottomNavigationView: BottomNavigationView

    companion object {
        lateinit var timer: TimerListener
        lateinit var alert: LoadingAlert
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Инцилизация Прилоудера
        alert = LoadingAlert(this)

        HomeActivity.alert = LoadingAlert(this)
        timer = TimerListener(this)
        GetLoanActivity.timer = TimerListenerLoan(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val notificationmanager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    @SuppressLint("ResourceType")
    private fun setupBottomNavigationBar() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(
            R.navigation.loans_navigation,
            R.navigation.notification_navigation,
            R.navigation.profile_navigation,
            R.navigation.support_navigation,
            R.navigation.still_navigation
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller


    }

//    private fun loadFragment(fragment: Fragment) {
//        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.nav_host_container, fragment)
//        ft.commit()
//    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onStart() {
        super.onStart()
        GetLoanActivity.timer.timeStop()
        // Я сменил "" на "null"
        if (AppPreferences.dataKey != "") {
            bottomNavigationView.ClickPushNotification()
        }
    }

    fun setTitle(title: String?, color: Int) {
        if (main_toolBar != null) {
            main_toolBar.setText(title)
            main_toolBar.setTextColor(color)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            timer.timeStop()
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        if (AppPreferences.token != "") {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                timer.timeStart()
                AppPreferences.isNumber = false
            }, 200)
        }
    }

    override fun onStop() {
        super.onStop()
//        if (AppPreferences.token != "") {
//            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                timer.timeStart()
//                AppPreferences.isNumber = false
//            }, 200)
//        }
    }
}