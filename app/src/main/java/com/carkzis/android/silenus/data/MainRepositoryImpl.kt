package com.carkzis.android.silenus.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class MainRepositoryImpl (private val firestore: FirebaseFirestore) : MainRepository {

    override suspend fun addReview(review: Review) {

        val reviews = firestore.collection("reviews").document()

        review.let {
            reviews.set(it)
                .addOnSuccessListener {
                    Timber.d("New document added!")
                }
                .addOnFailureListener {
                    Timber.d("Failed to add review document...")
                }
        }
    }
}