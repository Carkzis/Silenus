package com.carkzis.android.silenus.navigation


import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carkzis.android.silenus.MainActivity
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.launchFragmentInHiltContainer
import com.carkzis.android.silenus.review.AddReviewFragment
import com.carkzis.android.silenus.user.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginNavigationTest {

    /*
        Note, there is currently no way to use Espresso with the FirebaseUI for logging in.
        Here, we just check clicking the login button takes us to the next screen.
        Naturally, we should be logged out before doing this test!
     */

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        hiltRule.inject()
        activityTestRule.launchActivity(Intent(targetContext, MainActivity::class.java))
    }

    @Test
    fun loginFragment_navigateToLoginTypeSelection() {
        // On the login screen.
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<LoginFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the logout fab.
        onView(withId(R.id.login_button)).perform(click())

        // Verify that we have navigated to the screen for choosing type of login.
        onView(withId(R.id.google_signin)).check(matches(isDisplayed()))
        onView(withId(R.id.email_signin)).check(matches(isDisplayed()))
    }

    @Test(expected = RuntimeException::class)
    fun addReviewFragment_navigateToLoginFragment() {
        /*
        As we are automatically redirected to the login screen,
        a runtime exception should be thrown when trying to create launch the fragment in
        the container..
         */
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<AddReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(this.requireView(), navController)
            // Use the following with TestNavHostController.
            navController.setCurrentDestination(R.id.addReviewFragment)
        }
    }
}
