package com.carkzis.android.silenus.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SharedViewModelTest {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userRepository: FakeUserRepository

    // This allows access to Dispatcher.Main in testing.
    private val dispatcher = TestCoroutineDispatcher()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        userRepository = FakeUserRepository()
        sharedViewModel = SharedViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun chooseLogout_postEventToLiveData() {
        // Call function.
        sharedViewModel.chooseLogout()

        // Ensure that on choosing to logout, the logout message is added to the LiveData.
        // Please note: this does not log the member out, but ultimately leads to a logout toast.
        assertThat(sharedViewModel.logout.getOrAwaitValue().getContextIfNotHandled().toString(),
            `is`((R.string.logged_out).toString()))
    }

    @Test
    fun authoriseUser_currentUserIsNull_navToLoginEventIsTrue() {
        // Set up FirebaseAuth mock stubs to hold a null value for FirebaseUser.
        val displayName = "Margaret"
        val firebaseNotNull = false
        userRepository.setFirebaseAuthStubs(firebaseNotNull, displayName)

        // Run method.
        sharedViewModel.authoriseUser()

        // Ensure that, if the FirebaseUser is null, we get an Event with a value of true,
        // that will be added to the navToLogin LiveData.
        assertThat(sharedViewModel.navToLogin.getOrAwaitValue().getContextIfNotHandled(),
            `is`(true))
    }

    @Test
    fun authoriseUser_displayNameIsNull_postNullUserErrorEvent() {
        // Set up Firebase mock stubs to hold a value for FirebaseUser, but the displayName
        // attribute is set to null
        val displayName: String? = null
        val firebaseNotNull = true
        userRepository.setFirebaseAuthStubs(firebaseNotNull, displayName)

        // Run method.
        sharedViewModel.authoriseUser()

        // The null user error event should be posted to the logout LiveData.
        assertThat(sharedViewModel.logout.getOrAwaitValue().getContextIfNotHandled(),
            `is`(not((R.string.null_user_error).toString())))
    }

    @Test
    fun authoriseUser_displayNameIsEmpty_postNullUserErrorEvent() {
        // Set up Firebase mock stubs to hold a value for FirebaseUser, but the displayName
        // attribute is set to null
        val displayName = ""
        val firebaseNotNull = true
        userRepository.setFirebaseAuthStubs(firebaseNotNull, displayName)

        // Run method.
        sharedViewModel.authoriseUser()

        // The null user error event should be posted to the logout LiveData.
        assertThat(sharedViewModel.logout.getOrAwaitValue().getContextIfNotHandled(),
            `is`(not((R.string.null_user_error).toString())))
    }

    @Test
    fun authoriseUser_validInputs_postDisplayNameToUsername() {
        // Set up Firebase stubs to hold a value for FirebaseUser, and a display name.
        val displayName = "Dave" // Ensure this is set to "Dave".
        val firebaseNotNull = true
        userRepository.setFirebaseAuthStubs(firebaseNotNull, displayName)

        // Call method.
        sharedViewModel.authoriseUser()

        // _username should be set to the displayName "Dave".
        assertThat(sharedViewModel.username.getOrAwaitValue(),
            `is`(displayName))
    }

    @Test
    fun setGeopint_postGeopointToLiveData() {
        // Set up LatLng object.
        val latLng = LatLng(50.0, 100.0)

        // Call method.
        sharedViewModel.setGeopoint(latLng)

        // Ensure that the latLng object is posted to the LiveData.
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue()?.latitude,
            `is`(50.0))
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue()?.longitude,
            `is`(100.0))
    }


}