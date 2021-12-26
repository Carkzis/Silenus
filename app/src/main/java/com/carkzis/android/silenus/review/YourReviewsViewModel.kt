package com.carkzis.android.silenus.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.utils.Event
import com.carkzis.android.silenus.utils.LoadingState
import com.carkzis.android.silenus.data.MainRepository
import com.carkzis.android.silenus.data.YourReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the YourReviewsFragment.
 */
@HiltViewModel
class YourReviewsViewModel @Inject constructor(
    val repository: MainRepository
) : ViewModel() {

    /*
    LiveData for bar/restaurant details for the UI.
     */
    private var _yourReviews = MutableLiveData<List<YourReview>>()
    val yourReviews: LiveData<List<YourReview>>
        get() = _yourReviews

    /*
    LiveData for feedback e.g. Toast.
     */
    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    /**
     * Public method to attempt to refresh reviews.
     */
    fun refreshReviews() {
        getAndAttachReviews()
    }

    /**
     * Gets a list of reviews from MainRepository, and adds them to the yourReviews LiveData
     * to be observed by the YourReviewsFragment.
     */
    private fun getAndAttachReviews() {
        viewModelScope.launch {
            repository.getYourReviews()
                .collect { loadingState ->
                    when (loadingState) {
                        is LoadingState.Loading -> {
                            Timber.e("Loading reviews...")
                        }
                        is LoadingState.Success -> {
                            Timber.e(loadingState.data.toString())
                            _yourReviews.value = loadingState.data!!
                            Timber.e("Reviews loaded!")
                        }
                        is LoadingState.Error -> {
                            Timber.e("Error loading reviews: ${loadingState.exception}")
                            showToastMessage(loadingState.message)
                        }
                    }
                }
        }
    }

    /**
     * Post a string value in an Event wrapper to the associated LiveData.
     */
    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }
}

