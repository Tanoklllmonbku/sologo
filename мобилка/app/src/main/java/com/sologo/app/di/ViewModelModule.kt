package com.sologo.app.di

import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.presentation.viewmodel.*
import com.sologo.app.presentation.admin.viewmodel.AdminCityViewModel
import com.sologo.app.presentation.admin.viewmodel.AdminRouteViewModel
import com.sologo.app.presentation.admin.viewmodel.AdminSafeZoneViewModel
import com.sologo.app.presentation.admin.viewmodel.AdminUserViewModel
import com.sologo.app.presentation.viewmodel.admin.AdminBookingViewModel
import com.sologo.app.presentation.viewmodel.admin.AdminHotelsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // User ViewModels (существующие)
    viewModel { AuthViewModel(get(), get(), get(), get<AuthRepository>()) }
    viewModel { UserViewModel(get(), get(), get(), get<AuthRepository>()) }
    viewModel { HotelViewModel(get(), get()) }
    viewModel { BookingViewModel(get(), get(), get(), get()) }
    viewModel { CityViewModel(get(), get()) }
    viewModel { CityDetailViewModel(get(), get(), get(), get()) }
    viewModel { RouteViewModel(get(), get()) }
    viewModel { LostViewModel(get(), get()) }
    viewModel { SafeZoneViewModel(get()) }
    viewModel { WeatherViewModel(get()) }
    viewModel { AdminViewModel(get(), get(), get(), get(), get(), get(), get()) }

    /// ========== ADMIN VIEW MODELS ==========
    viewModel { AdminCityViewModel(get(), get(), get(), get()) }
    viewModel { AdminHotelsViewModel(get(), get(), get(), get(), get(), get()) }  // ← ДОБАВИТЬ
    viewModel { AdminBookingViewModel(get(), get()) }
    viewModel { AdminRouteViewModel(get(), get(), get(), get(), get()) }
    viewModel { AdminSafeZoneViewModel(get(), get(), get(), get(), get()) }
    viewModel { AdminUserViewModel(get(), get()) }
}