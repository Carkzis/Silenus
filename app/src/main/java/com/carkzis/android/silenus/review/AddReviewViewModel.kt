package com.carkzis.android.silenus.review

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.utils.Event
import com.carkzis.android.silenus.utils.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MainRepository
import com.carkzis.android.silenus.data.NewReviewDO
import com.carkzis.android.silenus.data.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import kotlin.IllegalArgumentException

/**
 * ViewModel for the AddReviewFragment.
 */
@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val repository: MainRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /*
    LiveData for bar/restaurant details from the UI.
     */
    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
    var location = MutableLiveData<String>()
    var description = MutableLiveData<String>()

    /*
    Holds data generated from the inputs/submission.
     */
    private var _geopoint = MutableLiveData<GeoPoint>()
    val geopoint: LiveData<GeoPoint>
        get() = _geopoint
    var submitDate = MutableLiveData<Timestamp>()

    /*
     Navigation LiveData, for taking the user back to the YourReviewsFragment.
     */
    private var _navToYourReviews = MutableLiveData<Event<Boolean>>()
    val navToYourReviews: LiveData<Event<Boolean>>
        get() = _navToYourReviews

    /*
    LiveData for feedback e.g. Toast.
     */
    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    /**
     * This checks that the minimum requirement for adding a review are available
     * (being the name and the location),
     * and provides feedback to the user accordingly via a Toast.  If all checks pass,
     * progressToAddingReview() is called to proceed with adding the review to the database.
     */
    fun submissionPreChecks() {
        if (barName.value == null || barName.value == "") {
            showToastMessage(R.string.no_establishment)
            return
        } else if (location.value == null) {
            showToastMessage(R.string.no_location)
            return
        } else if (geopoint.value == null) {
            showToastMessage(R.string.error) // We shouldn't get this at all.
            return
        }
        progressToAddingReview() // This means we can go ahead with this!
    }

    /**
     * Attempts to add a review to Firestore database.  Feedback is provided via a Toast.
     */
    private fun progressToAddingReview() {
        viewModelScope.launch() {
            repository.addReview(NewReviewDO(
                barName.value,
                rating.value,
                location.value,
                geopoint.value,
                description.value,
                Timestamp.now(),
                userRepository.getUser().uid,
                deleted = false
            )).collect { loadingState ->
                when (loadingState) {
                    is LoadingState.Loading -> {
                        Timber.e("Posting review...")
                    }
                    is LoadingState.Success -> {
                        showToastMessage(loadingState.message)
                        _navToYourReviews.value = Event(true)
                    }
                    is LoadingState.Error ->
                        showToastMessage(loadingState.message)
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
     * Post a string value in an Event wrapper to the associated LiveData.
     */
    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }

}