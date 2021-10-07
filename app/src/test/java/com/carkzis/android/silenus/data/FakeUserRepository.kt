package com.carkzis.android.silenus.data

import com.carkzis.android.silenus.LoadingState
import com.carkzis.android.silenus.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import javax.inject.Inject

class FakeUserRepository @Inject constructor(): UserRepository {

    // Decides if the addUser() method will fail or not.
    var failure = false
    // This will decide if a request is loading or not.
    var loading = false

    @Mock
    private lateinit var mockAuth: FirebaseAuth
    @Mock
    private var mockFirebaseUser: FirebaseUser? = null
    private var stubDisplayName: String? = ""

    /**
     * This will just emit the LoadingState, either as Loading, Success or Error according to
     * the boolean variables above.
     */
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

    /**
     * Stub to return a set username.
     */
    override fun getUsername(): String {
        return "Dave"
    }

    /**
     * Fake only method to set the FirebaseAuth return values (stubs) for testing.
     */
    fun setFirebaseAuthStubs(firebaseUserNotNull: Boolean, displayName: String?) {
        if (firebaseUserNotNull) mockFirebaseUser = mock(FirebaseUser::class.java)
        stubDisplayName = displayName
    }

    override fun getUser(): FirebaseAuth {
        mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        mockFirebaseUser?.let {
            `when`(mockAuth.currentUser!!.displayName).thenReturn(stubDisplayName)
        }
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