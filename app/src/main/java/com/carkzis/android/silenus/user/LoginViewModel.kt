package com.carkzis.android.silenus.user

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.utils.Event
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.UserRepository
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the LoginFragment.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    /*
     Navigation LiveData, for taking the user to the WelcomeFragment.
     */
    private var _navToWelcome = MutableLiveData<Event<Boolean>>()
    val navToWelcome: LiveData<Event<Boolean>>
        get() = _navToWelcome

    var navToWelcomePosted = false

    /**
     * Forwards the user to the WelcomeFragment if the FirebaseUser currentUser value is not null.
     */
    fun authoriseUser() {
        if (repository.getUser().currentUser != null) {
            _navToWelcome.value = Event(true)
            /*
             For testing purposes, this will change from false, as this is a faster test
             for a negative result than awaiting no posting to LiveData.
             */
            navToWelcomePosted = true
        }
    }

    /**
     * Post a string value in an Event wrapper to the associated LiveData.
     */
    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }

    /*
    Note: for some reason, this is not called when the member registers/signs in
    using password and email.
     */
    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            _navToWelcome.value = Event(true)
        } else {
            showToastMessage(R.string.sign_in_failed)
        }
    }


}