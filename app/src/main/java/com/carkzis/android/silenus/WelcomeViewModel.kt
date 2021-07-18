package com.carkzis.android.silenus

import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.firebase.ui.auth.AuthUI

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel(){

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    fun signedOutToast(message: String) {
        showToastMessage(message)
    }

    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

}