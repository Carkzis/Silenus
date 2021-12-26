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

    /*
    LiveData for feedback e.g. Toast.
     */
    private var _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>>
        get() = _toastText

    /*
    LiveData for the username.
     */
    private var _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    /**
     * Set the username LiveData to that obtained from the repository.
     */
    fun setUsername() {
        _username.value = repository.getUsername()
    }

    /**
     * Post a string value to the associated LiveData via the showToastMessage() method.
     */
    fun toastMe(message: String) {
        showToastMessage(message)
    }

    /**
     * Post a string value in an Event wrapper to the associated LiveData.
     */
    private fun showToastMessage(message: String) {
        _toastText.value = Event(message)
    }

}