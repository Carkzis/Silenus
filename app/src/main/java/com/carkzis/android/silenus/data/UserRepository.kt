package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.User

interface UserRepository {
    fun addUser(addedUser: User, userId: String)
}