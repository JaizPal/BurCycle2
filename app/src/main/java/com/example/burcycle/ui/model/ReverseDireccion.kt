package com.example.burcycle.ui.model

class ReverseDireccion {
    val features: ArrayList<Features> = ArrayList()
}

class Features {
    val properties: Properties = Properties()
}

class Properties {
    val geocoding: Geocoding = Geocoding()
}

class Geocoding {
    var label: String = ""
}