package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.User
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

object UserRemoteDataSource : UserDataSource {

    lateinit var firestore: FirebaseFirestore

    // TODO: Needs error handling, as do not want member logged in if they are not added to db.
    override fun addUser(addedUser: User, userId: String) {
        firestore = FirebaseFirestore.getInstance()
        val users = firestore.collection("users").document(userId)
        users.set(addedUser)
            .addOnSuccessListener { Timber.d("Member added with id ${userId}") }
            .addOnFailureListener { Timber.d("Failure to add user document...")}
    }

}