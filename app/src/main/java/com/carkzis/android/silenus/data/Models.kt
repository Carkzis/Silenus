package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

/**
 * Models to display data to the UI.
 */

data class YourReview(
    val id: String,
    val establishment: String,
    val rating: Float,
    val location: String,
    val geo: GeoPoint,
    val description: String?,
    val dateAdded: Timestamp,
)