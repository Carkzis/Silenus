package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.User

object UserLocalDataSource : UserDataSource {

    override fun addUser(addedUser: User, userId: String) {
        // Not required for the local data source.
    }

}