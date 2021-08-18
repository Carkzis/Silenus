package com.carkzis.android.silenus.data

interface MainRepository {
    suspend fun addReview(review: Review)
}