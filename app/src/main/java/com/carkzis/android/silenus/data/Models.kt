package com.carkzis.android.silenus.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

/**
 * Data class for displaying user review data to the UI.
 */
data class YourReview(
    val id: DocumentId? = null,
    val establishment: String? = null,
    val rating: Float? = null,
    val location: String? = null,
    val description: String? = null,
    val dateAdded: Timestamp? = null,
    val geo: GeoPoint? = null,
)

fun YourReview.toDataObject(userId: String) : ReviewDO {
    return let {
        ReviewDO(
            id = it.id,
            establishment = it.establishment,
            rating = it.rating,
            location = it.location,
            description = it.description,
            dateAdded = it.dateAdded,
            geo = it.geo,
            uid = userId
        )
    }
}