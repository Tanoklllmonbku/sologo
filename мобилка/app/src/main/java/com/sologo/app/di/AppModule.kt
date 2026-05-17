// di/AppModule.kt
package com.sologo.app.di

import org.koin.dsl.module

val appModule = module {
    includes(
        networkModule,
        repositoryModule,
        useCaseModule,
        viewModelModule  // ← Добавить эту строку
    )
}