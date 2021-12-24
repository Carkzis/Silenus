package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.utils.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.utils.Constants
import com.carkzis.android.silenus.utils.getCollectionName
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

        val yourReviewList = suspendCoroutine<QuerySnapshot> { cont ->
            reviews
                .whereEqualTo("uid", Firebase.auth.currentUser?.uid)
                .whereEqualTo("deleted", false) // We don't want deleted items.
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error.localizedMessage)
                        throw error
                    } else {
                        cont.resume(snapshot!!)
                    }
                }
        }.toObjects(YourReview::class.java)

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

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        /*
        This ensures we await the result of the query before we emit again.
        There is no returned object, just void.
         */
        suspendCoroutine<Void> { cont ->
            reviews.document(review.id.toString()).set(review)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { throw Exception() }
        }

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

    /**
     * This deletes a review, by setting the deleted flag to true.
     */
    override suspend fun deleteReview(reviewId: String) = flow {
        val reviews = firestore.collection((getCollectionName(Constants.REVIEWS)))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        reviews.document(reviewId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error.localizedMessage)
                    throw error
                } else {
                    // Delete the reference.
                    snapshot?.reference?.update("deleted", true)
                }
            }

        // Return the review.
        emit(LoadingState.Success(R.string.review_deleted, reviewId))
    }
        .catch {
        emit(
            LoadingState.Error(
                R.string.error,
                Exception("Error deleting $reviewId.")
            )
        ) // Emit the error if we get here...
    }.flowOn(Dispatchers.IO)

}