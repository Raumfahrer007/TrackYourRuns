package com.example.trackyourruns

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.Room
import com.example.trackyourruns.database.DatabaseRoute
import com.example.trackyourruns.database.DatabaseRun
import com.example.trackyourruns.databinding.ActivityMainBinding

/*
* Main Activity - initializes values in ViewModel and sets up Navigation
* */
class MainActivity : AppCompatActivity() {

    /*
    * Binding and ViewModel
    * */
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TyrViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration   // configuration for multiple top-level-destinations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        * Navigation
        * */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Top-level-destinations
        val topLevelDestinations = setOf(
            R.id.fragmentStartFrame,
            R.id.fragmentSelectRouteToCompare,
            R.id.fragmentStatistics
        )
        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(        // BottomNavigation
            binding.bottomNavigationViewMenu,
            navHostFragment.navController
        )

        /*
        * Set up databases for ViewModel
        * */
        val dbRun = Room.databaseBuilder(
            applicationContext,
            DatabaseRun::class.java, "database-run"
        ).build()

        val dbRoute = Room.databaseBuilder(
            applicationContext,
            DatabaseRoute::class.java, "database-route"
        ).build()

        viewModel.dbRun = dbRun
        viewModel.daoRun = dbRun.daoRun()
        viewModel.dbRoute = dbRoute
        viewModel.daoRoute = dbRoute.daoRoute()

        viewModel.resources = resources
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}