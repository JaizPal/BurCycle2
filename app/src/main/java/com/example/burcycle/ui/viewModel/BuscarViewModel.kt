package com.example.burcycle.ui.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burcycle.ui.model.Parking
import com.example.burcycle.ui.model.ParkingBD
import com.google.android.gms.common.util.Strings
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BuscarViewModel @Inject constructor() : ViewModel() {

    val parkings = MutableLiveData<List<Parking>>()
    val parkingsCercanos = MutableLiveData<List<Parking>>()
    var parkingsCercanosCargados = MutableLiveData<Boolean>()
    var parkingsCargados = MutableLiveData<Boolean>()

    private lateinit var database: FirebaseDatabase

    private lateinit var geocoder: Geocoder
//    val geocoder = Geocoder(context, Locale.US)

    fun onCreate(context: Context) {
        geocoder = Geocoder(context, Locale.US)
        database =
            Firebase.database("https://burcycle-default-rtdb.europe-west1.firebasedatabase.app/")
        parkings.value = ArrayList()
        parkingsCercanos.value = ArrayList()
        parkingsCercanosCargados. value = false
        parkingsCargados.value = false
        cargarParkings()

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
                            it.id,
                            (LatLng(it.lat, it.lon)),
                            it.tags.capacity,
                            "",
                            0f
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
        parkings.value?.forEach { parking -> establecerDistancia(latLng, parking) }
        parkings.value = parkings.value?.sortedBy { it.distancia }

        parkingsCercanos.value = parkings.value?.take(5)
        parkingsCercanos.value?.forEach { parking -> setDireccion(latLng, parking) }
        parkingsCercanosCargados.postValue(true)
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

    private fun setDireccion(latlng: LatLng, parking: Parking) {
        geocoder.getFromLocation(
            latlng.latitude,
            latlng.longitude,
            10,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if(addresses.size > 0) {
                        addresses.forEach { address ->
                            if(address.thoroughfare != null && address.thoroughfare.isNotBlank()) {
                                parking.direccion = "${addresses[0].thoroughfare}  ${addresses[0].subThoroughfare}"
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


}