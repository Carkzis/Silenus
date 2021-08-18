package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class UserRepositoryImpl(private val firestore: FirebaseFirestore): UserRepository {

    override suspend fun addUser() {
        val userProfile = Firebase.auth.currentUser
        // Note: may not be a new user.
        val newUser = userProfile?.let {
            UserModel(
                name = userProfile.displayName,
                email = userProfile.email,
                isAdmin = false,
                ratings = 0,
                joinDate = Timestamp.now()
            )
        }

        val users = userProfile?.let { firestore.collection("users").document(it.uid) }

        if (users != null) {
            newUser?.let {
                users.set(it)
                    .addOnSuccessListener {
                        Timber.d("Member added with id ${userProfile.uid}")
                    }
                    .addOnFailureListener {
                        Timber.d("Failed to add user document...")
                    }
            }
        }
    }

    override fun getUsername() : String {
        return Firebase.auth.currentUser?.displayName ?: ""
    }

    override fun getUser() : FirebaseAuth {
        return Firebase.auth
    }

}