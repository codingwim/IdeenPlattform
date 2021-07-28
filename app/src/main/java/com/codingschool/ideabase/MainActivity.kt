package com.codingschool.ideabase

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.codingschool.ideabase.databinding.ActivityMainBinding
import com.codingschool.ideabase.utils.Preferences
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val prefs: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val locale = Locale.getDefault()
        prefs.setLocale(locale.toString())*/
        Log.d("observer_ex", "main activity locale: and prefs: ${prefs.getLocale()}")
        // TODO mianactivity binding
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val actionBar = this.supportActionBar

        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_top_ranked, R.id.navigation_all_ideas, R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.commentFragment -> hideAppBarAndBottomNaviagtionBar(
                    navView,
                    actionBar
                )
                /*R.id.detailFragment -> actionBar?.hide()*/
                else -> showAppBarAndBottomNaviagtionBar(navView, actionBar)
            }
        }
    }


    private fun showAppBarAndBottomNaviagtionBar(
        navView: BottomNavigationView,
        actionBar: ActionBar?
    ) {
        actionBar?.show()
        navView.visibility = View.VISIBLE
    }

    private fun hideAppBarAndBottomNaviagtionBar(
        navView: BottomNavigationView,
        actionBar: ActionBar?
    ) {
        actionBar?.hide()
        navView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}