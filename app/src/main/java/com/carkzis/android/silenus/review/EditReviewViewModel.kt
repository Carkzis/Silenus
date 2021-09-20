package com.carkzis.android.silenus.review

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.Event
import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MainRepository
import com.carkzis.android.silenus.data.UserRepository
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.data.toDataObject
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class EditReviewViewModel @Inject constructor(
    private val repository: MainRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var location = MutableLiveData<String>()
    var description = MutableLiveData<String?>()

    private var _geopoint = MutableLiveData<GeoPoint>()
    val geopoint: LiveData<GeoPoint>
        get() = _geopoint

    // TODO: Remove this, we will change the fields into the DataObject NewReviewDO.
    private var _yourReview = MutableLiveData<YourReview>()
    val yourReview: LiveData<YourReview>
        get() = _yourReview

    private var _navToSingleReview = MutableLiveData<Event<YourReview>>()
    val navToSingleReview: LiveData<Event<YourReview>>
        get() = _navToSingleReview

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
        progressToEditOfReview() // This means we can go ahead with this!
    }

    private fun progressToEditOfReview() {
        // TODO: This will edit the data in the database.
        viewModelScope.launch {
            _yourReview.value?.let { repository.editYourReview(
                it.toDataObject(userRepository.getUser().uid!!))
                .collect { loadingState ->
                    when (loadingState) {
                        is LoadingState.Loading -> {
                            Timber.e("Editing review...")
                        }
                        is LoadingState.Success -> {
                            showToastMessage(loadingState.message)
                            // Need to change the LiveData
                            _navToSingleReview.value = Event(loadingState.data!!)
                        }
                        is LoadingState.Error ->
                            showToastMessage(loadingState.message)
                    }
                }
            }
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

    fun setUpReviewInfo(review: YourReview, geoCoder: Geocoder) {
        setUpLocationInfo(review.geo!!, geoCoder)
        barName.value = review.establishment!!
        rating.value = review.rating!!
        review.description?.let { description.value = review.description }
    }

    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }

}