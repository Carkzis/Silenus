package com.carkzis.android.silenus.review

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.data.MainRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var location = MutableLiveData<String>()
    var description = MutableLiveData<String>()

    private var _geopoint = MutableLiveData<GeoPoint>()
    val geopoint: LiveData<GeoPoint>
        get() = _geopoint

    var submitDate = MutableLiveData<Timestamp>()

    fun barSubmission() {
        submitDate.value = Timestamp.now()
        Timber.d(barName.value)
        Timber.d(rating.value.toString())
        Timber.d(location.value)
        Timber.d(description.value)
        Timber.d(submitDate.value.toString())
    }

    fun setUpLocationInfo(geoPoint: GeoPoint, geoCoder: Geocoder) {
        _geopoint.value = geoPoint
        var address: String = ""
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

}