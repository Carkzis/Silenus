package com.carkzis.android.silenus.utils

enum class Constants {
    REVIEWS, USERS
}

/**
 * Returns a string representation of a constant from the Constants enum class.
 */
fun getCollectionName(name: Constants) : String {
    return when (name) {
        Constants.REVIEWS -> "reviews"
        Constants.USERS -> "users"
    }
}