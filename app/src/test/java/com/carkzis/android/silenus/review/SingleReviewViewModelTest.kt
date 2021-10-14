package com.carkzis.android.silenus.review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.getOrAwaitValue
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
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
import org.mockito.Mockito
import org.mockito.Mockito.mock

class SingleReviewViewModelTest {

    private lateinit var singleReviewViewModel: SingleReviewViewModel
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
        singleReviewViewModel = SingleReviewViewModel(mainRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setUpRev_yourReviewPostedToLiveData() {
        // Given a YourReview object.
        val yourReview = YourReview( // Does not need any entries, as entries not being tested.
            "123", // To differentiate from "notYourReview".
            null,
            null,
            null,
            null,
            null,
            null)

        val notYourReview = YourReview( // Does not need any entries, as entries not being tested.
            "321", // To differentiated from "yourReview".
            null,
            null,
            null,
            null,
            null,
            null)

        // Call method posting YourReview.
        singleReviewViewModel.setUpRev(yourReview)

        // Assert that the review used as an argument was posted to the associated LiveData.
        assertThat(singleReviewViewModel.yourReview.getOrAwaitValue(), `is`(yourReview))
        // Assert that the other review was not posted to the LiveData.
        assertThat(singleReviewViewModel.yourReview.getOrAwaitValue(), `is`(not(notYourReview)))
    }

    @Test
    fun getGeo_yourReviewHoldsValidGeoPoint_returnArrayOfCorrectLatitudeAndLongitude() {
        // Given a mock GeoPoint that returns specified latitude and longitude values.
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        Mockito.`when`(geoPoint.latitude).thenReturn(10.0)
        Mockito.`when`(geoPoint.longitude).thenReturn(50.0)

        // And a review, that uses the geoPoint.
        val yourReview = YourReview( // Only geoPoint being tested.
            null,
            null,
            null,
            null,
            null,
            null,
            geoPoint)

        // Post the review to the LiveData.
        singleReviewViewModel.setUpRev(yourReview)

        // Call the method to return an array with two values.
        val latLngArray = singleReviewViewModel.getGeo()

        /*
         Assert that the first value in the array is a string of the latitude, and the
         second value in the array is a string of the longitude.
         */
        assertThat(latLngArray.first(), `is`("10.0"))
        assertThat(latLngArray.last(), `is`("50.0"))
    }

    @Test
    fun getGeo_noGeoPointInYourReview_returnArrayOfNullValues() {
        // Given a review with a null GeoPoint parameter.
        val yourReview = YourReview( // Only geoPoint being tested.
            null,
            null,
            null,
            null,
            null,
            null,
            null)

        // Post the review to the LiveData.
        singleReviewViewModel.setUpRev(yourReview)

        // Call the method to return an array.
        val latLngArray = singleReviewViewModel.getGeo()

        // Assert that array holds null values (as Strings).
        assertThat(latLngArray.first(), `is`("null"))
        assertThat(latLngArray.last(), `is`("null"))
    }

    @Test
    fun getGeo_noYourReviewInLiveData_returnArrayOfNullValues() {
        // Given no values set to yourReview LiveData.

        // Call the method to return an array.
        val latLngArray = singleReviewViewModel.getGeo()

        // Assert that array holds null values (as Strings).
        assertThat(latLngArray.first(), `is`("null"))
        assertThat(latLngArray.last(), `is`("null"))
    }
}