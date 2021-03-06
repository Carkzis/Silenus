package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.utils.LoadingState
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Main Repository, which is used to abstract access to non-User data
 * between the Firestore database, and the UI.
 */
interface MainRepository {
    suspend fun addReview(review: NewReviewDO): Flow<LoadingState<DocumentReference>>
    suspend fun getYourReviews(): Flow<LoadingState<MutableList<YourReview>>>
    suspend fun editYourReview(review: ReviewDO): Flow<LoadingState<YourReview>>
    suspend fun deleteReview(reviewId: String): Flow<LoadingState<String>>
}