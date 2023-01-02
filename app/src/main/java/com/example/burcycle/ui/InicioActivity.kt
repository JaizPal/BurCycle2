package com.example.burcycle.ui


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.burcycle.R
import com.example.burcycle.databinding.ActivityInicioBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InicioActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInicioBinding
    companion object {
        var lastFragment = R.id.item_buscar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        val navController = findNavController(R.id.nav_host_fragment_content_inicio)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_inicio) as NavHostFragment
        val navControllerBottomNavigation = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navControllerBottomNavigation)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            val layoutSearchView = binding.layoutSearchView
            supportActionBar?.title = null
            when (destination.id) {
                R.id.FirstFragment -> {
                    layoutSearchView.visibility = View.VISIBLE
                }
                R.id.SecondFragment -> {
                    layoutSearchView.visibility = View.GONE
                }
            }
        }

        val bottomNavigation = binding.bottomNavigation
        bottomNavigation.setOnItemSelectedListener {
            lastFragment = binding.bottomNavigation.selectedItemId
            when (it.itemId) {
                R.id.item_buscar -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_inicio)
                        .navigate(R.id.FirstFragment)
                    true
                }
                R.id.item_mapa -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_inicio)
                        .navigate(R.id.SecondFragment)
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnItemReselectedListener {
            // DO NOTHING
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_inicio)
//        binding.bottomNavigation.selectedItemId = lastFragment
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}