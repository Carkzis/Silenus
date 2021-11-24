package com.carkzis.android.silenus.review

import android.widget.RatingBar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.carkzis.android.silenus.DataBindingIdlingResource
import com.carkzis.android.silenus.MainActivity
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.monitorActivity
import com.schibsted.spain.barista.assertion.BaristaAssertions.assertAny
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import com.schibsted.spain.barista.internal.viewaction.RatingBarActions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EditReviewFragmentTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /*
    Review details used for testing.
    Ensure it is first item in Firestore.
    */
    private val rating = 5.0f
    private val barName = "Actual Bar"
    private val description = "Best location."

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

    // TODO: This. We can do more than with adding a new one, as we already have a location.

    private fun standardEditTextSetUp() {

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

        onView(withId(R.id.single_name_bar_text))
            .check(matches(withText(containsString("Actual Bar"))))
        onView(withId(R.id.single_location_text))
            .check(matches(withText(containsString("54 Wissey Way"))))
        onView(withId(R.id.single_description_text))
            .check(matches(withText(containsString("Best location."))))

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())
    }

    @Test
    fun editReviewFragment_noFieldsAmended_returnToSingleReviewFragment() {
        standardEditTextSetUp()

        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())

        onView(withId(R.id.single_name_bar_text))
            .check(matches(withText(containsString("Actual Bar"))))
        onView(withId(R.id.single_location_text))
            .check(matches(withText(containsString("54 Wissey Way"))))
        onView(withId(R.id.single_description_text))
            .check(matches(withText(containsString("Best location."))))
    }

    @Test
    fun editReviewFragment_barNameRemoved_editRequestNotAllowed() {
        standardEditTextSetUp()

        // Remove the bar name.
        onView(withId(R.id.edit_name_bar_edittext))
            .perform(replaceText(""))

        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        onView(withId(R.id.single_name_bar_text))
            .check(doesNotExist())
    }

    @Test
    fun editReviewFragment_descriptionRemoved_descriptionEditedInFirestore() {
        standardEditTextSetUp()

        // Remove the description.
        onView(withId(R.id.edit_description_bar_edittext))
            .perform(replaceText(""))

        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Check that the description is now empty.
        onView(withId(R.id.single_description_text))
            .check(matches(withText("")))

        /* Restore to original values. */

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())
        // Enter the global description used in our tests.
        onView(withId(R.id.edit_description_bar_edittext))
            .perform(replaceText(description))
        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())
    }

    @Test
    fun editReviewFragment_descriptionEdited_descriptionEditedInFirestore() {
        val newDescription = "No, please, change me back!"
        standardEditTextSetUp()

        // Remove the description.
        onView(withId(R.id.edit_description_bar_edittext))
            .perform(replaceText(newDescription))

        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Check that the description is now empty.
        onView(withId(R.id.single_description_text))
            .check(matches(withText(newDescription)))

        /* Restore to original values. */

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())
        // Enter the global description used in our tests.
        onView(withId(R.id.edit_description_bar_edittext))
            .perform(replaceText(description))
        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())
    }

    @Test
    fun editReviewFragment_ratingEdited_ratingEditedInFirestore() {
        val newRating = 3.0f
        standardEditTextSetUp()

        // Check that the rating bar is at the correct value.
        assertAny<RatingBar>(withId(R.id.edit_bar_rating_bar)) {
            it.rating == rating
        }

        onView(withId(R.id.edit_bar_rating_bar))
            .perform(
                RatingBarActions.setRatingBarRatingTo(newRating))

        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Check that the rating bar is at the new value.
        assertAny<RatingBar>(withId(R.id.single_rating_bar)) {
            it.rating == newRating
        }

        /* Restore to original values. */

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())
        // Enter the global description used in our tests.
        onView(withId(R.id.edit_bar_rating_bar))
            .perform(
                RatingBarActions.setRatingBarRatingTo(rating))
        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())
    }

    @Test
    fun editReviewFragment_barNameEdited_ratingEditedInFirestore() {
        val newBarName = "Factual Car"
        standardEditTextSetUp()

        // Remove the description.
        onView(withId(R.id.edit_name_bar_edittext))
            .perform(replaceText(newBarName))

        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())

        // Sleep so we know the "network" call loads.
        BaristaSleepInteractions.sleep(1000)

        // Check that the description is now empty.
        onView(withId(R.id.single_name_bar_text))
            .check(matches(withText(newBarName)))

        /* Restore to original values. */

        // Click the edit review menu button.
        onView(withId(R.id.edit_rev_menu_button))
            .perform(click())
        // Enter the global description used in our tests.
        onView(withId(R.id.edit_name_bar_edittext))
            .perform(replaceText(barName))
        // Click the confirm edit menu button.
        onView(withId(R.id.edit_rev_confirm_menu_button))
            .perform(click())
    }

}