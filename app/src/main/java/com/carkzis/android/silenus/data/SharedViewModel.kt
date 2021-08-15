package com.carkzis.android.silenus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.data.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private var _chosenGeopoint = MutableLiveData<GeoPoint?>()
    val chosenGeopoint: LiveData<GeoPoint?>
        get() = _chosenGeopoint

    fun addUser() {
        val userProfile = Firebase.auth.currentUser
        // Note: may not be a new user.
        val newUser = userProfile?.let {
            User(
                name = userProfile.displayName,
                email = userProfile.email,
                isAdmin = false,
                ratings = 0,
                joinDate = Timestamp.now()
            )
        }
        repository.addUser(newUser!!, userProfile.uid)
    }

    fun setGeopoint(latLng: LatLng) {
        _chosenGeopoint.value = GeoPoint(latLng.latitude, latLng.longitude)
    }

    fun resetGeopoint() {
        _chosenGeopoint.value = null
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

data class User(
    val name: String?,
    val email: String?,
    @field:JvmField
    val isAdmin: Boolean,
    val ratings: Int,
    val joinDate: Timestamp
)