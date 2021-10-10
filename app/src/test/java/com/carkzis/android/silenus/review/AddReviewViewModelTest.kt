package com.carkzis.android.silenus.review

import android.location.Address
import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.FakeUserRepository
import com.carkzis.android.silenus.getOrAwaitValue
import com.google.firebase.firestore.GeoPoint
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
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class AddReviewViewModelTest() {

    private lateinit var addReviewViewModel: AddReviewViewModel
    private lateinit var userRepository: FakeUserRepository
    private lateinit var mainRepository: FakeMainRepository

    private lateinit var location: String
    private lateinit var geoPoint: GeoPoint
    private lateinit var geoCoder: Geocoder

    // This allows access to Dispatcher.Main in testing.
    private val dispatcher = TestCoroutineDispatcher()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        userRepository = FakeUserRepository()
        mainRepository = FakeMainRepository()
        addReviewViewModel = AddReviewViewModel(mainRepository, userRepository)

        // Set up location variables.
        location = "Marc's Amazing Street"
        geoPoint = Mockito.mock(GeoPoint::class.java)
        `when`(geoPoint.latitude).thenReturn(1.0)
        `when`(geoPoint.longitude).thenReturn(1.0)
        geoCoder = Mockito.mock(Geocoder::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setUpLocationInfo_success_postGeoPointAndLocationValuesToLiveData() {
        /*
         The given mocked GeoPoint and Geocoder, that will return a specific location, are
         set up in the setUp() method.
         */

        /*
         Mock an address object, to be returned in a list from geoCoder when getFromLocation()
         is called.
         */
        val addressObject = Mockito.mock(Address::class.java)
        // We need to set the stub for the returned list to prevent an out of bounds error.
        `when`(geoCoder.getFromLocation(1.0, 1.0, 1))
            .thenReturn(listOf(addressObject))
        // When getAddressLine(0) is called, it will return the location we set instead.
        `when`(geoCoder.getFromLocation(1.0,1.0,1)[0]
            .getAddressLine(0))
            .thenReturn(location)

        // Call method.
        addReviewViewModel.setUpLocationInfo(geoPoint, geoCoder)

        // Assert that we get the correct values posted to the LiveData.
        assertThat(addReviewViewModel.geopoint.getOrAwaitValue(), `is`(geoPoint))
        assertThat(addReviewViewModel.location.getOrAwaitValue(), `is`(location))
    }

    @Test
    fun setUpLocationInfo_geoCoderThrowsException_postDefaultLocationToLiveData() {
        /*
         The given mocked GeoPoint and Geocoder, that will return a specific location, are
         set up in the setUp() method.
         */

        // Throw NullPointerException, which occurs when there are no matches.
        `when`(geoCoder.getFromLocation(1.0, 1.0, 1)).thenThrow(
            NullPointerException()
        )

        // Call method.
        addReviewViewModel.setUpLocationInfo(geoPoint, geoCoder)

        // Assert that we get the correct values posted to the LiveData.
        assertThat(addReviewViewModel.location.getOrAwaitValue(), `is`("Nowhere Land"))
    }

    @Test
    fun setUpBarName_provideFullInput_postInputToLiveData() {
        // Given a bar name as a String.
        val barName = "Sam is Barred"

        // Call method.
        addReviewViewModel.setUpBarName(barName)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(addReviewViewModel.barName.getOrAwaitValue(), `is`(barName))
    }

    @Test
    fun setUpBarName_provideBlankInput_postInputToLiveData() {
        // Given a bar name as a String.
        val barName = ""

        // Call method.
        addReviewViewModel.setUpBarName(barName)

        // Assert that we get the blank input is still posted to the LiveData.
        assertThat(addReviewViewModel.barName.getOrAwaitValue(), `is`(barName))
    }

    @Test
    fun setUpRating_provideZeroRating_postInputToLiveData() {
        // Given rating as a float, with a value of 0.0f.
        val rating = 0.0f

        // Call method.
        addReviewViewModel.setUpRating(rating)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(addReviewViewModel.rating.getOrAwaitValue(), `is`(rating))
    }

    @Test
    fun setUpRating_provideFiveStarRating_postInputToLiveData() {
        // Given rating as a float, with a value of 5.0f.
        val rating = 5.0f

        // Call method.
        addReviewViewModel.setUpRating(rating)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(addReviewViewModel.rating.getOrAwaitValue(), `is`(rating))
    }

    @Test
    fun setUpRating_provideMiddleStarRating_postInputToLiveData() {
        // Given rating as a float, with a value of 2.7f.
        val rating = 2.7f

        // Call method.
        addReviewViewModel.setUpRating(rating)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(addReviewViewModel.rating.getOrAwaitValue(), `is`(rating))
    }

    @Test(expected = IllegalArgumentException::class)
    fun setUpRating_provideExceedingFiveStarRating_throwException() {
        // Give rating as a float, with a value of 6.0f.
        val rating = 5.1f

        // Call Method
        addReviewViewModel.setUpRating(rating)

        // As the rating should be between 0.0f and 5.0f inclusive, an Exception should be thrown.
    }

    @Test(expected = IllegalArgumentException::class)
    fun setUpRating_provideNegativeStarRating_throwException() {
        // Give rating as a float, with a value of 6.0f.
        val rating = -0.1f

        // Call Method
        addReviewViewModel.setUpRating(rating)

        // As the rating should be between 0.0f and 5.0f inclusive, an Exception should be thrown.
    }

    @Test
    fun setUpDescription_provideFullInput_postInputToLiveData() {
        // Given a description as a String.
        val description = "Great food even if it was only water."

        // Call method.
        addReviewViewModel.setUpDescription(description)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(addReviewViewModel.description.getOrAwaitValue(), `is`(description))
    }

    @Test
    fun setUpDescription_provideBlankInput_postInputToLiveData() {
        // Given a description as a String.
        val description = ""

        // Call method.
        addReviewViewModel.setUpDescription(description)

        // Assert that we get the blank input is still posted to the LiveData.
        assertThat(addReviewViewModel.description.getOrAwaitValue(), `is`(description))
    }


}