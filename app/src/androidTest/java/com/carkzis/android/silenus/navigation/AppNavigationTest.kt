package com.carkzis.android.silenus.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.android.silenus.*
import com.carkzis.android.silenus.review.*
import com.carkzis.android.silenus.welcome.WelcomeFragment
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
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
    fun editReviewFragment_navigateToSingleReviewFragment() = runBlockingTest {
        // Mock a navController.
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<EditReviewFragment>(Bundle()) {
            navController.setGraph(R.navigation.navigation)
            Navigation.setViewNavController(requireView(), navController)
        }

        // Click the quit button.
        onView(withId(R.id.edit_rev_quit_menu_button)).perform(click())

        verify(navController).navigate(
            EditReviewFragmentDirections.actionEditReviewFragmentToSingleReviewFragment()
        )
    }

    /*
    The MapFragment will be testing as part of the AddReviewFragment and EditReviewFragments
    as it's use is tightly coupled with them.
     */

    @Test
    fun yourReviewsFragment_backButton() = runBlockingTest {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the WelcomeFragment.
        onView(withId(R.id.welcome_user)).check(matches(isDisplayed()))
    }

    @Test
    fun singleReviewFragment_backButton() = runBlockingTest {
        /*
         Given the description for the review we want to visit.
         Ensure it is first item in Firestore.
         */
        val description = "Best location."

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click the description.
        onView(withText(description)).perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the YourReviewsFragment.
        onView(withId(R.id.your_reviews_recylerview)).check(matches(isDisplayed()))
    }

    @Test
    fun addReviewFragment_backButton() = runBlockingTest {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the add review menu button.
        onView(withId(R.id.add_rev_menu_button))
            .perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the YourReviewsFragment.
        onView(withId(R.id.your_reviews_recylerview)).check(matches(isDisplayed()))
    }

    @Test
    fun editReviewFragment_backButton() = runBlockingTest {
        /*
         Given the description for the review we want to visit.
         Ensure it is first item in Firestore.
         */
        val description = "Best location."

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click the description.
        onView(withText(description)).perform(click())

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the YourReviewsFragment.
        onView(withId(R.id.edit_rev_menu_button)).check(matches(isDisplayed()))
    }

    @Test
    fun mapFragment_backButtonFromYourReviewsFragment() = runBlockingTest {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the map.
        onView(withId(R.id.your_reviews_recylerview))
            .perform(RecyclerViewActions.actionOnItemAtPosition<YourReviewsAdapter
            .YourReviewsViewHolder>(0, clickRecyclerViewChildView(R.id.your_rev_map)))

        // Click the back button.
        pressBack()

        // Confirm we end up back at the WelcomeFragment.
        onView(withId(R.id.your_reviews_recylerview)).check(matches(isDisplayed()))
    }

    @Test
    fun mapFragment_backButtonFromAddReviewFragment() = runBlockingTest {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the add review menu button.
        onView(withId(R.id.add_rev_menu_button))
            .perform(click())

        // Click on the description edittext.
        onView(withId(R.id.location_bar_edittext))
            .perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the YourReviewsFragment.
        onView(withId(R.id.description_bar_edittext)).check(matches(isDisplayed()))
    }

    @Test
    fun mapFragment_backButtonFromSingleReviewFragment() = runBlockingTest {
        /*
         Given the description for the review we want to visit.
         Ensure it is first item in Firestore.
         */
        val description = "Best location."

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click the description.
        onView(withText(description)).perform(click())

        // Click the map icon.
        onView(withId(R.id.view_location_menu_button))
            .perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the YourReviewsFragment.
        onView(withId(R.id.single_name_bar_text)).check(matches(isDisplayed()))
    }

    @Test
    fun mapFragment_backButtonFromEditReviewFragment() = runBlockingTest {
        /*
         Given the description for the review we want to visit.
         Ensure it is first item in Firestore.
         */
        val description = "Best location."

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click the description.
        onView(withText(description)).perform(click())

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())

        // Click the map icon.
        onView(withId(R.id.edit_location_bar_edittext))
            .perform(click())

        // Click the back button.
        pressBack()

        // Confirm we end up back at the YourReviewsFragment.
        onView(withId(R.id.edit_name_bar_row)).check(matches(isDisplayed()))
    }


}