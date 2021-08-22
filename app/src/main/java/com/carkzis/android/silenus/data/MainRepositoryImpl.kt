package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.Constants
import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getCollectionName
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainRepositoryImpl (private val firestore: FirebaseFirestore) : MainRepository {

    private val reviews = firestore.collection(getCollectionName(Constants.REVIEWS))

    /**
     * Add a review for a member into the database.
     */
    override suspend fun addReview(review: Review) = flow {

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        val reviewReference = suspendCoroutine<DocumentReference> { cont ->
            reviews.add(review).addOnCompleteListener { it ->
                cont.resume(it.result)
            }
        } // This ensures we await the result of the query before we emit again.

        emit(LoadingState.Success(R.string.review_added, reviewReference)) // Emit the result!

    }.catch {
        emit(LoadingState.Error(R.string.error, Exception())) // Emit the error if we get here...
    }.flowOn(Dispatchers.IO)
}