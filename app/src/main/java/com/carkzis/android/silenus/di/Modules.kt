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
    fun provideUserRepository(
        @LocalUserDataSource localUserDataSource: UserDataSource,
        @RemoteUserDataSource remoteUserDataSource: UserDataSource
    ) : UserRepository {
        return DefaultUserRepository(
            localUserDataSource, remoteUserDataSource
        )
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalMainDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteMainDataSource

    @Singleton
    @LocalMainDataSource
    @Provides
    fun provideMainLocalDataSource() : MainDataSource {
        return MainLocalDataSource
    }

    @Singleton
    @RemoteMainDataSource
    @Provides
    fun provideMainRemoteDataSource() : MainDataSource {
        return MainRemoteDataSource
    }

    @Singleton
    @Provides
    fun provideMainRepository(
        @LocalMainDataSource localMainDataSource: MainDataSource,
        @RemoteMainDataSource remoteMainDataSource: MainDataSource
    ) : MainRepository {
        return DefaultMainRepository(
            localMainDataSource, remoteMainDataSource
        )
    }

}