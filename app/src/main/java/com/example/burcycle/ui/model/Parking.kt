package com.example.burcycle.ui.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Parking(
    val id: Long,
    val latLng: LatLng,
    val capacidad: String,
    var direccion: String,
    var distancia: Float
) : ClusterItem {
    private val position: LatLng = latLng
    private val title: String = capacidad
    private val snippet: String = capacidad
    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return "Capacidad: $title"
    }

    override fun getSnippet(): String {
        return ""
    }

}

// TODO Igual hay que meter :Parceable

