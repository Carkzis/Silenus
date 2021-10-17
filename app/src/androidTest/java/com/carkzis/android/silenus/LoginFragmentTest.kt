package com.carkzis.android.silenus


import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    /*
    TODO: Note, there is currently no way to Espresso with the FirebaseUI for logging in.
        Therefore, will need to do this manually.
        This test is for info only, but it will always fail.
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
    fun loginFragmentTest() {
        val materialButton = onView(
            allOf(
                withId(R.id.login_button), withText("Log in!"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navHostFragment),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.email_signin),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.email),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.email_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText.perform(
            scrollTo(),
            replaceText("marc.jowett@gmail.com"),
            closeSoftKeyboard()
        )

        val materialButton2 = onView(
            allOf(
                withId(R.id.button_next), withText("Next"),
                childAtPosition(
                    allOf(
                        withId(R.id.email_top_layout),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.password),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.password_layout),
                        0
                    ),
                    0
                )
            )
        )
        // This password is only used on my local emulator.
        textInputEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard())

        val materialButton3 = onView(
            allOf(
                withId(R.id.button_done), withText("Sign in"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        materialButton3.perform(scrollTo(), click())

        val textView = onView(
            allOf(
                withId(R.id.hub_text), withText("Your Silenus Hub"),
                withParent(withParent(withId(R.id.navHostFragment))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Your Silenus Hub")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
