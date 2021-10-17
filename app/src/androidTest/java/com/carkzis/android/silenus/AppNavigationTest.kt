package com.carkzis.android.silenus

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.android.silenus.welcome.WelcomeFragment
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.Exception

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppNavigationTest {

    /*
     TODO: Log in before running these tests. The logout functionality will be tested in
        a separate testing class.
     */


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    // TODO: Move this to its own "LoginTest" class.
//    @Test
//    fun welcomeScreen_navigateToLoginScreen() {
//        // On the welcome screen.
//        val navController = mock(NavController::class.java)
//        launchFragmentInHiltContainer<WelcomeFragment>(Bundle()) {
//            navController.setGraph(R.navigation.navigation)
//            Navigation.setViewNavController(requireView(), navController)
//        }
//
//        // Click the logout fab.
//        onView(withId(R.id.logout_fab)).perform(click())
//
//        // Verify that we navigate to the Login screen.
//        verify(navController).navigate(
//            WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
//        )
//    }

    @Test
    fun welcomeScreen_navigateToYourReviewsFragment() {
        // On the welcome screen.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<WelcomeFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click on the reviews fab.
        onView(withId(R.id.reviews_fab)).perform(click())

        // Verify that we navigate to the YourReviews Fragment.
        verify(navController).navigate(
            WelcomeFragmentDirections.actionWelcomeFragmentToYourReviewsFragment()
        )
    }

}