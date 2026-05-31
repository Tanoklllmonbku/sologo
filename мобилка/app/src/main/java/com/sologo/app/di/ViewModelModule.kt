package com.sologo.app.di

import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.presentation.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Auth
    viewModel { AuthViewModel(get(), get(), get(), get<AuthRepository>()) }

    // User
    viewModel { UserViewModel(get(), get(), get(), get<AuthRepository>()) }

    // Hotel
    viewModel { HotelViewModel(get(), get()) }

    // Booking
    viewModel { BookingViewModel(get(), get(), get(), get()) }

    // City
    viewModel { CityViewModel(get(), get()) }

    // CityDetail - ДОБАВИТЬ
    viewModel { CityDetailViewModel(get(), get(), get(), get()) }

    // Route
    viewModel { RouteViewModel(get(), get()) }

    // Lost
    viewModel { LostViewModel(get(), get()) }

    // SafeZone
    viewModel { SafeZoneViewModel(get()) }

    // Weather
    viewModel { WeatherViewModel(get()) }

    // Admin
    viewModel { AdminViewModel(get(), get(), get(), get(), get(), get(), get()) }
}