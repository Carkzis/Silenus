package com.carkzis.android.silenus.review

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.carkzis.android.silenus.*
import com.schibsted.spain.barista.internal.viewaction.RatingBarActions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AddReviewFragmentTest {

    /*
    Adding a review is not being tested here, as it requires the MapFragment for the location.
    This requires a manual test.
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
    fun addReviewFragment_noFieldsCompleted_addingNewItemDisallowed() {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the add review menu button.
        onView(withId(R.id.add_rev_menu_button))
            .perform(click())

        // The add establishment title should display.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))

        // Without any items entered, you should not be able to add an item.
        onView(withId(R.id.add_rev_frag_menu_button))

        // The title should still be displayed, as we will not have gone anywhere.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))
    }

    @Test
    fun addReviewFragment_onlyTitleCompleted_addingNewItemDisallowed() {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the add review menu button.
        onView(withId(R.id.add_rev_menu_button))
            .perform(click())

        // The add establishment title should display.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))

        // Add a title.
        onView(withId(R.id.name_bar_edittext))
            .perform(typeText("Time Is Of The Essence"))

        // Without any items entered, you should not be able to add an item.
        onView(withId(R.id.add_rev_frag_menu_button))

        // The title should still be displayed, as we will not have gone anywhere.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))
    }

    @Test
    fun addReviewFragment_onlyRatingCompleted_addingNewItemDisallowed() {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the add review menu button.
        onView(withId(R.id.add_rev_menu_button))
            .perform(click())

        // The add establishment title should display.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))

        // Add a rating.
        onView(withId(R.id.add_rev_rating_bar))
            .perform(
                RatingBarActions.setRatingBarRatingTo(3.0f))

        // Without any items entered, you should not be able to add an item.
        onView(withId(R.id.add_rev_frag_menu_button))

        // The title should still be displayed, as we will not have gone anywhere.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))
    }

    @Test
    fun addReviewFragment_onlyDescriptionCompleted_addingNewItemDisallowed() {
        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click the reviews fab.
        onView(withId(R.id.reviews_fab))
            .perform(click())

        // Click the add review menu button.
        onView(withId(R.id.add_rev_menu_button))
            .perform(click())

        // The add establishment title should display.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))

        // Add a title.
        onView(withId(R.id.description_bar_edittext))
            .perform(typeText("But I am le tired."))

        // Without any items entered, you should not be able to add an item.
        onView(withId(R.id.add_rev_frag_menu_button))

        // The title should still be displayed, as we will not have gone anywhere.
        onView(withId(R.id.add_bar_text)).check(matches(isDisplayed()))
    }

}