package com.carkzis.android.silenus.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.FakeUserRepository
import com.carkzis.android.silenus.getOrAwaitValue
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userRepository: FakeUserRepository
    private lateinit var mainRepository: FakeMainRepository

    // This allows access to Dispatcher.Main in testing.
    private val dispatcher = TestCoroutineDispatcher()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        userRepository = FakeUserRepository()
        loginViewModel = LoginViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun authoriseUser_currentUserIsNotNull_postEventToNavToWelcomeLiveData() {
        // Given that the FirebaseUser currentUser user value is not null.
        userRepository.setFirebaseAuthStubs(true, "Marc")

        // Call the method.
        loginViewModel.authoriseUser()

        // Assert that the Event is posted to the navToWelcome LiveData.
        assertThat(loginViewModel.navToWelcome.getOrAwaitValue().getContextIfNotHandled(),
            `is`(true))
    }

    @Test
    fun authoriseUser_currentUserIsNull_noEventPostedToNavToWelcomeLiveData() {
        // Given that the FirebaseUser currentUser is null, as we have not set it.

        // Call the method.
        loginViewModel.authoriseUser()

        // Assert that the navToWelcomePosted is still showing as false.
        assertThat(loginViewModel.navToWelcomePosted, `is`(false))
    }

    @Test
    fun onSignInResult_resultOK_postEventToNavToWelcomeLiveData() {
        // Given a FirebaseAuthUIAuthenticationResult with a successful result (-1).
        val result = mock(FirebaseAuthUIAuthenticationResult::class.java)
        `when`(result.resultCode).thenReturn(-1)

        // Call the method.
        loginViewModel.onSignInResult(result)

        // Assert that the Event is posted to the navToWelcome LiveData.
        assertThat(loginViewModel.navToWelcome.getOrAwaitValue().getContextIfNotHandled(),
            `is`(true))
    }

    @Test
    fun onSignInResult_resultCancelled_postEventToNavToWelcomeLiveData() {
        // Given a FirebaseAuthUIAuthenticationResult with a cancelled result (0).
        val result = mock(FirebaseAuthUIAuthenticationResult::class.java)
        `when`(result.resultCode).thenReturn(0)

        // Call the method.
        loginViewModel.onSignInResult(result)

        // Assert an error message has be posted to the toastText LiveData.
        assertThat(loginViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.sign_in_failed))
    }
}