package com.carkzis.android.silenus.user

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.Event
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.UserRepository
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authorisation: FirebaseAuth
) : ViewModel() {

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    private var _loginIntent = MutableLiveData<Event<Intent>>()
    val loginIntent: LiveData<Event<Intent>>
        get() = _loginIntent

    private var _navToWelcome = MutableLiveData<Event<Boolean>>()
    val navToWelcome: LiveData<Event<Boolean>>
        get() = _navToWelcome

    fun signedInToast(message: String) {
        showToastMessage(message)
    }

    fun authoriseUser() {
        if (repository.getUser().currentUser != null) {
            _navToWelcome.value = Event(true)
        }
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