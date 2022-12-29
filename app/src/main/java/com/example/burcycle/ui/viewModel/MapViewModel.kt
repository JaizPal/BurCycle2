package com.example.burcycle.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burcycle.ui.model.Parking
import com.example.burcycle.ui.model.ParkingBD
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    val parkings = MutableLiveData<ArrayList<Parking>>()
    var parkingsCargados = MutableLiveData<Boolean>()
    var parkingPulsado = MutableLiveData<Parking>()
    private lateinit var database: FirebaseDatabase

    fun onCreate() {
        database =
            Firebase.database("https://burcycle-default-rtdb.europe-west1.firebasedatabase.app/")
        parkings.value = ArrayList()
        cargarParkings()
        parkingsCargados.value = false
        parkingPulsado.value = Parking(1, LatLng(1.0, 1.0), "1")
    }


    private fun cargarParkings() {
        var pp: List<ParkingBD>
        val parkingsBD = ArrayList<Parking>()
        database.getReference("elements")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pp = task.result.getValue<List<ParkingBD>>()!!
                    pp.forEach {
                        parkingsBD.add(Parking(it.id, (LatLng(it.lat, it.lon)), it.tags.capacity))
                    }
                }
                parkings.value = parkingsBD
                parkingsCargados.postValue(true)
            }.addOnFailureListener {
                val m = it.message
            }

    }

}