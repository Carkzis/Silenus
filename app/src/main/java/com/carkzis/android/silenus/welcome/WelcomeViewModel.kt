package com.carkzis.android.silenus.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.utils.Event
import com.carkzis.android.silenus.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel(){

    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    fun setUsername() {
        _username.value = repository.getUsername()
    }

    fun toastMe(message: String) {
        showToastMessage(message)
    }

    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

}