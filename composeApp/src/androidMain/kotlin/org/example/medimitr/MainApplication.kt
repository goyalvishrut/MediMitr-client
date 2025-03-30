package org.example.medimitr

import android.app.Application
import org.example.medimitr.di.dataModule
import org.example.medimitr.di.networkModule
import org.example.medimitr.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            // Use androidLogger in debug builds, Level.INFO or Level.ERROR in production
            androidLogger(Level.DEBUG)
            // Load shared KMM modules
            modules(listOf(networkModule, dataModule, viewModelModule))
            // Add any Android specific modules here if needed:
            // modules(androidSpecificModule)
        }
    }
}
