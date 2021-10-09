package com.carkzis.android.silenus.review

import android.location.Address
import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.FakeUserRepository
import com.carkzis.android.silenus.data.SharedViewModel
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

@ExperimentalCoroutinesApi
class AddReviewViewModelTest() {

    private lateinit var addReviewViewModel: AddReviewViewModel
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
        mainRepository = FakeMainRepository()
        addReviewViewModel = AddReviewViewModel(mainRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setUpLocationInfo_success_postGeoPointAndLocationValuesToLiveData() {
        // Given mocked GeoPoint and Geocoder, that will return a specific location.
        val location = "Marc's Amazing Street"
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        `when`(geoPoint.latitude).thenReturn(1.0)
        `when`(geoPoint.longitude).thenReturn(1.0)
        val geoCoder = Mockito.mock(Geocoder::class.java)

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
    fun setUpLocationInfo_geoCoderThrowsError_postDefaultLocationToLiveData() {
        // TODO: Mock needs to throw an exception to execute the catch block.
    }


}