package com.carkzis.android.silenus

import com.carkzis.android.silenus.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalUserDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteUserDataSource

    @Singleton
    @LocalUserDataSource
    @Provides
    fun provideUserLocalDataSource() : UserDataSource {
        return UserLocalDataSource
    }

    @Singleton
    @RemoteUserDataSource
    @Provides
    fun provideUserRemoteDataSource() : UserDataSource {
        return UserRemoteDataSource
    }

    @Singleton
    @Provides
    fun provideRepository(
        @LocalUserDataSource localUserDataSource: UserDataSource,
        @RemoteUserDataSource remoteUserDataSource: UserDataSource
    ) : UserRepository {
        return DefaultUserRepository(
            localUserDataSource, remoteUserDataSource
        )
    }

}