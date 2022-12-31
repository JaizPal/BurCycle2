package com.example.burcycle.ui

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
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: AutocompleteSupportFragment

    private val buscarViewModel: BuscarViewModel by viewModels()
    private val tvDirecciones: ArrayList<TextView> = ArrayList()
    private val tvCapacidades: ArrayList<TextView> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        tvDirecciones.add(binding.tvParking1Direccion)
        tvDirecciones.add(binding.tvParking2Direccion)
        tvDirecciones.add(binding.tvParking3Direccion)
        tvDirecciones.add(binding.tvParking4Direccion)
        tvDirecciones.add(binding.tvParking5Direccion)

        tvCapacidades.add(binding.tvParking1Capacidad)
        tvCapacidades.add(binding.tvParking2Capacidad)
        tvCapacidades.add(binding.tvParking3Capacidad)
        tvCapacidades.add(binding.tvParking4Capacidad)
        tvCapacidades.add(binding.tvParking5Capacidad)

        buscarViewModel.onCreate(requireContext())
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
                    // TODO: Handle the error.
                    Log.i("TAG", "An error occurred: $status")
                }
            })

        buscarViewModel.parkingsCercanosCargados.observe(requireActivity()) {
            if (it) {
                for (i in 0..4) {
                    tvDirecciones[i].text =
                        buscarViewModel.parkingsCercanos.value?.get(i)?.direccion
                    tvCapacidades[i].text = "Capacidad: ${buscarViewModel.parkingsCercanos.value?.get(i)?.capacidad}"
                }
                binding.linearLayoutCercanos.visibility = View.VISIBLE
            }
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setText(null)
        _binding = null
    }

}