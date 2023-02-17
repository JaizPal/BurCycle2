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

@HiltViewModel
class BuscarViewModel @Inject constructor() : ViewModel() {

    private val parkings = MutableLiveData<List<Parking>>()
    val parkingsCercanos = MutableLiveData<ArrayList<Parking>>()
    var parkingsCercanosCargados = MutableLiveData(false)
    var parkingsCargados = MutableLiveData<Boolean>()

    var cardChecked = MutableLiveData<MaterialCardView?>()
    var parkingElegido = MutableLiveData<Parking?>()

    var parkingsRecientes = MutableLiveData<ArrayList<Parking>>()
    var parkingsRecientesCargados = MutableLiveData(false)

    var progressBar = MutableLiveData(false);

    var nParkings = MutableLiveData(0)
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
        nParkings.postValue(0)
        progressBar.value = true
        viewModelScope.launch {
            progressBar.value = true
            parkings.value?.forEach { parking -> establecerDistancia(latLng, parking) }
            parkings.value = parkings.value?.sortedBy { it.distancia }

            val cacheParkings: ArrayList<Parking>? = parkings.value?.take(5) as ArrayList<Parking>?
            cacheParkings?.forEach {
                parkingsCercanos.value?.add(it)
                setDireccion(it)
            }
            parkingsCercanos.value = cacheParkings
            progressBar.value = false
//            parkingsCercanos.value = parkings.value?.take(5)
//            parkingsCercanos.value?.forEach { parking -> setDireccion(parking) }
//            delay(1000)
//            parkingsCercanosCargados.postValue(true)
//            progressBar.value = false
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
        nParkings.value = nParkings.value?.plus(1)
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
                            } else {
                                setDireccion(parking)
                                return
                            }
                        }
                    }
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)

                }
            }
        )

    }

    fun guardarReciente(context: Context) {
        val parkingsGuardados = recuperarRecientes(context)
        parkingsGuardados.remove(parkingElegido.value)
        parkingsGuardados.add(0, parkingElegido.value!!)
        val sharedPreferences = context.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE).edit()
        parkingsGuardados.forEachIndexed { index, parking ->
            sharedPreferences.putString(
                "parking${index + 1}",
                "${parking.id}" +
                        "|${parking.latLng.latitude}" +
                        "|${parking.latLng.longitude}" +
                        "|${parking.capacidad}" +
                        "|${parking.direccion}" +
                        "|${parking.distancia}"
            )
        }
        sharedPreferences.apply()
    }

    private fun recuperarRecientes(context: Context): ArrayList<Parking> {
        // TODO devuelve null
        val sharedPreferencesParking1 =
            context.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE)!!
                .getString("parking1", null)
        val parkingsRecientesGuardados = ArrayList<Parking>()
        if (sharedPreferencesParking1 != null) {
            val variablesParking1 = sharedPreferencesParking1.split("|")
            parkingsRecientesGuardados.add(
                Parking(
                    variablesParking1[0].toLong(),
                    LatLng(variablesParking1[1].toDouble(), variablesParking1[2].toDouble()),
                    variablesParking1[3],
                    variablesParking1[4],
                    variablesParking1[5].toFloat()
                )
            )
        }
        val sharedPreferencesParking2 =
            context.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE)!!
                .getString("parking2", null)
        if (sharedPreferencesParking2 != null) {
            val variablesParking2 = sharedPreferencesParking2.split("|")
            parkingsRecientesGuardados.add(
                Parking(
                    variablesParking2[0].toLong(),
                    LatLng(variablesParking2[1].toDouble(), variablesParking2[2].toDouble()),
                    variablesParking2[3],
                    variablesParking2[4],
                    variablesParking2[5].toFloat()
                )
            )
        }
        val sharedPreferencesParking3 =
            context.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE)!!
                .getString("parking3", null)
        if (sharedPreferencesParking3 != null) {
            val variablesParking3 = sharedPreferencesParking3.split("|")
            parkingsRecientesGuardados.add(
                Parking(
                    variablesParking3[0].toLong(),
                    LatLng(variablesParking3[1].toDouble(), variablesParking3[2].toDouble()),
                    variablesParking3[3],
                    variablesParking3[4],
                    variablesParking3[5].toFloat()
                )
            )
        }
        val sharedPreferencesParking4 =
            context.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE)!!
                .getString("parking4", null)
        if (sharedPreferencesParking4 != null) {
            val variablesParking4 = sharedPreferencesParking4.split("|")
            parkingsRecientesGuardados.add(
                Parking(
                    variablesParking4[0].toLong(),
                    LatLng(variablesParking4[1].toDouble(), variablesParking4[2].toDouble()),
                    variablesParking4[3],
                    variablesParking4[4],
                    variablesParking4[5].toFloat()
                )
            )
        }

        val sharedPreferencesParking5 =
            context.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE)!!
                .getString("parking5", null)
        if (sharedPreferencesParking5 != null) {
            val variablesParking5 = sharedPreferencesParking5.split("|")
            parkingsRecientesGuardados.add(
                Parking(
                    variablesParking5[0].toLong(),
                    LatLng(variablesParking5[1].toDouble(), variablesParking5[2].toDouble()),
                    variablesParking5[3],
                    variablesParking5[4],
                    variablesParking5[5].toFloat()
                )
            )
        }
        return parkingsRecientesGuardados
    }

    fun cargarRecientes(context: Context) {
        parkingsRecientes.value = recuperarRecientes(context)
        parkingsRecientesCargados.value = true
    }

}