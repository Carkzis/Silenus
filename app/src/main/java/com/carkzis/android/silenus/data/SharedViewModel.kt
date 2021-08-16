package com.carkzis.android.silenus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.data.UserModel
import com.carkzis.android.silenus.data.UserRepository
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authorisation: FirebaseAuth
) : ViewModel() {

    // These are for the review screen, saving the data when we are taken to the map fragment.
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

    // Shared user data across fragments
    private var _logout = MutableLiveData<Event<Int>>()
    val logout: LiveData<Event<Int>>
        get() = _logout

    private var _navToLogin = MutableLiveData<Event<Boolean>>()
    val navToLogin: LiveData<Event<Boolean>>
        get() = _navToLogin

    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    fun addUser() {
        viewModelScope.launch {
            repository.addUser()
        }
    }

    fun chooseLogout() {
        _logout.value = Event(R.string.logged_out)
    }

    // TODO: BE VARY CAREFUL WITH THIS.
    fun authoriseUser() {
        if (authorisation.currentUser == null) {
            _navToLogin.value = Event(true)
        } else if (authorisation.currentUser?.displayName == null ||
            authorisation.currentUser?.displayName == "") {
            _logout.value = Event(R.string.null_user_error)
        } else {
            _username.value = repository.getUsername()
            addUser()
        }
    }


    fun setGeopoint(latLng: LatLng) {
        _chosenGeopoint.value = GeoPoint(latLng.latitude, latLng.longitude)
    }

    fun setBarDetails(name: String?, rating: Float?, description: String?) {
        _reviewBarName.value = name
        _reviewRating.value = rating
        _reviewDescription.value = description
    }

    fun resetReviewScreen() {
        _chosenGeopoint.value = null
        _reviewBarName.value = null
        _reviewRating.value = null
        _reviewDescription.value = null
    }

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    fun toastMe(message: String) {
        showToastMessage(message)
    }

    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

}