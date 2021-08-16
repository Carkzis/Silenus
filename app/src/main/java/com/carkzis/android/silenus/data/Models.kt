package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp

data class UserModel(
    val name: String?,
    val email: String?,
    @field:JvmField
    val isAdmin: Boolean,
    val ratings: Int,
    val joinDate: Timestamp
)