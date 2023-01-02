package com.example.burcycle.ui.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burcycle.ui.model.Parking
import com.example.burcycle.ui.model.ParkingBD
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class BuscarViewModel @Inject constructor() : ViewModel() {

    val parkings = MutableLiveData<List<Parking>>()
    val parkingsCercanos = MutableLiveData<List<Parking>>()
    var parkingsCercanosCargados = MutableLiveData(false)
    var parkingsCargados = MutableLiveData<Boolean>()

    var cardChecked = MutableLiveData<MaterialCardView?>()
    var parkingElegido = MutableLiveData<Parking?>()

    var parkingsRecientes = MutableLiveData<ArrayList<Parking>>()
    var parkingsRecientesCargados = MutableLiveData(false)

    private lateinit var database: FirebaseDatabase

    private lateinit var geocoder: Geocoder

    fun onCreate(context: Context) {
        geocoder = Geocoder(context, Locale.US)
        database =
            Firebase.database("https://burcycle-default-rtdb.europe-west1.firebasedatabase.app/")
        parkings.value = ArrayList()
        parkingsCercanos.value = ArrayList()

        parkingsCargados.value = false
        cargarParkings()
        cardChecked.value = null
        parkingElegido.value = null

        parkingsRecientes.value = ArrayList()
        cargarRecientes(context)
    }

    private fun cargarParkings() {
        var pp: List<ParkingBD>
        val parkingsBD = ArrayList<Parking>()
        database.getReference("elements").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                pp = task.result.getValue<List<ParkingBD>>()!!
                pp.forEach {
                    parkingsBD.add(
                        Parking(
                            it.id, (LatLng(it.lat, it.lon)), it.tags.capacity, "", 0f
                        )
                    )
                }
            }
            parkings.value = parkingsBD
            parkingsCargados.postValue(true)
        }.addOnFailureListener {
            val m = it.message
        }
    }

    fun setParkingCercanos(latLng: LatLng) {
        viewModelScope.launch {
            parkings.value?.forEach { parking -> establecerDistancia(latLng, parking) }
            parkings.value = parkings.value?.sortedBy { it.distancia }

            parkingsCercanos.value = parkings.value?.take(5)
            parkingsCercanos.value?.forEach { parking -> setDireccion(parking) }
            delay(500)
            parkingsCercanosCargados.postValue(true)
        }

    }

    private fun establecerDistancia(direccion: LatLng, parking: Parking) {
        val distancia: FloatArray = floatArrayOf(0f)
        Location.distanceBetween(
            direccion.latitude,
            direccion.longitude,
            parking.latLng.latitude,
            parking.latLng.longitude,
            distancia
        )
        parking.distancia = distancia[0]
    }


    private fun setDireccion(parking: Parking) {
        geocoder.getFromLocation(parking.latLng.latitude,
            parking.latLng.longitude,
            10,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.size > 0) {
                        addresses.forEach { address ->
                            if (address.thoroughfare != null && address.thoroughfare.isNotBlank()) {
                                parking.direccion =
                                    "${addresses[0].thoroughfare}  ${addresses[0].subThoroughfare}"
                                return
                            }
                        }
                    }
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)

                }
            })
    }

    fun guardarReciente(context: Context) {

    }

    fun cargarRecientes(context: Context) {

    }


}