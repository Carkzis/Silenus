package com.carkzis.android.silenus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SilenusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Set up Timber for logging.
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

}