package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.mockito.Mock
import org.mockito.Mockito.mock
import javax.inject.Inject

class FakeMainRepository @Inject constructor() : MainRepository {

    // Decides if a request to Firestore will fail or not.
    var failure = false
    // This will decide if a request is loading or not.
    var loading = false

    @Mock
    private lateinit var mockDocumentReference: DocumentReference

    override suspend fun addReview(review: NewReviewDO) = flow {
        // The review goes into the aether.
        emit(LoadingState.Loading(R.string.loading))
        // Skip if we are loading.
        if (!loading) {
            // Do this if we are successful.
            if (!failure) {
                // We want this initialised. It is just there to allow LoadingState.Success to be emitted.
                mockDocumentReference = mock(DocumentReference::class.java)
                emit(LoadingState.Success(R.string.review_added, mockDocumentReference))
            } else {
                throw Exception()
            }
        }
    }.catch {
        emit(LoadingState.Error(R.string.error, Exception()))
    }

    override suspend fun getYourReviews() = flow {
        emit(LoadingState.Loading(R.string.reviews_retrieved))
        // Skip if we are loading.
        if (!loading) {
            // Do this if we are successful.
            if (!failure) {
                // Emit a list of (mock) reviews. Mock as their contents will not matter.
                emit(LoadingState.Success(R.string.reviews_retrieved, MutableList(5) {
                    mock(YourReview::class.java)
                }))
            } else {
                throw Exception()
            }
        } // Emit the error if we get here...
    }
        .catch {
        emit(LoadingState.Error(R.string.error, Exception()))
    }

    override suspend fun editYourReview(review: ReviewDO): Flow<LoadingState<YourReview>> {
        TODO("Not yet implemented")
    }

}