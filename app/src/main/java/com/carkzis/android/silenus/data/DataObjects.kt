package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

// TODO: This is not a UI model despite the name (needs changing), but will need a dedicated model.
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
    val geo: GeoPoint?,
    val description: String?,
    val dateAdded: Timestamp,
    val uid: String?
)