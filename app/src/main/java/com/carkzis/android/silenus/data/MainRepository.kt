package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.LoadingState
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun addReview(review: Review): Flow<LoadingState<DocumentReference>>
}