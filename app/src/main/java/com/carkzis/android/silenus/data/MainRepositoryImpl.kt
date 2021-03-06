package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.utils.Constants
import com.carkzis.android.silenus.utils.LoadingState
import com.carkzis.android.silenus.utils.getCollectionName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Interface for the Main Repository, which is used to abstract access to non-User data
 * between the Firestore database, and the UI.
 */
class MainRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val firebaseAuth: FirebaseAuth) : MainRepository {

    /**
     * Adds a new review for a member into the database.
     */
    override suspend fun addReview(review: NewReviewDO) = flow {

        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        // This ensures we await the result of the query before we emit again.
        reviews.add(review)

        emit(LoadingState.Success(R.string.review_added, null)) // Emit the result!

    }.catch {
        emit(LoadingState.Error(R.string.error, Exception())) // Emit the error if we get here...
    }.flowOn(Dispatchers.IO)

    /**
     * Get the member's reviews from the Firestore database.
     */
    override suspend fun getYourReviews() = flow {
        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        // Need to use a listener, so that we can remove it to prevent weird bugs.
        lateinit var query: ListenerRegistration

        val yourReviewList = suspendCancellableCoroutine<QuerySnapshot> { cont ->
            query = reviews
                .whereEqualTo("uid", firebaseAuth.currentUser?.uid)
                .whereEqualTo("deleted", false) // We don't want deleted items.
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error.localizedMessage)
                        throw error
                    } else {
                        // Need to ensure that the coroutine has not already been resumed.
                        if (cont.isActive) {
                            cont.resume(snapshot!!)
                        }
                    }
                }
        }.toObjects(YourReview::class.java)

        // Remove the listener after we have performed our query.
        query.remove()

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
     * This edits a review by setting the review again using the same document id.
     */
    override suspend fun editYourReview(review: ReviewDO) = flow {
        val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        reviews.document(review.id.toString()).set(review)

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

        // Need to use a listener, so that we can remove it to prevent weird bugs.
        lateinit var query: ListenerRegistration

        suspendCancellableCoroutine<DocumentSnapshot> { cont ->
            query = reviews.document(reviewId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error.localizedMessage)
                        throw error
                    } else {
                        // Delete the reference.
                        snapshot?.reference?.update("deleted", true)
                        if (cont.isActive) {
                            cont.resume(snapshot!!)
                        }
                    }
                }
        }

        // Remove the listener after we have deleted the review.
        query.remove()

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