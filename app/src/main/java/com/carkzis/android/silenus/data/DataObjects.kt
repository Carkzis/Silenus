package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

/**
 * Object for transferring data relating to member details to and from the Firestore database.
 */
data class UserObject(
    val name: String?,
    val email: String?,
    @field:JvmField
    val isAdmin: Boolean,
    val ratings: Int,
    val joinDate: Timestamp
)

/**
 * Object for transferring data relating to a newly added member review to the Firestore database.
 */
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

/**
 * Object for transferring data relating to an existing review to and from the Firestore database.
 */
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
 * Converts the data object from Firebase (ReviewDO) to its respective UI model (YourReview).
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