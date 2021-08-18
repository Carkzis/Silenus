package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class UserModel(
    val name: String?,
    val email: String?,
    @field:JvmField
    val isAdmin: Boolean,
    val ratings: Int,
    val joinDate: Timestamp
)

data class Review(
    val establishment: String?,
    val rating: Float?,
    val location: String?,
    val geo: String?,
    val description: String?,
    val dateAdded: Timestamp,
    val uid: String?
)