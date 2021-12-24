package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.utils.LoadingState
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.utils.Constants
import com.carkzis.android.silenus.utils.getCollectionName
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth): UserRepository {

    /**
     * Add a user to the database, emits Loading and Success or Error states.
     */
    override suspend fun addUser() = flow {

        emit(LoadingState.Loading(R.string.loading)) // Loading!

        val newUser = getUserDetails().first
        val uId = getUserDetails().second
        val users = firestore.collection(getCollectionName(Constants.USERS))

        // Set the new user to the database.
        users.document(uId).set(newUser)

        emit(LoadingState.Success<Int>(R.string.user_added))

    }.catch {

        emit(LoadingState.Error(R.string.error, Exception())) // Emit the error if we get here...

    }.flowOn(Dispatchers.IO)

    /**
     * Retrieve the logging in username as a String.
     */
    override fun getUsername() : String {
        return firebaseAuth.currentUser?.displayName ?: ""
    }

    /**
     * Retrieves a Firebase object, which stores information relating to the user.
     */
    override fun getUser() : FirebaseAuth {
        return firebaseAuth
    }

    /**
     * Retrieve the user details as a Pair of a UserObject, which is an object that
     * can be entered into the Firestore database, and a String, which stores the uid of the
     * user which will be used as the document ID for said user.
     */
    private fun getUserDetails() : Pair<UserObject, String> {
        val userProfile = firebaseAuth.currentUser

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