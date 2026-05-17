// SoloGoApplication.kt
package com.sologo.app

import android.app.Application
import com.sologo.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SoloGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@SoloGoApplication)
            modules(appModule)
        }
    }
}