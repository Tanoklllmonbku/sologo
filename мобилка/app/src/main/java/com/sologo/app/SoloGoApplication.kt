package com.sologo.app

import android.app.Application
import com.sologo.app.di.appModule
import com.sologo.app.network.TokenManager
import com.sologo.app.utils.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

val themeModule = module {
    single { ThemeManager(androidContext()) }
}

class SoloGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@SoloGoApplication)
            modules(appModule, themeModule)  // ← добавили themeModule
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenManager: TokenManager = getKoin().get()
                tokenManager.loadToken()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}