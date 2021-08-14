package com.carkzis.android.silenus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.sql.Time
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor() : ViewModel() {

    var barName = MutableLiveData<String>()
    var rating = MutableLiveData<Float>()
        set
    // TODO: Give the geopoint as well as location name.
    var location = MutableLiveData<String>()
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

}