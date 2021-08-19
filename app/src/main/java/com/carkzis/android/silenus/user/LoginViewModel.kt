package com.carkzis.android.silenus.user

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.Event
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.UserRepository
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authorisation: FirebaseAuth
) : ViewModel() {

    private var _toastText = MutableLiveData<Event<Int>>()
    val toastText: LiveData<Event<Int>>
        get() = _toastText

    private var _navToWelcome = MutableLiveData<Event<Boolean>>()
    val navToWelcome: LiveData<Event<Boolean>>
        get() = _navToWelcome


    fun authoriseUser() {
        if (repository.getUser().currentUser != null) {
            _navToWelcome.value = Event(true)
        }
    }

    private fun showToastMessage(message: Int) {
        _toastText.value = Event(message)
    }

    // Note: for some reason, this is not called when the member registers/signs in using password
    // and email.
    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            _navToWelcome.value = Event(true)
        } else {
            showToastMessage(R.string.sign_in_failed)
        }
    }


}