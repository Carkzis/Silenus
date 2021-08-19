package com.carkzis.android.silenus.review

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.Event
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MainRepository
import com.carkzis.android.silenus.data.Review
import com.carkzis.android.silenus.data.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val repository: MainRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var location = MutableLiveData<String>()
    var description = MutableLiveData<String>()

    private var _geopoint = MutableLiveData<GeoPoint>()
    val geopoint: LiveData<GeoPoint>
        get() = _geopoint

    var submitDate = MutableLiveData<Timestamp>()

    fun submissionPreChecks() {
        if (barName.value == null) {
            showToastMessage(R.string.no_establishment)
            return
        } else if (location.value == null) {
            showToastMessage(R.string.no_location)
            return
        } else if (geopoint.value == null) {
            showToastMessage(R.string.error)
        }

        viewModelScope.launch {
            // TODO: Add try catch, and make failure throw exception in repository.
            repository.addReview(Review(
                barName.value,
                rating.value,
                location.value,
                geopoint.value,
                description.value,
                Timestamp.now(),
                userRepository.getUser().uid
            ))
            _navToWelcome.value = Event(true)
        }
    }

    fun setUpLocationInfo(geoPoint: GeoPoint, geoCoder: Geocoder) {
        _geopoint.value = geoPoint
        var address = ""
        try {
            address = geoCoder
                .getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)[0]
                .getAddressLine(0)
            location.value = address
        } catch (e: Exception) {
            location.value = "Nowhere Land"
        }
        Timber.e(address)
    }

    fun setUpBarName(name: String) {
        barName.value = name
    }

    fun setUpRating(barRating: Float) {
        rating.value = barRating
    }

    fun setUpDescription(summary: String) {
        description.value = summary
    }

    private var _navToWelcome = MutableLiveData<Event<Boolean>>()
    val navToWelcome: LiveData<Event<Boolean>>
        get() = _navToWelcome

    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }

}