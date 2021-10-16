package com.carkzis.android.silenus.welcome

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.FakeUserRepository
import com.carkzis.android.silenus.getOrAwaitValue
import com.carkzis.android.silenus.review.YourReviewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WelcomeViewModelTest {

    private lateinit var welcomeViewModel: WelcomeViewModel
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
        welcomeViewModel = WelcomeViewModel(userRepository)
    }

    @Test
    fun setUserName_userNameObtainedFromRepository_userNamePostedToLiveData() {
        // Given a username as a string, by setting the value in user repository.
        val userName = "Dave" // Note: getUsername() stub in FakeUserRepository.
        userRepository.setFirebaseAuthStubs(true, userName)

        // Call method.
        welcomeViewModel.setUsername()

        // Assert that the correct value is posted to the LiveData.
        // Note: Null username is tested within SharedViewModel.
        assertThat(welcomeViewModel.username.getOrAwaitValue(), `is`(userName))
    }

    @Test
    fun toastMe_postEventToLiveData() {
        // Set up string to be Toast(ed).
        val toastString = "Toasty."

        // Call method.
        welcomeViewModel.toastMe(toastString)

        // Ensure that the Toast message is added to the LiveData (within an Event wrapper).
        assertThat(
            welcomeViewModel.toastText.getOrAwaitValue().getContextIfNotHandled().toString(),
            `is`(toastString)
        )
    }

}