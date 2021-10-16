package com.carkzis.android.silenus

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carkzis.android.silenus.welcome.WelcomeFragment
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    // TODO: We need to load the emulator with no user logged in.
//    @Test
//    fun welcomeScreen_notLoggedIn_navigateToLoginScreen() {
//        // On the welcome screen.
//        val navController = mock(NavController::class.java)
//        launchFragmentInHiltContainer<WelcomeFragment>(Bundle()) {
//            navController.setGraph(R.navigation.navigation)
//            Navigation.setViewNavController(requireView(), navController)
//        }
//
//        // Verify that we navigate to the Login Screen().
//        verify(navController).navigate(
//            WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
//        )
//    }

}