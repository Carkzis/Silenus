package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class UserObject(
    val name: String?,
    val email: String?,
    @field:JvmField
    val isAdmin: Boolean,
    val ratings: Int,
    val joinDate: Timestamp
)

data class NewReviewDO (
    val establishment: String?,
    val rating: Float?,
    val location: String?,
    val geo: GeoPoint?,
    val description: String?,
    val dateAdded: Timestamp?,
    val uid: String?,
    val deleted: Boolean = false
)

data class ReviewDO (
    val id: String?,
    val establishment: String?,
    val rating: Float?,
    val location: String?,
    val geo: GeoPoint?,
    val description: String?,
    val dateAdded: Timestamp?,
    val uid: String?,
    val deleted: Boolean = false
)

/**
 * Converts data object for NewReviewDO from Firebase to UI model.
 */
fun ReviewDO.toUIModel() : YourReview {
    return let {
        YourReview(
            documentId = it.id,
            establishment = it.establishment,
            rating = it.rating,
            location = it.location,
            description = it.description,
            dateAdded = it.dateAdded,
            geo = it.geo,
        )
    }
}