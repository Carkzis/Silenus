package com.carkzis.android.silenus.review

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.android.silenus.DataBindingIdlingResource
import com.carkzis.android.silenus.MainActivity
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.monitorActivity
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class YourReviewsFragmentTest {

    /*
    Note: As navigation is tested in AppNavigationTest, this is just testing the search bar.
     */

    /*
     Log in before running these tests. The logout functionality will be tested in
     a separate testing class.
     */


    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val dataBindingIdlingResource = DataBindingIdlingResource()

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

    @Test
    fun searchBar_searchTermForExistingReview_reviewDisplays() = runBlockingTest {
        val searchText = "Actua"

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click on the search bar.
        onView(withId(R.id.searchview))
            .perform(typeText(searchText))

        // Sleep so we know the search completes.
        BaristaSleepInteractions.sleep(1000)

        // Confirm that the test review is not there.
        onView(withText("Actual Bar")).check(matches(isDisplayed()))
    }

    @Test
    fun searchBar_searchTermForNonExistentReview_noReviewDisplays() = runBlockingTest {
        val searchText = "Actua1"

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Click on the search bar.
        onView(withId(R.id.searchview))
            .perform(typeText(searchText))

        // Sleep so we know the search completes.
        BaristaSleepInteractions.sleep(1000)

        // Confirm that the test review is not there.
        onView(withText("Actual Bar")).check(doesNotExist())
    }

}