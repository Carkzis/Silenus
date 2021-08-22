package com.carkzis.android.silenus

import java.lang.Exception

/**
 * Used with flow to show the current loading status after a call to Firebase.
 */
sealed class LoadingState<out T> {
    data class Loading<T>(val message: Int) : LoadingState<T>()
    data class Success<T>(val message: Int, val data: T? = null) : LoadingState<T>()
    data class Error<T>(val message: Int, val exception: Exception) : LoadingState<T>()
}