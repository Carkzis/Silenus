package com.carkzis.android.silenus.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel(){

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    fun setUsername() {
        _username.value = Firebase.auth.currentUser?.displayName
    }

    fun toastMe(message: String) {
        showToastMessage(message)
    }

    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

}