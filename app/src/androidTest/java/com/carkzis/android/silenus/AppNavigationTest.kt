package com.carkzis.android.silenus

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.carkzis.android.silenus.review.YourReviewsAdapter
import com.carkzis.android.silenus.review.YourReviewsFragment
import com.carkzis.android.silenus.review.YourReviewsFragmentDirections
import com.carkzis.android.silenus.welcome.WelcomeFragment
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.Exception
import kotlin.math.exp

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppNavigationTest {

    /*
     Log in before running these tests. The logout functionality will be tested in
     a separate testing class.
     */

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        hiltRule.inject()
    }

    @After
    fun closeActivityScenerio() {
        activityScenario.close()
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Test
    fun welcomeFragment_navigateToYourReviewsFragment() = runBlockingTest {

        // Start on the WelcomeFragment (first screen if logged in).
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<WelcomeFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab)).perform(click())

        // Verify that we navigate to the YourReviews Fragment.
        verify(navController).navigate(
            WelcomeFragmentDirections.actionWelcomeFragmentToYourReviewsFragment()
        )
    }

    @Test
    fun yourReviewsFragment_navigateToSingleReviewFragment() = runBlockingTest {
        /*
         Given the description for the review we want to visit.
         Ensure it is first item in Firestore.
         */
        val description = "Best location."

        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<YourReviewsFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click the description.
        onView(withText(description)).perform(click())

        // Verify that we navigate to the SingleReviewFragment.
        verify(navController).navigate(
            YourReviewsFragmentDirections.actionYourReviewsFragmentToSingleReviewFragment()
        )
    }

    @Test
    fun yourReviewsFragment_navigateToMapFragment() = runBlockingTest {

        // Mock a navController.
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<YourReviewsFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(this.requireView(), navController)
            // Use the following with TestNavHostController.
            navController.setCurrentDestination(R.id.yourReviewsFragment)
        }

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click the map.
        onView(withId(R.id.your_reviews_recylerview))
            .perform(RecyclerViewActions.actionOnItemAtPosition<YourReviewsAdapter
            .YourReviewsViewHolder>(0, clickRecyclerViewChildView(R.id.your_rev_map)))

        assertThat(navController.currentDestination?.id, `is`(R.id.mapsFragment))

    }



}