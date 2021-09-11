package com.carkzis.android.silenus

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.carkzis.android.silenus.data.MapReason
import com.carkzis.android.silenus.data.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    val args : MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO: See if we should remove this or not, needs Fragment activity.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
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
        val latLng = args.yourReviewsMapLocation
        val latlng = LatLng(latLng!![0].toDouble(), latLng[1].toDouble())
        val zoom = 10.0f
        zoomMe(map, zoom, latlng)
        // This may be better using viewModels tbh. Do not like safeArgs.
    }

    /**
     * This sets up the map for when getting a location when adding a review.
     * It has a listener to set a marker for where the reviewed establishment is.
     */
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

    /**
     * This zooms in on the requested position on the map.
     */
    private fun zoomMe(map: GoogleMap, zoom: Float, latLng: LatLng) {
        map.addMarker(MarkerOptions().position(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    /**
     * Get current position.
     */
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(zoom: Float) {
        if (!locationPermissionsApproved()) {
            requestLocationPermissions()
        }
        val location = fusedLocationClient.lastLocation
        location.addOnCompleteListener {
            val currentLocation = location.result
            val lat = currentLocation.latitude
            val lng = currentLocation.longitude
            val latLng = LatLng(lat, lng)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    /**
     * Request the permissions for locating the users positions.
     */
    private fun requestLocationPermissions() {
        if (locationPermissionsApproved()) return
        Timber.e("Requesting permissions.")
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
    }

    /**
     * Check if we have the permissions to go check the users position.
     */
    private fun locationPermissionsApproved() : Boolean {
        val fineLocationApproved = ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationApproved = ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationApproved && coarseLocationApproved
    }
}