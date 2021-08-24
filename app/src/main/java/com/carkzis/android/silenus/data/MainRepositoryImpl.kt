package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.Constants
import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getCollectionName
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainRepositoryImpl (private val firestore: FirebaseFirestore) : MainRepository {

    /**
     * Add a review for a member into the database.
     */
    override suspend fun addReview(review: Review) = flow {

        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        // This ensures we await the result of the query before we emit again.
        val reviewReference = suspendCoroutine<DocumentReference> { cont ->
            reviews.add(review)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { throw Exception() }
        }

        emit(LoadingState.Success(R.string.review_added, reviewReference)) // Emit the result!

    }.catch {
        emit(LoadingState.Error(R.string.error, Exception())) // Emit the error if we get here...
    }.flowOn(Dispatchers.IO)

    /**
     * Get the member's reviews, to put into the database.
     */
    suspend fun getYourReviews() = flow {
        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        val yourReviewList = suspendCoroutine<QuerySnapshot> { cont ->
            reviews.get()
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { throw Exception() }
        }.toObjects(YourReview::class.java) // This converts the snapshot into a list.

        emit(LoadingState.Success(R.string.reviews_retrieved, yourReviewList))

    }.catch {
        emit(LoadingState.Error(R.string.error, Exception())) // Emit the error if we get here...
    }.flowOn(Dispatchers.IO)
}