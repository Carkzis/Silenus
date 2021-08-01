package com.carkzis.android.silenus

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun addUser() {

        val userProfile = Firebase.auth.currentUser
        // Note: may not be a new user.
        val newUser = userProfile?.let {
            User(
                userId = userProfile.uid,
                name = userProfile.displayName,
                email = userProfile.email,
                isAdmin = false
            )
        }
        repository.addUser(newUser!!)
    }
}

data class User(
    val userId: String,
    val name: String?,
    val email: String?,
    @field:JvmField
    val isAdmin: Boolean,
)