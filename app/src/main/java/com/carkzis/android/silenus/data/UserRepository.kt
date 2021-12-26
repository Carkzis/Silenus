package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.utils.LoadingState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the User Repository, which is used to abstract access to user data
 * between the Firestore database, and the UI.
 */
interface UserRepository {
    suspend fun addUser(): Flow<LoadingState<Int>>
    fun getUsername() : String
    fun getUser() : FirebaseAuth
}