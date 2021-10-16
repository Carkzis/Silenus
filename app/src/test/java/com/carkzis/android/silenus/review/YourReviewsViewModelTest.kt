package com.carkzis.android.silenus.review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.FakeUserRepository
import com.carkzis.android.silenus.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class YourReviewsViewModelTest {

    private lateinit var yourReviewsViewModel: YourReviewsViewModel
    private lateinit var mainRepository: FakeMainRepository

    // This allows access to Dispatcher.Main in testing.
    private val dispatcher = TestCoroutineDispatcher()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mainRepository = FakeMainRepository()
        yourReviewsViewModel = YourReviewsViewModel(mainRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refreshReviews_failure_postFailureMessageToToastLiveData() = runBlockingTest {
        // Given that the request to refresh reviews will fail.
        mainRepository.failure = true

        // Call refreshReviews method.
        yourReviewsViewModel.refreshReviews()

        // Assert that an error message is collected, to be added to toastText via helper method.
        assertThat(yourReviewsViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.error))
    }

    @Test
    fun refreshReviews_success_postListOfYourReviewsToLiveData() = runBlockingTest {
        // Given that the request to refresh reviews will succeed.
        mainRepository.failure = false

        // Call refreshReviews method.
        yourReviewsViewModel.refreshReviews()

        // Assert that a list of five (mock) reviews is posted to the associated LiveData.
        assertThat(yourReviewsViewModel.yourReviews.getOrAwaitValue().size, `is`(5))
    }

    /*
    TODO: We need to add tests for the responses to LoadingState.Loading emissions,
        which have not yet been fully implemented, currently only logging a message to Logcat.
     */

}