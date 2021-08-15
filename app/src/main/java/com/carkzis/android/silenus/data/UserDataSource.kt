package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.User

interface UserDataSource {
    fun addUser(addedUser: User, userId: String)
}