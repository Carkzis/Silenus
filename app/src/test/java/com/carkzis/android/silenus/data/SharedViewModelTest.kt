package com.carkzis.android.silenus.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
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
import org.mockito.Mockito

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
    fun setGeopoint_postGeopointToLiveData() {
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

    @Test
    fun setBarDetails_provideNonNullInputs_postInputsToLiveData() {
        // Set up inputs.
        val name = "Fancy Noodle Bar"
        val rating = 5.0f
        val description = "It is quite fancy, to be fair."

        // Call method.
        sharedViewModel.setBarDetails(name, rating, description)

        // Ensure that the inputs are posted to the respective LiveData.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
    }

    @Test
    fun setBarDetails_provideNullInputs_postInputsToLiveData() {
        // Set up inputs.
        val name: String? = null
        val rating: Float? = null
        val description: String? = null

        // Call method.
        sharedViewModel.setBarDetails(name, rating, description)

        // Ensure that the inputs are posted to the respective LiveData.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
    }

    @Test
    fun setBarDetails_provideEmptyInputs_postInputsToLiveData() {
        // Set up inputs.
        val name = ""
        val rating = 0.0f
        val description = ""

        // Call method.
        sharedViewModel.setBarDetails(name, rating, description)

        // Ensure that the inputs are posted to the respective LiveData.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
    }

    @Test
    fun setSingleReview_postSingleReviewToLiveData() {
        // Set up the input.
        val singleReview = YourReview("123", "Testaurant", 5.0f,
            "Mantester", "This is for a test.", Timestamp.now(),
            Mockito.mock(GeoPoint::class.java))

        // Call the method.
        sharedViewModel.setSingleReview(singleReview)

        // Ensure the review is posted to the LiveData.
        assertThat(sharedViewModel.singleReview.getOrAwaitValue(),
            `is`(singleReview))
    }

    @Test
    fun setBarDetailsFromModel_provideNonNullInputs_postInputsToLiveData() {
        // Set up the inputs.
        val name = "Testaurant"
        val rating = 5.0f
        val description = "This is for a test."
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        val singleReview = YourReview("123", name, rating,
            "Mantester", description, Timestamp.now(),
            geoPoint)

        // Set the singleReview LiveData
        sharedViewModel.setSingleReview(singleReview)

        // Call the method.
        sharedViewModel.setBarDetailsFromModel()

        // Assert that the bar details are set to the LiveData, using data from singleReview.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue(),
            `is`(geoPoint))
    }

    @Test
    fun setBarDetailsFromModel_provideNullInputs_postInputsToLiveData() {
        // Set up the inputs.
        val name: String? = null
        val rating: Float? = null
        val description: String? = null
        val geoPoint: GeoPoint? = null
        val singleReview = YourReview("123", name, rating,
            "Mantester", description, Timestamp.now(),
            geoPoint)

        // Set the singleReview LiveData
        sharedViewModel.setSingleReview(singleReview)

        // Call the method.
        sharedViewModel.setBarDetailsFromModel()

        // Assert that the bar details are set to the LiveData, using data from singleReview.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue(),
            `is`(geoPoint))
    }

    @Test
    fun setBarDetailsFromModel_provideEmptyInputs_postInputsToLiveData() {
        // Set up the input.
        val name = ""
        val rating = 0.0f
        val description = ""
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        val singleReview = YourReview("123", name, rating,
            "Mantester", description, Timestamp.now(),
            geoPoint)

        // Set the singleReview LiveData
        sharedViewModel.setSingleReview(singleReview)

        // Call the method.
        sharedViewModel.setBarDetailsFromModel()

        // Assert that the bar details are set to the LiveData, using data from singleReview.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue(),
            `is`(geoPoint))
    }

    @Test
    fun resetReviewScreen_resetsReviewInputLiveDataToNull() {
        // Set up the review input LiveData, so we know if it is cleared.
        val name = "Testaurant"
        val rating = 5.0f
        val description = "This is for a test."
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        val singleReview = YourReview("123", name, rating,
            "Mantester", description, Timestamp.now(),
            geoPoint)
        sharedViewModel.setSingleReview(singleReview)
        sharedViewModel.setBarDetailsFromModel()

        // Test that the review inputs have been set.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(name))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue().toString(),
            `is`(rating.toString()))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(description))
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue(),
            `is`(geoPoint))

        // Call the method to test.
        sharedViewModel.resetReviewScreen()

        val nullString: String? = null
        val nullFloat: Float? = null
        val nullGeoPoint: GeoPoint? = null

        // Test that the review inputs are now null.
        assertThat(sharedViewModel.reviewBarName.getOrAwaitValue(),
            `is`(nullString))
        assertThat(sharedViewModel.reviewRating.getOrAwaitValue(),
            `is`(nullFloat))
        assertThat(sharedViewModel.reviewDescription.getOrAwaitValue(),
            `is`(nullString))
        assertThat(sharedViewModel.chosenGeopoint.getOrAwaitValue(),
            `is`(nullGeoPoint))
    }

    @Test
    fun setMapOpenReason_postMapReasonToLiveData() {
        // Set up MapReason..
        val mapReason = MapReason.EDITREV

        // Call method.
        sharedViewModel.setMapOpenReason(mapReason)

        // Ensure that the latLng object is posted to the LiveData.
        assertThat(sharedViewModel.mapReason.getOrAwaitValue(),
            `is`(MapReason.EDITREV))
    }

    @Test
    fun toastMe_postEventToLiveData() {
        // Set up string to be Toast(ed).
        val toastString = "Toasty."

        // Call method.
        sharedViewModel.toastMe(toastString)

        // Ensure that the Toast message is added to the LiveData (within an Event wrapper).
        assertThat(sharedViewModel.toastText.getOrAwaitValue().getContextIfNotHandled().toString(),
            `is`(toastString))
    }

}