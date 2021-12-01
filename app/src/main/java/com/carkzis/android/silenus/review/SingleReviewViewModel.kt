package com.carkzis.android.silenus.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carkzis.android.silenus.data.MainRepository
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.utils.Event
import com.carkzis.android.silenus.utils.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SingleReviewViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    /**
     * This is for displaying the current review to the UI.
     */
    private var _yourReview = MutableLiveData<YourReview>()
    val yourReview: LiveData<YourReview>
        get() = _yourReview

    private val _deletedReviewId = MutableLiveData<String>()
    val deletedReviewId: LiveData<String>
        get() = _deletedReviewId

    fun setUpRev(review: YourReview) {
        _yourReview.value = review
    }

    fun getGeo() : Array<String> {
        return arrayOf(_yourReview.value?.geo?.latitude.toString(),
        _yourReview.value?.geo?.longitude.toString())
    }

    fun progressToDeletingReview(review: YourReview) {
        viewModelScope.launch {
            repository.deleteReview(review.documentId!!)
                .collect { loadingState ->
                when (loadingState) {
                    is LoadingState.Loading -> {
                        Timber.e("Posting review...")
                    }
                    is LoadingState.Success -> {
                        showToastMessage(loadingState.message)
                        delay(500) // Delays the return to the YourReviewsFragment.
                        _deletedReviewId.value = loadingState.data!!
                        _navToYourReviews.value = Event(true)
                    }
                    is LoadingState.Error ->
                        showToastMessage(loadingState.message)
                }
            }
        }
    }

    private var _navToYourReviews = MutableLiveData<Event<Boolean>>()
    val navToYourReviews: LiveData<Event<Boolean>>
        get() = _navToYourReviews

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