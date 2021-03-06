package com.codingschool.ideabase

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.codingschool.ideabase.utils.Preferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val prefs: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs.setAppJustStarted()
        prefs.setLocale(Locale.getDefault().language)
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
                R.id.loginFragment, R.id.commentFragment, R.id.loadingFragment -> {
                    actionBar?.hide()
                    navView.visibility = View.GONE
                }
                R.id.registerFragment -> {
                    actionBar?.show()
                    navView.visibility = View.GONE
                }
                else -> {
                    actionBar?.show()
                    navView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}