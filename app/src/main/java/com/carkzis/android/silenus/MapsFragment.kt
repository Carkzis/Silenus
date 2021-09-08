package com.carkzis.android.silenus

import android.app.AlertDialog
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    val args : MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // TODO: Check this, I have changed this and it needs testing.
        mapReasonListener(map)
    }

    // TODO: This allows us to reuse the map depending on the source, may be a bad idea!
    private fun mapReasonListener(map: GoogleMap) {
        sharedViewModel.mapReason.observe(viewLifecycleOwner, {
            when (it) {
                MapReason.ADDREV -> setUpLocationRequest(map)
                MapReason.VIEWREV -> setUpReviewLocation(map)
                else -> Timber.e("No map reason supplied.")
            }
        })
    }

    /**
     * This will home in onto the bar location on the map.
     */
    private fun setUpReviewLocation(map: GoogleMap) {
        Timber.e("Got here!") // And so it did!
        val latLng = args.yourReviewsMapLocation
        val lat = latLng!![0]
        val lng = latLng[1]
        Timber.e(lat)
        Timber.e(lng)
        // This may be better using viewModels tbh. Do not like safeArgs.
    }

    private fun setUpLocationRequest(map: GoogleMap) {
        map.setOnMapLongClickListener { latitudeLongitude ->
            // Add a marker to the map.
            val marker = map.addMarker(MarkerOptions().position(latitudeLongitude))
            // Set up the dialog box to allow the user to confirm their selection.
            val builder = AlertDialog.Builder(view?.context)
            builder.setTitle(R.string.location_selected)
            builder.setMessage(R.string.this_place_query)
            builder.setPositiveButton(R.string.yes) {_, _ ->
                sharedViewModel.setGeopoint(latitudeLongitude)
                findNavController().navigate(
                    MapsFragmentDirections.actionMapsFragmentToAddReviewFragment())
                marker?.remove()
            }
            builder.setNegativeButton(R.string.no) {_, _ ->
                marker?.remove()
            }
            builder.show()
        }
    }
}