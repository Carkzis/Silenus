package com.carkzis.android.silenus

open class Event <out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow read but not write

    // Returns the content and prevents its use again.
    fun getContextIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}