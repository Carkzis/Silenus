package com.carkzis.android.silenus

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.android.silenus.welcome.WelcomeFragment
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LogoutNavigationTest {

    /*
    TODO: Log in before running this test.
    */


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }
    @After
    fun closeActivityScenerio() = runBlockingTest {
        activityScenario.close()
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Test
    fun welcomeFragment_navigateToLoginScreen() = runBlockingTest {

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)


        // On the welcome screen.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<WelcomeFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the logout fab.
        onView(withId(R.id.logout_fab)).perform(click())

        // Verify that we navigate to the Login screen.
        verify(navController).navigate(
            WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
        )
    }
}