package com.carkzis.android.silenus.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.utils.Event
import com.carkzis.android.silenus.utils.LoadingState
import com.carkzis.android.silenus.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    /*
     * These are for the add and edit review screen, saving the data when we are taken to the map fragment.
     */
    private var _chosenGeopoint = MutableLiveData<GeoPoint?>()
    val chosenGeopoint: LiveData<GeoPoint?>
        get() = _chosenGeopoint

    private var _reviewBarName = MutableLiveData<String?>()
    val reviewBarName: LiveData<String?>
        get() = _reviewBarName

    private var _reviewRating = MutableLiveData<Float?>()
    val reviewRating: LiveData<Float?>
        get() = _reviewRating

    private var _reviewDescription = MutableLiveData<String?>()
    val reviewDescription: LiveData<String?>
        get() = _reviewDescription

    /*
     * This relates to maps, an enum that limits what the map fragment is used for.
     */
    private var _mapReason = MutableLiveData<MapReason?>()
    val mapReason: LiveData<MapReason?>
        get() = _mapReason

    /*
     * Shared user data across fragments
     */
    private var _logout = MutableLiveData<Event<Int>>()
    val logout: LiveData<Event<Int>>
        get() = _logout

    private var _navToLogin = MutableLiveData<Event<Boolean>>()
    val navToLogin: LiveData<Event<Boolean>>
        get() = _navToLogin

    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    /*
     * This is for the individual review screen.
     */
    private val _singleReview = MutableLiveData<YourReview>()
    val singleReview: LiveData<YourReview>
        get() = _singleReview

    /**
     * Adds a user to Firestore via the UserRepository.  The UserRepository gets
     * access to the details via FirebaseAuth; the user details are not provided by the ViewModel.
     */
    private fun addUser() {
        viewModelScope.launch() {
            repository.addUser().collect { loadingState ->
                when (loadingState) {
                    is LoadingState.Loading -> {
                        Timber.e("Adding user...")
                    }
                    is LoadingState.Success -> {
                        Timber.e("User added.")
                    }
                    is LoadingState.Error -> {
                        Timber.e("Error adding user.")
                    }
                }
            }
        }
    }

    /**
     * Sends an event with a log out message, to be used as a toast once observed by a fragment.
     */
    fun chooseLogout() {
        _logout.value = Event(R.string.logged_out)
    }

    /**
     * Authorises the user based on some tests.
     */
    fun authoriseUser() {
        val auth = repository.getUser()
        if (auth.currentUser == null) {
            _navToLogin.value = Event(true)
        } else if (auth.currentUser?.displayName == null ||
            auth.currentUser?.displayName == "") {
            _logout.value = Event(R.string.null_user_error)
        } else {
            _username.value = repository.getUsername()
            addUser()
        }
    }

    /**
     * Sets the chosen geopoint, using the input LatLng object, obtained from the Maps API.
     * This allows the input to be stored whilst the user goes to the Map fragment and back.
     */
    fun setGeopoint(latLng: LatLng) {
        _chosenGeopoint.value = GeoPoint(latLng.latitude, latLng.longitude)
    }

    /**
     * Set bar details with the exception of the geopoint, obtained from user inputs.
     * This allows the inputs to be stored whilst the user goes to the Map fragment and back.
     */
    fun setBarDetails(name: String?, rating: Float?, description: String?) {
        _reviewBarName.value = name
        _reviewRating.value = rating
        _reviewDescription.value = description
    }

    /**
     * This is for setting bar details from a UI model e.g. data obtained from Firestore.
     */
    fun setBarDetailsFromModel() {
        _reviewBarName.value = _singleReview.value?.establishment
        _reviewRating.value = _singleReview.value?.rating
        _reviewDescription.value = _singleReview.value?.description
        _chosenGeopoint.value = _singleReview.value?.geo
    }

    /**
     * Clears the LiveData associated with the review inputs.
     */
    fun resetReviewScreen() {
        _chosenGeopoint.value = null
        _reviewBarName.value = null
        _reviewRating.value = null
        _reviewDescription.value = null
    }

    /**
     * Sets a single YourReview object to the _singleReview LiveData.
     */
    fun setSingleReview(review: YourReview) {
        _singleReview.value = review
    }

    /**
     * Sets the reason as to why we are visiting the MapFragment.
     * ADDREV: Visiting from AddReviewFragment.
     * VIEWREV: Visiting from YourReviewsFragment.
     * EDITREV: Visiting from EditReviewFragment.
     */
    fun setMapOpenReason(reason: MapReason) {
        _mapReason.value = reason
    }

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    /**
     * This allows for a Toast to be generated even when switching between Fragments,
     * by adding the string to a one-off Event LiveData.
     */
    fun toastMe(message: String) {
        showToastMessage(message)
    }

    /**
     * Adds a message in an Event wrapper, which will result in a Toast being generated
     * by the observing Fragment.
     */
    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

}

enum class MapReason { ADDREV, VIEWREV, EDITREV }