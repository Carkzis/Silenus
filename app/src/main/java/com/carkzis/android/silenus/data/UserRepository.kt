package com.carkzis.android.silenus.data

interface UserRepository {
    suspend fun addUser()
    fun getUsername() : String
}