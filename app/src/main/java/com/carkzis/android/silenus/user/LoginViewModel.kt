package com.carkzis.android.silenus.user

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.Event
import com.carkzis.android.silenus.R
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    private var _loginIntent = MutableLiveData<Event<Intent>>()
    val loginIntent: LiveData<Event<Intent>>
        get() = _loginIntent

    fun signedInToast(message: String) {
        showToastMessage(message)
    }

    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

    fun signIn() {
        val customLayout = AuthMethodPickerLayout
            .Builder(R.layout.firebase_auth)
            .setGoogleButtonId(R.id.google_signin)
            .setEmailButtonId(R.id.email_signin)
            .build();
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher_round)
            .setTheme(R.style.Theme_Silenus)
            .setAuthMethodPickerLayout(customLayout)
            .setAvailableProviders(listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
            ))
            .build()
        _loginIntent.value = Event(intent)
    }

}