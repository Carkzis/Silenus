package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.Constants
import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getCollectionName
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(private val firestore: FirebaseFirestore): UserRepository {

    override suspend fun addUser() = flow {

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        val newUser = getUserDetails().first
        val uId = getUserDetails().second
        val users = firestore.collection(getCollectionName(Constants.USERS))

        // This ensures we await the result of the query before we emit again.
        val result = suspendCoroutine<Int> { cont ->

            users.document(uId).set(newUser)
                .addOnSuccessListener { cont.resume(R.string.user_added)}
                .addOnFailureListener { throw Exception() }
        }

        emit(LoadingState.Success<Int>(result))

    }.catch {

        emit(LoadingState.Error(R.string.error, Exception())) // Emit the error if we get here...

    }.flowOn(Dispatchers.IO)

    override fun getUsername() : String {
        return Firebase.auth.currentUser?.displayName ?: ""
    }

    override fun getUser() : FirebaseAuth {
        return Firebase.auth
    }

    private fun getUserDetails() : Pair<UserObject, String> {
        val userProfile = Firebase.auth.currentUser

        // Note: may not be a new user.
        val newUser = UserObject(
                name = userProfile?.displayName,
                email = userProfile?.email,
                isAdmin = false,
                ratings = 0,
                joinDate = Timestamp.now()
            )
        val uId = userProfile!!.uid
        return Pair(newUser, uId)
    }

}