package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.Constants
import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getCollectionName
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) : MainRepository {

    /*
    TODO: If you are logged in, but you have no account in the remote database, these fall down.
          This can be avoided by logging a user out if they are not in the remote database.
     */

    /**
     * Add a review for a member into the database.
     */
    override suspend fun addReview(review: NewReviewDO) = flow {

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
    override suspend fun getYourReviews() = flow {
        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        // TODO: Use the uid obtained from the UserRepository instead of directly here.
        val yourReviewList = suspendCoroutine<QuerySnapshot> { cont ->
            reviews.whereEqualTo("uid", Firebase.auth.currentUser?.uid).get()
                .addOnSuccessListener { cont.resume(it) } // This is successful.
                .addOnFailureListener { throw Exception() }
        }.toObjects(YourReview::class.java) // This converts the snapshot into a list.

        emit(LoadingState.Success(R.string.reviews_retrieved, yourReviewList))

    }.catch {
        emit(
            LoadingState.Error(
                R.string.error,
                Exception()
            )
        ) // Emit the error if we get here...
    }.flowOn(Dispatchers.IO)

    /**
     * This edits a review, by setting the review again using the same document id.
     */
    override suspend fun editYourReview(review: ReviewDO) = flow {
        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))
        Timber.e("Come on?")

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        /*
        This ensures we await the result of the query before we emit again.
        There is no returned object, just void.
         */
        // TODO: May need to test here that we are who we say we are, or add to security rules.
        suspendCoroutine<Void> { cont ->
            reviews.document(review.id.toString()).set(review)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { throw Exception() }
        }

        Timber.e("Come on?")

        emit(LoadingState.Success(R.string.review_edited, review.toUIModel())) // Just emit the review!

    }
        .catch {
            emit(
                LoadingState.Error(
                    R.string.error,
                    Exception()
                )
            ) // Emit the error if we get here...
        }.flowOn(Dispatchers.IO)
}