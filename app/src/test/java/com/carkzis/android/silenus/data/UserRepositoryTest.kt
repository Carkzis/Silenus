package com.carkzis.android.silenus.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carkzis.android.silenus.utils.LoadingState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // We are using the FakeUserRepository for testing.
    private lateinit var userRepository: FakeUserRepository

    @Before
    fun createRepository() {
        userRepository = FakeUserRepository()
    }

    @Test
    fun addUser_error_receiveLoadingStateError() = runBlockingTest {
        // Set the failure of adding a user to the database to true in the fake repository.
        userRepository.failure = true
        // Test the response, if we get LoadingState.Error, isSuccessful will be false.
        var isSuccessful = true
        userRepository.addUser().collect {
            isSuccessful = when (it) {
                is LoadingState.Error -> false
                else -> true
            }
        }
        // Assert that the addition of a user was not successful.
        assertThat(isSuccessful, `is`(false))
    }

    @Test
    fun addUser_success_receiveLoadingStateSuccess() = runBlockingTest {
        // Set the failure of adding a user to false (the default).
        userRepository.failure = false
        // Test the response, if we get LoadingState.Success, isSuccessful will be true
        var isSuccessful = false
        userRepository.addUser().collect {
            isSuccessful = when (it) {
                is LoadingState.Success -> true
                else -> false
            }
        }
        assertThat(isSuccessful, `is`(true))
    }

    @Test
    fun addUser_loading_receiveLoadingStateLoading() = runBlockingTest {
        // Set the loading variable to true.
        userRepository.loading = true
        // Test the response, if we get LoadingState.Loading, isLoading will be true.
        var isLoading = false
        userRepository.addUser().collect {
            isLoading = when (it) {
                is LoadingState.Loading -> true
                else -> false
            }
        }
        assertThat(isLoading, `is`(true))
    }

    @Test
    fun getUsername_isDave() {
        // Get the username
        val username = userRepository.getUsername()
        // Assert that it is, in fact, "Dave".
        assertThat(username, `is`("Dave"))
    }

    @Test
    fun getUsername_isNotAndrew() {
        // Get the username
        val username = userRepository.getUsername()
        // Assert that it is, in fact, "Dave".
        assertThat(username, `is`(not("Andrew")))
    }

    /*
    Test we are actually getting the object. Should always pass.
     */
    @Test
    fun getUser_getFirebaseAuthObject() {
        // Get the user
        val user = userRepository.getUser()
        // Assert that this works, and we have the object.
        assertThat(user is FirebaseAuth, `is`(true))
    }

    @Test
    fun getUserDetails_returnCorrectUserObjectAndUid() {
        // Get the user object and uid.
        val user = userRepository.getUserDetails().first
        val uid = userRepository.getUserDetails().second
        // Assert that the name is "David", and the email address is "david@david.com".
        assertThat(user.name, `is`("David"))
        assertThat(user.email, `is`("david@david.com"))
        // Assert that the uid is "anId".
        assertThat(uid, `is`(uid))
    }

}