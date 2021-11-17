package com.carkzis.android.silenus.welcome

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.android.silenus.DataBindingIdlingResource
import com.carkzis.android.silenus.MainActivity
import com.carkzis.android.silenus.monitorActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WelcomeFragmentTest {
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
    fun welcomeFragment_userNameDisplays() {
        val greeting = "Welcome, Marc Jowett"

        // Start on the WelcomeFragment (first screen if logged in).
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Confirm that the test review is not there.
        onView(withText(greeting))
            .check(matches(isDisplayed()))
    }

}