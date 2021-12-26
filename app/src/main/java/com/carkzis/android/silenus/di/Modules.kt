package com.carkzis.android.silenus.di

import com.carkzis.android.silenus.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * This is for providing the Repository and Firebase objects for Hilt dependency injections.
 */
@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Singleton
    @Provides
    fun providesFirestore() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideUserRepository(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) : UserRepository {
        return UserRepositoryImpl(firestore, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideMainRepository(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) : MainRepository {
        return MainRepositoryImpl(firestore, firebaseAuth)
    }

}