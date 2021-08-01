package com.carkzis.android.silenus

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class UserRepository : Repository {

    lateinit var firestore: FirebaseFirestore

    override fun addUser(addedUser: User) {

        firestore = FirebaseFirestore.getInstance()
        val users = firestore.collection("users").document(addedUser.userId)
        users.set(addedUser)
            .addOnSuccessListener { Timber.d("Member added with id ${addedUser.userId}") }
            .addOnFailureListener { Timber.d("Failure to add user document...")}
    }

}