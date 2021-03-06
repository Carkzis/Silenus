package com.carkzis.android.silenus.review

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
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
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SingleReviewFragmentTest {

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
    fun closeActivityScenario() {
        activityScenario.close()
    }

    @Test
    fun singleReviewFragment_correctItemsDisplayed() {
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

        onView(withId(R.id.single_name_bar_text)).check(matches(withText(containsString("Actual Bar"))))
        onView(withId(R.id.single_location_text)).check(matches(withText(containsString("54 Wissey Way"))))
        onView(withId(R.id.single_description_text)).check(matches(withText(containsString("Best location."))))
    }

}