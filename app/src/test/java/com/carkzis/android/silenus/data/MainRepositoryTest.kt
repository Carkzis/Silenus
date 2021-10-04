package com.carkzis.android.silenus.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.LoadingState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class MainRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // We are using the FakeMainRepository for testing.
    private lateinit var mainRepository: FakeMainRepository

    // Fake review for use in test, initialise in @Before to recreate it for each test.
    private lateinit var fakeReview: NewReviewDO

    @Before
    fun createRepository() {
        // We will reuse this fakeReview.
        fakeReview = NewReviewDO(
            "Real Cheese Pizzeria",
            5.0f,
            "Manchester",
            mock(GeoPoint::class.java),
            "They really do have real cheese, just wish they would put it on the pizza.",
            Timestamp.now(),
            "123"
        )
        mainRepository = FakeMainRepository()
    }

    @Test
    fun addReview_error_receiveLoadingStateError() = runBlockingTest {
        // Set the failure of adding the review.
        mainRepository.failure = true
        // Test the response, if we get LoadingState.Error, isSuccessful will be false.
        var isSuccessful = true
        mainRepository.addReview(fakeReview).collect {
            isSuccessful = when (it) {
                is LoadingState.Error -> false
                else -> true
            }
        }
        // Assert that the addition of a review was not successful.
        assertThat(isSuccessful, `is`(false))
    }

    @Test
    fun addReview_success_receiveLoadingStateSuccess() = runBlockingTest {
        // Set the failure of adding the review.
        mainRepository.failure = false
        // Test the response, if we get LoadingState.Error, isSuccessful will be false.
        var isSuccessful = false
        mainRepository.addReview(fakeReview).collect {
            isSuccessful = when (it) {
                is LoadingState.Success -> true
                else -> false
            }
        }
        // Assert that the addition of a review was not successful.
        assertThat(isSuccessful, `is`(true))
    }

    @Test
    fun addReview_loading_receiveLoadingStateLoading() = runBlockingTest {
        // Set the loading variable to true.
        mainRepository.loading = true
        // Test the response, if we get LoadingState.Loading, isLoading with be true.
        var isLoading = false
        mainRepository.addReview(fakeReview).collect {
            isLoading = when (it) {
                is LoadingState.Loading -> true
                else -> false
            }
        }
        assertThat(isLoading, `is`(true))
    }

    @Test
    fun getYourReviews_error_receiveLoadingStateError() = runBlockingTest {
        // Set the failure of adding the review.
        mainRepository.failure = true
        // Test the response, if we get LoadingState.Error, isSuccessful will be false.
        var isSuccessful = true
        mainRepository.getYourReviews().collect {
            isSuccessful = when (it) {
                is LoadingState.Error -> false
                else -> true
            }
        }
        // Assert that the addition of a review was not successful.
        assertThat(isSuccessful, `is`(false))
    }

    @Test
    fun getYourReviews_success_receiveLoadingStateSuccessFiveReviews() = runBlockingTest {
        // We want to check if the returned list of reviews is
        var yourReviews = mutableListOf<YourReview>()
        // Set the failure of adding the review.
        mainRepository.failure = false
        // Test the response, if we get LoadingState.Error, isSuccessful will be false.
        var isSuccessful = false
        mainRepository.getYourReviews().collect {
            isSuccessful = when (it) {
                is LoadingState.Success -> {
                    yourReviews = it.data as MutableList<YourReview>
                    true
                }
                else -> false
            }
        }
        // Assert that the addition of a review was not successful.
        assertThat(isSuccessful, `is`(true))
        assertThat(yourReviews.size, `is`(5))
    }

    @Test
    fun getYourReviews_loading_receiveLoadingStateLoading() = runBlockingTest {
        // Set the loading variable to true.
        mainRepository.loading = true
        // Test the response, if we get LoadingState.Loading, isLoading with be true.
        var isLoading = false
        mainRepository.getYourReviews().collect {
            isLoading = when (it) {
                is LoadingState.Loading -> true
                else -> false
            }
        }
        assertThat(isLoading, `is`(true))
    }

}