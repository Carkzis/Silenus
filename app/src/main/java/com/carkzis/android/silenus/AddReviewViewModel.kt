package com.carkzis.android.silenus

import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.sql.Time
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor() : ViewModel() {

    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var location = MutableLiveData<String>()
    private var geopoint = MutableLiveData<GeoPoint>()
    var description = MutableLiveData<String>()
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
        geopoint.value = geoPoint
        val address = geoCoder
            .getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
            .get(0)
            .getAddressLine(0)
        location.value = address
        Timber.e(address.toString())
    }
}