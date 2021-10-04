package com.carkzis.android.silenus.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getOrAwaitValue
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
    fun authoriseUser() {
        // TODO: May need to rethink this method to make it more testable.
    }

}