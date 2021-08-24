package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class YourReview(
    val establishment: String,
    val rating: Float,
    val location: String,
    val geo: GeoPoint,
    val description: String?,
    val dateAdded: Timestamp,
)