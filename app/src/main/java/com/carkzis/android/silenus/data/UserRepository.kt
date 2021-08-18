package com.carkzis.android.silenus.data

import com.google.firebase.auth.FirebaseAuth

interface UserRepository {
    suspend fun addUser()
    fun getUsername() : String
    fun getUser() : FirebaseAuth
}