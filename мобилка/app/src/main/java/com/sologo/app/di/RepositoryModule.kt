// di/RepositoryModule.kt
package com.sologo.app.di

import com.sologo.app.data.repository.impl.*
import com.sologo.app.domain.repository.*
import org.koin.dsl.module

val repositoryModule = module {

    // Auth
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // User
    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    // Hotel
    single<HotelRepository> { HotelRepositoryImpl(get()) }

    // Booking
    single<BookingRepository> { BookingRepositoryImpl(get()) }

    // City
    single<CityRepository> { CityRepositoryImpl(get()) }

    // Route
    single<RouteRepository> { RouteRepositoryImpl(get()) }

    // Lost
    single<LostRepository> { LostRepositoryImpl(get()) }

    // SafeZone
    single<SafeZoneRepository> { SafeZoneRepositoryImpl(get()) }

    // Weather
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
}