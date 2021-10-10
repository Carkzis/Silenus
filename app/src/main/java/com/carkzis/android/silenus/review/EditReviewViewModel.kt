package com.carkzis.android.silenus.review

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.Event
import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.*
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

    /*
    TODO: Consider merging this with AddReviewViewModel with the use of higher-order functions.
     */

    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var location = MutableLiveData<String>()
    var description = MutableLiveData<String?>()

    private var _geopoint = MutableLiveData<GeoPoint>()
    val geopoint: LiveData<GeoPoint>
        get() = _geopoint

    private var _navToSingleReview = MutableLiveData<Event<YourReview>>()
    val navToSingleReview: LiveData<Event<YourReview>>
        get() = _navToSingleReview

    fun submissionPreChecks(review: YourReview) {
        if (barName.value == null) {
            showToastMessage(R.string.no_establishment)
            return
        } else if (location.value == null) {
            showToastMessage(R.string.no_location)
            return
        } else if (geopoint.value == null) {
            showToastMessage(R.string.error)
        }
        val editReviewObject = createAmendedReview(review)
        progressToEditOfReview(editReviewObject)
    }

    private fun createAmendedReview(review: YourReview) : YourReview {
        return YourReview(
            review.documentId,
            barName.value,
            rating.value,
            location.value,
            description.value,
            review.dateAdded,
            _geopoint.value
        )
    }

    private fun progressToEditOfReview(review: YourReview) {
        viewModelScope.launch {
            review.let { repository.editYourReview(
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

    /**
     * Sets up the up the LiveData for the location of the establishment being reviewed,
     * using the input geoPoint, which gives the latitude and longitude, and the geoCoder, which
     * uses these values to return the first line of the address for the resulting location.
     * The value "Nowhere Land" is posted if the address is null (this can happen when the location
     * is out at sea.  Restaurant on the waves!)
     */
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

    /**
     * Posts the provided barName to the associated LiveData.
     */
    fun setUpBarName(name: String) {
        barName.value = name
    }

    /**
     * Posts the provided barRating to the associated LiveData. Throws an
     * IllegalArgumentException if the rating exceeds 5.0f or 0.0f.
     */
    fun setUpRating(barRating: Float) {
        if (barRating > 5.0f || barRating < 0.0f) throw IllegalArgumentException()
        rating.value = barRating
    }

    /**
     * Posts the provided description to the associated LiveData.
     */
    fun setUpDescription(summary: String) {
        description.value = summary
    }

    /**
     * Posts a location to the associated LiveData, for testing purposes.
     */
    fun setUpLocation(loc: String) {
        location.value = loc
    }

    /**
     * Sets up individual LiveData information using individual fields
     * from a YourReview object and a Geocoder.
     */
    fun setUpReviewInfo(review: YourReview, geoCoder: Geocoder) {
        setUpLocationInfo(review.geo!!, geoCoder)
        barName.value = review.establishment!!
        rating.value = review.rating!!
        review.description?.let { description.value = review.description }
    }

    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    /**
     * Post a string value in an Event wrapper to the associated LiveData.
     */
    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }

}