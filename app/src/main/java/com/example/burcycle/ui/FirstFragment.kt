package com.example.burcycle.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.burcycle.R
import com.example.burcycle.databinding.ActivityInicioBinding
import com.example.burcycle.databinding.FragmentFirstBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.LocationRestriction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val perimetroMaximo = RectangularBounds.newInstance(
        LatLng(42.32, -3.78),  // SW bounds
        LatLng(42.39, -3.61) // NE bounds
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
//        val token = AutocompleteSessionToken.newInstance()
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.maps_api_key), Locale.US);
        }
//
//        val autoComplete = requireActivity().supportFragmentManager.findFragmentById(R.id.searchView) as AutocompleteSupportFragment
//        autoComplete.setCountries(listOf("ES", "BU"))
//
//        val request =
//            FindAutocompletePredictionsRequest.builder()
//                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(perimetroMaximo)
//                //.setLocationRestriction(bounds)
//                .setOrigin(LatLng(42.3502200, -3.6752700))
//                .setCountries("ES", "BU")
//                .setTypesFilter(listOf(TypeFilter.ADDRESS.toString()))
//                .setSessionToken(token)
//                .setQuery()
//                .build()
//        val placesClient = Places.createClient(context)
//        placesClient.findAutocompletePredictions(request)
//            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
//                for (prediction in response.autocompletePredictions) {
//                    Log.i("TAG", prediction.placeId)
//                    Log.i("TAG", prediction.getPrimaryText(null).toString())
//                }
//            }.addOnFailureListener { exception: Exception? ->
//                if (exception is ApiException) {
//                    Log.e("TAG", "Place not found: " + exception.statusCode)
//                }
//            }

        val searchView =
            requireActivity().supportFragmentManager.findFragmentById(R.id.searchView) as AutocompleteSupportFragment
        searchView.setCountries(listOf("ES"))
        searchView.setLocationRestriction(RectangularBounds.newInstance(LatLngBounds(
            LatLng(42.32, -3.78),  // SW bounds
            LatLng(42.39, -3.61) // NE bounds
        )))
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



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}