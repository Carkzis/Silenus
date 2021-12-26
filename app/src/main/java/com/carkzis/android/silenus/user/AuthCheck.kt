package com.carkzis.android.silenus.user

/**
 * Interface for methods relating to logging out.
 */
interface AuthCheck {
    /**
     * This observes when the user should be logged out, and directed to the LoginFragment.
     */
    fun setUpLogout()
    /**
     * Sets up the navigation to the LoginFragment.
     */
    fun setUpNavigateToLogin()
}