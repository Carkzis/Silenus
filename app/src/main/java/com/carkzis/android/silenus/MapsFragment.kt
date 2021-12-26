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
import com.carkzis.android.silenus.user.AuthCheck
import com.firebase.ui.auth.AuthUI
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
class MapsFragment : Fragment(), OnMapReadyCallback, AuthCheck {

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private val args : MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()

        /*
         We request an authorisation of the user; if this fails, the user is directed
         to the LoginFragment.
         */
        sharedViewModel.authoriseUser()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        mapReasonListener(map)
    }

    /**
     * This sets up the reasons we have navigated to the MapFragment (e.g. add a review (ADDREV)
     * or edit a review (EDITREV). If this alters where we navigate to after selecting a location,
     * the relevant navigation method is provided as an argument in the setUpLocationRequest()
     * method.
     */
    private fun mapReasonListener(map: GoogleMap) {
        sharedViewModel.mapReason.observe(viewLifecycleOwner, {
            when (it) {
                MapReason.ADDREV -> {
                    getCurrentLocation(15.0f)
                    setUpLocationRequest(map) { navToAddRevFragment() }
                }
                MapReason.EDITREV -> {
                    setUpEditReviewLocation(map)
                    setUpLocationRequest(map) { navToEditRevFragment() }
                }
                MapReason.VIEWREV -> setUpReviewLocation(map)
                else -> Timber.e("No map reason supplied.")
            }
        })
    }

    /**
     * Navigates the user to the AddReviewFragment.
     */
    private fun navToAddRevFragment() {
        findNavController().navigate(
            MapsFragmentDirections.actionMapsFragmentToAddReviewFragment())
    }

    /**
     * Navigates the user to the EditReviewFragment.
     */
    private fun navToEditRevFragment() {
        findNavController().navigate(
            MapsFragmentDirections.actionMapsFragmentToEditReviewFragment())
    }

    /**
     * This will home in onto the bar location on the map.
     */
    private fun setUpReviewLocation(map: GoogleMap) {
        // This may be better using viewModels tbh. Do not like safeArgs.
        val latLng = args.yourReviewsMapLocation
        val latlng = LatLng(latLng!![0].toDouble(), latLng[1].toDouble())
        val zoom = 10.0f
        zoomMe(map, zoom, latlng)
    }

    /**
     * Set up original bar location when using the Map to edit a review.
     */
    private fun setUpEditReviewLocation(map: GoogleMap) {
        val lat = sharedViewModel.singleReview.value?.geo?.latitude
        val lng = sharedViewModel.singleReview.value?.geo?.longitude
        val latLng = LatLng(lat!!, lng!!)
        val zoom = 10.0f
        zoomMe(map, zoom, latLng)
    }

    /**
     * This sets up the map for when getting a location when adding a review.
     * It has a listener to set a marker for where the reviewed establishment is.
     */
    private fun setUpLocationRequest(map: GoogleMap, navigateMe: () -> Unit) {
        map.setOnMapLongClickListener { latitudeLongitude ->
            // Add a marker to the map.
            val marker = map.addMarker(MarkerOptions().position(latitudeLongitude))
            // Set up the dialog box to allow the user to confirm their selection.
            val builder = AlertDialog.Builder(view?.context)
            builder.setTitle(R.string.location_selected)
            builder.setMessage(R.string.this_place_query)
            builder.setPositiveButton(R.string.yes) {_, _ ->
                sharedViewModel.setGeopoint(latitudeLongitude)
                navigateMe()
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
        } else {
            val location = fusedLocationClient.lastLocation
            location.addOnCompleteListener {
                // Sometimes, despite saying otherwise, this can be null.
                // May just be an emulator thing? Needs handling anyway.
                if (it.result == null) return@addOnCompleteListener
                val currentLocation = it.result
                val lat = currentLocation.latitude
                val lng = currentLocation.longitude
                val latLng = LatLng(lat, lng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
            }
        }
    }

    /**
     * Request the permissions for locating the users positions.
     */
    private fun requestLocationPermissions() {
        if (locationPermissionsApproved()) return
        Timber.e("Requesting permissions.")
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }

    /**
     * Check if we have the permissions to go check the users position.
     */
    private fun locationPermissionsApproved() : Boolean {
        val fineLocationApproved = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationApproved = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationApproved && coarseLocationApproved
    }

    override fun setUpLogout() {
        sharedViewModel.logout.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { reason ->
                AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        sharedViewModel.toastMe(getString(reason))
                        findNavController().navigate(
                            MapsFragmentDirections.actionMapsFragmentToLoginFragment()
                        )
                    }
            }
        })
    }

    override fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                findNavController().navigate(
                    MapsFragmentDirections.actionMapsFragmentToLoginFragment()
                )
            }
        })
    }
}