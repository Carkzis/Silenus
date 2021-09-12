package com.carkzis.android.silenus.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.data.MainRepository
import com.carkzis.android.silenus.data.YourReview
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun setUpRev(review: YourReview) {
        _yourReview.value = review
    }

    fun getGeo() : Array<String> {
        return arrayOf(_yourReview.value?.geo?.latitude.toString(),
        _yourReview.value?.geo?.longitude.toString())
    }

}