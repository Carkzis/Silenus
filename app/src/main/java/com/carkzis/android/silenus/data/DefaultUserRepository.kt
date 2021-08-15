package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.User

class DefaultUserRepository(
    private val userLocalDataSource: UserDataSource,
    private val userRemoteDataSource: UserDataSource): UserRepository {

    override fun addUser(addedUser: User, userId: String) {
        userRemoteDataSource.addUser(addedUser, userId)
    }

}