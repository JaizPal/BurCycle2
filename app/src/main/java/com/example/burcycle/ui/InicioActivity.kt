package com.example.burcycle.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.burcycle.R
import com.example.burcycle.databinding.ActivityInicioBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InicioActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        val navController = findNavController(R.id.nav_host_fragment_content_inicio)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val searchView = supportFragmentManager.findFragmentById(R.id.searchView) as AutocompleteSupportFragment
            searchView.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
                .setOnPlaceSelectedListener(object : PlaceSelectionListener {
                    override fun onPlaceSelected(place: Place) {
                        // TODO: Get info about the selected place.
                        Log.i("TAG", "Place: ${place.name}, ${place.id}")
                    }

                    override fun onError(status: Status) {
                        // TODO: Handle the error.
                        Log.i("TAG", "An error occurred: $status")
                    }
                })
//            binding.searchView.isVisible = false
//            supportActionBar?.title = null
//            when (destination.id) {
//                R.id.FirstFragment -> {
//                    binding.searchView.isVisible = true
//                    binding.searchView.requestFocus()
//                }
//                else -> {
//                    binding.searchView.isVisible = false
//                }
//            }
        }
        val bottomNavigation = binding.bottomNavigation
        bottomNavigation.setOnItemSelectedListener {
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


    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_inicio)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}