package com.carkzis.android.silenus.review

import android.location.Address
import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.FakeMainRepository
import com.carkzis.android.silenus.data.FakeUserRepository
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.getOrAwaitValue
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class EditReviewViewModelTest {

    private lateinit var editReviewViewModel: EditReviewViewModel
    private lateinit var userRepository: FakeUserRepository
    private lateinit var mainRepository: FakeMainRepository

    private var location: String? = null
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
        editReviewViewModel = EditReviewViewModel(mainRepository, userRepository)

        // Set up location variables.
        location = "Marc's Upgraded Street"
        geoPoint = Mockito.mock(GeoPoint::class.java)
        Mockito.`when`(geoPoint.latitude).thenReturn(1.0)
        Mockito.`when`(geoPoint.longitude).thenReturn(1.0)
        geoCoder = Mockito.mock(Geocoder::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * This sets up a successful geoPoint and location value posting to the LiveData, as this
     * is often reused.
     */
    fun setUpSuccessfulGeoPointAndLocationValueToLiveData(setupMockOnly: Boolean = false) {
        /*
         Mock an address object, to be returned in a list from geoCoder when getFromLocation()
         is called.
         */
        val addressObject = Mockito.mock(Address::class.java)
        // We need to set the stub for the returned list to prevent an out of bounds error.
        Mockito.`when`(geoCoder.getFromLocation(1.0, 1.0, 1))
            .thenReturn(listOf(addressObject))
        // When getAddressLine(0) is called, it will return the location we set instead.
        Mockito.`when`(
            geoCoder.getFromLocation(1.0, 1.0, 1)[0]
                .getAddressLine(0)
        )
            .thenReturn(location)

        // Call method if we are not just setting up the mock.
        if (!setupMockOnly) {
            editReviewViewModel.setUpLocationInfo(geoPoint, geoCoder)
        }
    }

    @Test
    fun setUpLocationInfo_success_postGeoPointAndLocationValuesToLiveData() {
        /*
         The given mocked GeoPoint and Geocoder, that will return a specific location, are
         set up in the setUp() method.
         */

        // Set up the successful posting of values to the LiveData.
        setUpSuccessfulGeoPointAndLocationValueToLiveData()

        // Assert that we get the correct values posted to the LiveData.
        assertThat(
            editReviewViewModel.geopoint.getOrAwaitValue(),
            CoreMatchers.`is`(geoPoint)
        )
        assertThat(
            editReviewViewModel.location.getOrAwaitValue(),
            CoreMatchers.`is`(location)
        )
    }

    @Test
    fun setUpLocationInfo_geoCoderThrowsException_postDefaultLocationToLiveData() {
        /*
         The given mocked GeoPoint and Geocoder, that will return a specific location, are
         set up in the setUp() method.
         */

        // Throw NullPointerException, which occurs when there are no matches.
        Mockito.`when`(geoCoder.getFromLocation(1.0, 1.0, 1)).thenThrow(
            NullPointerException()
        )

        // Call method.
        editReviewViewModel.setUpLocationInfo(geoPoint, geoCoder)

        // Assert that we get the correct values posted to the LiveData.
        assertThat(
            editReviewViewModel.location.getOrAwaitValue(),
            CoreMatchers.`is`("Nowhere Land")
        )
    }

    @Test
    fun setUpBarName_provideFullInput_postInputToLiveData() {
        // Given a bar name as a String.
        val barName = "Sam is Barred"

        // Call method.
        editReviewViewModel.setUpBarName(barName)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(
            editReviewViewModel.barName.getOrAwaitValue(),
            CoreMatchers.`is`(barName)
        )
    }

    @Test
    fun setUpBarName_provideBlankInput_postInputToLiveData() {
        // Given a bar name as a String.
        val barName = ""

        // Call method.
        editReviewViewModel.setUpBarName(barName)

        // Assert that we get the blank input is still posted to the LiveData.
        assertThat(
            editReviewViewModel.barName.getOrAwaitValue(),
            CoreMatchers.`is`(barName)
        )
    }

    @Test
    fun setUpRating_provideZeroRating_postInputToLiveData() {
        // Given rating as a float, with a value of 0.0f.
        val rating = 0.0f

        // Call method.
        editReviewViewModel.setUpRating(rating)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(
            editReviewViewModel.rating.getOrAwaitValue(),
            CoreMatchers.`is`(rating)
        )
    }

    @Test
    fun setUpRating_provideFiveStarRating_postInputToLiveData() {
        // Given rating as a float, with a value of 5.0f.
        val rating = 5.0f

        // Call method.
        editReviewViewModel.setUpRating(rating)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(
            editReviewViewModel.rating.getOrAwaitValue(),
            CoreMatchers.`is`(rating)
        )
    }

    @Test
    fun setUpRating_provideMiddleStarRating_postInputToLiveData() {
        // Given rating as a float, with a value of 2.7f.
        val rating = 2.7f

        // Call method.
        editReviewViewModel.setUpRating(rating)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(
            editReviewViewModel.rating.getOrAwaitValue(),
            CoreMatchers.`is`(rating)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun setUpRating_provideExceedingFiveStarRating_throwException() {
        // Give rating as a float, with a value of 6.0f.
        val rating = 5.1f

        // Call Method
        editReviewViewModel.setUpRating(rating)

        // As the rating should be between 0.0f and 5.0f inclusive, an Exception should be thrown.
    }

    @Test(expected = IllegalArgumentException::class)
    fun setUpRating_provideNegativeStarRating_throwException() {
        // Give rating as a float, with a value of 6.0f.
        val rating = -0.1f

        // Call Method
        editReviewViewModel.setUpRating(rating)

        // As the rating should be between 0.0f and 5.0f inclusive, an Exception should be thrown.
    }

    @Test
    fun setUpDescription_provideFullInput_postInputToLiveData() {
        // Given a description as a String.
        val description = "Great food even if it was only water."

        // Call method.
        editReviewViewModel.setUpDescription(description)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(
            editReviewViewModel.description.getOrAwaitValue(),
            CoreMatchers.`is`(description)
        )
    }

    @Test
    fun setUpDescription_provideBlankInput_postInputToLiveData() {
        // Given a description as a String.
        val description = ""

        // Call method.
        editReviewViewModel.setUpDescription(description)

        // Assert that we get the blank input is still posted to the LiveData.
        assertThat(
            editReviewViewModel.description.getOrAwaitValue(),
            CoreMatchers.`is`(description)
        )
    }

    @Test
    fun setUpReviewInfo_completeInputs_postInputsToLiveData() = runBlockingTest {
        // Given a review, and a geocoder.
        val documentId = "123"
        val establishment = "Marc's Bar"
        val rating = 2.5f
        val description = "More like Mock's Bar (ugh)."
        val timestamp = Timestamp.now()
        location = "Sheffield" // This is a global variable.
        val yourReview = YourReview(
            documentId, establishment, rating, location, description, timestamp, geoPoint
        )
        geoCoder // Just a reminder that we have one.

        // Set up the geocoder mock to be reader for setUpLocation() called by setUpReviewInfo().
        setUpSuccessfulGeoPointAndLocationValueToLiveData(true)

        // Call method.
        editReviewViewModel.setUpReviewInfo(yourReview, geoCoder)

        // Assert that values have been posted to LiveData.
        assertThat(editReviewViewModel.geopoint.getOrAwaitValue().latitude, `is`(1.0))
        assertThat(editReviewViewModel.geopoint.getOrAwaitValue().longitude, `is`(1.0))
        assertThat(editReviewViewModel.location.getOrAwaitValue(), `is`(location))
        assertThat(editReviewViewModel.barName.getOrAwaitValue(), `is`(establishment))
        assertThat(editReviewViewModel.description.getOrAwaitValue(), `is`(description))

    }

    @Test(expected = NullPointerException::class)
    fun setUpReviewInfo_nullGeopoint_throwException() = runBlockingTest {
        // Given a review, and a geocoder.
        val documentId = "123"
        val establishment = "Marc's Bar"
        val rating = 2.5f
        val description = "More like Mock's Bar (ugh)."
        val timestamp = Timestamp.now()
        location = "Sheffield" // This is a global variable.
        val yourReview = YourReview(
            documentId, establishment, rating, location, description, timestamp, null
        )
        geoCoder // Just a reminder that we have one.

        // Set up the geocoder mock to be reader for setUpLocation() called by setUpReviewInfo().
        setUpSuccessfulGeoPointAndLocationValueToLiveData(true)

        // Call method.
        editReviewViewModel.setUpReviewInfo(yourReview, geoCoder)
    }

    @Test
    fun setUpReviewInfo_nullLocation_defaultLocationPostedToLiveData() = runBlockingTest {
        // Given a review, and a geocoder.
        val documentId = "123"
        val establishment = "Marc's Bar"
        val rating = 2.5f
        val description = "More like Mock's Bar (ugh)."
        val timestamp = Timestamp.now()
        location = null // This is a global variable.
        val yourReview = YourReview(
            documentId, establishment, rating, location, description, timestamp, geoPoint
        )
        geoCoder // Just a reminder that we have one.

        // Set up the geocoder mock to be reader for setUpLocation() called by setUpReviewInfo().
        setUpSuccessfulGeoPointAndLocationValueToLiveData(true)

        // Call method.
        editReviewViewModel.setUpReviewInfo(yourReview, geoCoder)

        // The default "Nowhere Land" should be posted to the location LiveData.
        assertThat(editReviewViewModel.location.getOrAwaitValue(), `is`("Nowhere Land"))
    }

    @Test(expected = NullPointerException::class)
    fun setUpReviewInfo_nullBarName_throwException() = runBlockingTest {
        // Given a review, and a geocoder.
        val documentId = "123"
        val establishment: String? = null
        val rating = 2.5f
        val description = "More like Mock's Bar (ugh)."
        val timestamp = Timestamp.now()
        location = "Sheffield" // This is a global variable.
        val yourReview = YourReview(
            documentId, establishment, rating, location, description, timestamp, geoPoint
        )
        geoCoder // Just a reminder that we have one.

        // Set up the geocoder mock to be reader for setUpLocation() called by setUpReviewInfo().
        setUpSuccessfulGeoPointAndLocationValueToLiveData(true)

        // Call method.
        editReviewViewModel.setUpReviewInfo(yourReview, geoCoder)
    }

    @Test(expected = NullPointerException::class)
    fun setUpReviewInfo_nullValue_throwException() = runBlockingTest {
        // Given a review, and a geocoder.
        val documentId = "123"
        val establishment = "Marc's Bar"
        val rating: Float? = null
        val description = "More like Mock's Bar (ugh)."
        val timestamp = Timestamp.now()
        location = "Sheffield" // This is a global variable.
        val yourReview = YourReview(
            documentId, establishment, rating, location, description, timestamp, geoPoint
        )
        geoCoder // Just a reminder that we have one.

        // Set up the geocoder mock to be reader for setUpLocation() called by setUpReviewInfo().
        setUpSuccessfulGeoPointAndLocationValueToLiveData(true)

        // Call method.
        editReviewViewModel.setUpReviewInfo(yourReview, geoCoder)
    }

    @Test
    fun setUpReviewInfo_nullDescription_noExceptionThrown() = runBlockingTest {
        // Given a review, and a geocoder.
        val documentId = "123"
        val establishment = "Marc's Bar"
        val rating = 2.5f
        val description : String? = null
        val timestamp = Timestamp.now()
        location = "Sheffield" // This is a global variable.
        val yourReview = YourReview(
            documentId, establishment, rating, location, description, timestamp, geoPoint
        )
        geoCoder // Just a reminder that we have one.

        // Set up the geocoder mock to be reader for setUpLocation() called by setUpReviewInfo().
        setUpSuccessfulGeoPointAndLocationValueToLiveData(true)

        // Call method.
        editReviewViewModel.setUpReviewInfo(yourReview, geoCoder)
    }

    @Test
    fun setUpLocation_provideInput_postInputToLiveData() {
        // Note: This is a test of a test-only method. Remove in production.

        // Given a description as a String.
        val location = "Rockport" // Sorry...

        // Call method.
        editReviewViewModel.setUpDescription(location)

        // Assert that we get the correct value posted to the LiveData.
        assertThat(editReviewViewModel.description.getOrAwaitValue(), `is`(location))
    }

    @Test
    fun submissionPreChecks_barNameNull_showToastAndReturn() {
        // Given a barValue of null (not set), but values set to the location and geoPoint
        // (latter being set up in setUp()).
        setUpSuccessfulGeoPointAndLocationValueToLiveData()
        val review = YourReview() // Can use an empty review with null values.

        // Call method.
        editReviewViewModel.submissionPreChecks(review)

        // Assert that the toast is set to the correct value.
        assertThat(editReviewViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.no_establishment))
    }

    @Test
    fun submissionPreChecks_barLocationNull_showToastAndReturn() {
        // Given a location of null (not set), but value set for the name.
        val barName = "Town"
        val review = YourReview() // Can use an empty review with null values.

        // And subsequently LiveData for the two values.
        editReviewViewModel.setUpBarName(barName)

        // Call method.
        editReviewViewModel.submissionPreChecks(review)
        setUpSuccessfulGeoPointAndLocationValueToLiveData()

        // Assert that the toast is set to the correct value.
        assertThat(editReviewViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.no_location))
    }

    @Test
    fun submissionPreChecks_barGeoPointNull_showToastAndReturn() {
        // Given a barName and location but a geoPoint of null.
        val barName = "Town"
        val location = "Funchester"
        val review = YourReview() // Can use an empty review with null values.

        // And subsequently LiveData for the two values.
        editReviewViewModel.setUpBarName(barName)
        editReviewViewModel.setUpLocation(location)

        // Call method.
        editReviewViewModel.submissionPreChecks(review)

        // Assert that the toast is set to the correct value.
        assertThat(editReviewViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.error))
    }

    @Test
    fun progressToEditingReview_success_postEditedReviewToLiveDataAndShowToast() = runBlockingTest {
        // Given that all values required for editing a review are posted to the LiveData.
        val newBarName = "Town Bar Too"
        val newRating = 5.0f
        val newDescription = "It is in town, and a bar"

        // These set the LiveData.
        editReviewViewModel.setUpBarName(newBarName)
        editReviewViewModel.setUpRating(newRating)
        editReviewViewModel.setUpDescription(newDescription)
        setUpSuccessfulGeoPointAndLocationValueToLiveData()

        // Also given the "old" review.
        val oldBarName = "Old Bar Won"
        val oldRating = 2.5f
        val oldDescription = "It was supposed to be in town. And be a bar."
        val documentId = "123"
        val dateAdded = Timestamp.now()
        val oldReview = YourReview(
            documentId,
            oldBarName,
            oldRating,
            location,
            oldDescription,
            dateAdded,
            geoPoint
        )

        // Call the method.
        editReviewViewModel.submissionPreChecks(oldReview)

        /*
        Assert that the edit of a review was successful, and so the navToYourReviews
        LiveData has a YourReview object, with all the same information as oldReview except
        for the attributes we edited; establishment, rating and description.
         */
        val returnedReview = editReviewViewModel.navToSingleReview.getOrAwaitValue()
            .getContextIfNotHandled()
        assertThat(returnedReview?.documentId, `is`(documentId))
        assertThat(returnedReview?.establishment, `is`(newBarName))
        assertThat(returnedReview?.rating, `is`(newRating))
        assertThat(returnedReview?.location, `is`(location))
        assertThat(returnedReview?.description, `is`(newDescription))
        assertThat(returnedReview?.dateAdded, `is`(dateAdded))
        assertThat(returnedReview?.geo, `is`(geoPoint))

        // Should also be a message collected by Flow, to be added to toastText via helper method.
        assertThat(editReviewViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.review_edited))
    }

    @Test
    fun progressToEditingReview_failure_postFailureMessageToToastLiveData() = runBlockingTest {
        // Given that all values required for editing a review are posted to the LiveData.
        val newBarName = "Town Bar Too"
        val newRating = 5.0f
        val newDescription = "It is in town, and a bar"

        // These set the LiveData.
        editReviewViewModel.setUpBarName(newBarName)
        editReviewViewModel.setUpRating(newRating)
        editReviewViewModel.setUpDescription(newDescription)
        setUpSuccessfulGeoPointAndLocationValueToLiveData()

        // Mock a review (default attributes are null).
        val oldReview = YourReview()

        // Set the repository to fail the request to edit a review to the database.
        mainRepository.failure = true

        // Call the method.
        editReviewViewModel.submissionPreChecks(oldReview)

        // Should also be a message collected by Flow, to be added to toastText via helper method.
        assertThat(editReviewViewModel.toastText.getOrAwaitValue().getContextIfNotHandled(),
            `is`(R.string.error))
    }

    /*
    TODO: We need to add tests for the responses to LoadingState.Loading emissions,
        which have not yet been fully implemented, currently only logging a message to Logcat.
     */

}