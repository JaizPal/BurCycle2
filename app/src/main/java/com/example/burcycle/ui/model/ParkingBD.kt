package com.example.burcycle.ui.model

data class ParkingBD(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: TagsBD
) {
    constructor() : this(
        0, 0.0, 0.0, TagsBD("")
    )
}