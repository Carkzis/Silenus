package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(): Flow<LoadingState<Boolean>>
    fun getUsername() : String
    fun getUser() : FirebaseAuth
}