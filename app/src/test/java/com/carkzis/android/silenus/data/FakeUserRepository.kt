package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.mockito.Mock
import org.mockito.Mockito.mock
import javax.inject.Inject

class FakeUserRepository @Inject constructor(): UserRepository {

    // Decides if the addUser() method will fail or not.
    var failure = false
    // Want to check the loading works.
    var loading = false

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    override suspend fun addUser() = flow {
        emit(LoadingState.Loading(R.string.loading))
        // Skip if we are loading.
        if (!loading) {
            // Do this if we are succesful.
            if (!failure) {
                emit(LoadingState.Success<Int>(500))
            } else {
                throw Exception()
            }
        }
    }.catch {
        emit(LoadingState.Error(R.string.error, Exception()))
    }

    override fun getUsername(): String {
        return "Dave"
    }

    override fun getUser(): FirebaseAuth {
        mockAuth = mock(FirebaseAuth::class.java)
        return mockAuth
    }

    fun getUserDetails() : Pair<UserObject, String> {
        val newUser = UserObject(
            name = "David",
            email = "david@david.com",
            isAdmin = false,
            ratings = 0,
            joinDate = Timestamp.now()
        )
        return Pair(newUser, "anId")
    }

}