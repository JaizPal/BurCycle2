package com.example.burcycle.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.burcycle.R
import com.example.burcycle.databinding.FragmentFirstBinding
import com.example.burcycle.ui.viewModel.BuscarViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: AutocompleteSupportFragment

    private val buscarViewModel: BuscarViewModel by viewModels()

    private val tvDireccionesCercanas: ArrayList<TextView> = ArrayList()
    private val tvCapacidadesCercanas: ArrayList<TextView> = ArrayList()
    private val materialCardsCercanos: ArrayList<MaterialCardView> = ArrayList()

    private val tvDireccionesRecientes: ArrayList<TextView> = ArrayList()
    private val tvCapacidadesRecientes: ArrayList<TextView> = ArrayList()
    private val materialCardsRecientes: ArrayList<MaterialCardView> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        tvDireccionesCercanas.add(binding.tvParking1Direccion)
        tvDireccionesCercanas.add(binding.tvParking2Direccion)
        tvDireccionesCercanas.add(binding.tvParking3Direccion)
        tvDireccionesCercanas.add(binding.tvParking4Direccion)
        tvDireccionesCercanas.add(binding.tvParking5Direccion)

        tvCapacidadesCercanas.add(binding.tvParking1Capacidad)
        tvCapacidadesCercanas.add(binding.tvParking2Capacidad)
        tvCapacidadesCercanas.add(binding.tvParking3Capacidad)
        tvCapacidadesCercanas.add(binding.tvParking4Capacidad)
        tvCapacidadesCercanas.add(binding.tvParking5Capacidad)

        materialCardsCercanos.add(binding.card1)
        materialCardsCercanos.add(binding.card2)
        materialCardsCercanos.add(binding.card3)
        materialCardsCercanos.add(binding.card4)
        materialCardsCercanos.add(binding.card5)


        tvDireccionesRecientes.add(binding.tvParkingReciente1Direccion)
        tvDireccionesRecientes.add(binding.tvParkingReciente2Direccion)
        tvDireccionesRecientes.add(binding.tvParkingReciente3Direccion)
        tvDireccionesRecientes.add(binding.tvParkingReciente4Direccion)
        tvDireccionesRecientes.add(binding.tvParkingReciente5Direccion)

        tvCapacidadesRecientes.add(binding.tvParkingReciente1Capacidad)
        tvCapacidadesRecientes.add(binding.tvParkingReciente2Capacidad)
        tvCapacidadesRecientes.add(binding.tvParkingReciente3Capacidad)
        tvCapacidadesRecientes.add(binding.tvParkingReciente4Capacidad)
        tvCapacidadesRecientes.add(binding.tvParkingReciente5Capacidad)

        materialCardsRecientes.add(binding.cardReciente1)
        materialCardsRecientes.add(binding.cardReciente2)
        materialCardsRecientes.add(binding.cardReciente3)
        materialCardsRecientes.add(binding.cardReciente4)
        materialCardsRecientes.add(binding.cardReciente5)

        buscarViewModel.onCreate(requireContext())

        buscarViewModel.cargarRecientes(requireContext())

        buscarViewModel.parkingsRecientesCargados.observe(requireActivity()) {
            if (it) {
                buscarViewModel.parkingsRecientes.value?.forEachIndexed { i, parking ->
                    materialCardsRecientes[i].visibility = View.VISIBLE
                    tvDireccionesRecientes[i].text = parking.direccion
                    tvCapacidadesRecientes[i].text = parking.capacidad

                }
            }
        }


        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.maps_api_key), Locale.US);
        }
        binding.linearLayoutCercanos.visibility = View.GONE
        searchView =
            requireActivity().supportFragmentManager.findFragmentById(R.id.searchView) as AutocompleteSupportFragment
        searchView.setCountries(listOf("ES"))
        searchView.setHint("Introduce la calle")
        searchView.setLocationRestriction(
            RectangularBounds.newInstance(
                LatLngBounds(
                    LatLng(42.32, -3.78),  // SW bounds
                    LatLng(42.39, -3.61) // NE bounds
                )
            )
        )
        searchView.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            .setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    buscarViewModel.setParkingCercanos(place.latLng!!)
                }

                override fun onError(status: Status) {
                    Log.i("TAG", "An error occurred: $status")
                }
            })

        buscarViewModel.parkingsCercanosCargados.observe(requireActivity()) {
            if (it) {
                for (i in 0..4) {
                    tvDireccionesCercanas[i].text =
                        buscarViewModel.parkingsCercanos.value?.get(i)?.direccion
                    tvCapacidadesCercanas[i].text =
                        "Plazas: ${buscarViewModel.parkingsCercanos.value?.get(i)?.capacidad}"
                }
                binding.linearLayoutCercanos.visibility = View.VISIBLE
            }
        }

        materialCardsCercanos.forEach { card ->
            card.setOnClickListener {
                checkCard(card)
            }
        }

        buscarViewModel.cardChecked.observe(requireActivity()) { card ->
            binding.bIr.isEnabled = card != null
        }

        binding.bIr.setOnClickListener {
            if (buscarViewModel.parkingElegido.value != null) {
                buscarViewModel.guardarReciente(requireContext())
                val gmmIntentUri = Uri.parse(
                    "google.navigation:q=${buscarViewModel.parkingElegido.value?.latLng?.latitude}"
                            + ",${buscarViewModel.parkingElegido.value?.latLng?.longitude}&mode=b"
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        return binding.root

    }

    private fun checkCard(card: MaterialCardView) {
        buscarViewModel.cardChecked.value = card
        materialCardsCercanos.forEachIndexed { i, c ->
            if (c == buscarViewModel.cardChecked.value) {
                card.toggle()
                buscarViewModel.cardChecked.value = if (card.isChecked) {
                    buscarViewModel.parkingElegido.value =
                        buscarViewModel.parkingsCercanos.value?.get(i)
                    card
                } else {
                    buscarViewModel.parkingElegido.value = null
                    null
                }
            } else {
                c.isChecked = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setText(null)
        buscarViewModel.parkingsCercanosCargados.value = false
        buscarViewModel.parkingsCercanos.value = ArrayList()
        buscarViewModel.parkingsCargados.value = false

        _binding = null
    }

}