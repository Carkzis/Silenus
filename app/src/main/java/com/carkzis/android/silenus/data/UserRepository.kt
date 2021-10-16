package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.utils.LoadingState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(): Flow<LoadingState<Int>>
    fun getUsername() : String
    fun getUser() : FirebaseAuth
}