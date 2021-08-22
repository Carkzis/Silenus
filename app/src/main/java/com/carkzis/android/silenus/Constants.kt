package com.carkzis.android.silenus

enum class Constants {
    REVIEWS, USERS
}

fun getCollectionName(name: Constants) : String {
    return when (name) {
        Constants.REVIEWS -> "reviews"
        Constants.USERS -> "users"
    }
}