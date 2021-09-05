package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

/**
 * Models to display data to the UI.
 */

data class YourReview(
    val establishment: String? = null,
    val rating: Float? = null,
    val location: String? = null,
    val description: String? = null,
    val dateAdded: Timestamp? = null,
    val geo: GeoPoint? = null,
)