package com.carkzis.android.silenus

import java.lang.Exception

/**
 * Used with flow to show the current loading status after a call to Firebase.
 */
sealed class LoadingState<out T> {
    data class Loading<T>(val data: T) : LoadingState<T>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error<T>(val data: T, val exception: Exception) : LoadingState<T>()
}