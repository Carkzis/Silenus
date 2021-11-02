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
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.carkzis.android.silenus.review.*
import com.carkzis.android.silenus.welcome.WelcomeFragment
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import com.google.android.gms.maps.MapFragment
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

        // Using a TestNavHostController.
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

        // Verify that we navigate to the maps fragment.
        assertThat(navController.currentDestination?.id, `is`(R.id.mapsFragment))

    }

    @Test
    fun yourReviewsFragment_navigateToAddReviewFragment() = runBlockingTest {

        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<YourReviewsFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the "+" button.
        onView(withId(R.id.add_rev_menu_button)).perform(click())

        // Verify that we navigate to the AddReviewFragment.
        verify(navController).navigate(
            YourReviewsFragmentDirections.actionYourReviewsFragmentToAddReviewFragment()
        )
    }

    @Test
    fun addReviewFragment_navigateToMapFragment() = runBlockingTest {
        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<AddReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the location edittext.
        onView(withId(R.id.location_bar_edittext)).perform(click())

        // Verify that we navigate to the MapFragment.
        verify(navController).navigate(
            AddReviewFragmentDirections.actionAddReviewFragmentToMapsFragment()
        )

    }

    /*
    "addReviewFragment_navigateToYourReviewsFragment" will be tested in the AddReviewFragment
    instrumented test, as it requires data entry.
     */

    @Test
    fun singleReviewFragment_navigateToEditReviewFragment() = runBlockingTest {
        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<SingleReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button)).perform(click())

        // Verify that we navigate to the EditReviewFragment.
        verify(navController).navigate(
            SingleReviewFragmentDirections.actionSingleReviewFragmentToEditReviewFragment()
        )
    }

    @Test
    fun singleReviewFragment_navigateToMapFragment() = runBlockingTest {
        // Mock a navController.
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<SingleReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(this.requireView(), navController)
            // Use the following with TestNavHostController.
            navController.setCurrentDestination(R.id.singleReviewFragment)
        }

        // Click the map menu button.
        onView(withId(R.id.view_location_menu_button)).perform(click())

        // Verify that we navigate to the MapFragment.
        assertThat(navController.currentDestination?.id, `is`(R.id.mapsFragment))
    }

    /*
    "editReviewFragment_navigateToSingleFragment" will be tested in the EditReviewFragment
    instrumented test, as it requires data entry.
     */

    @Test
    fun editReviewFragment_navigateToMapFragment() = runBlockingTest {
        // Mock a navController.
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<EditReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(this.requireView(), navController)
            // Use the following with TestNavHostController.
            navController.setCurrentDestination(R.id.editReviewFragment)
        }

        // Click the map menu button.
        onView(withId(R.id.edit_location_bar_edittext)).perform(click())

        // Verify that we navigate to the MapFragment.
        assertThat(navController.currentDestination?.id, `is`(R.id.mapsFragment))
    }

    @Test
    fun mapFragment_navigateToAddReviewFragment() = runBlockingTest {
        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<AddReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Sleep to allow map to load.
        BaristaSleepInteractions.sleep(1000)

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button)).perform(longClick())

        // Verify that we navigate to the EditReviewFragment.
        verify(navController).navigate(
            SingleReviewFragmentDirections.actionSingleReviewFragmentToEditReviewFragment()
        )
    }

}