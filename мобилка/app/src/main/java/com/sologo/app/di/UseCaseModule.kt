// di/UseCaseModule.kt
package com.sologo.app.di

import com.sologo.app.domain.usecase.user.admin.GetAllUsersUseCase
import com.sologo.app.domain.usecase.user.admin.AdminUpdateUserUseCase
import com.sologo.app.domain.usecase.auth.IsLoggedInUseCase
import com.sologo.app.domain.usecase.auth.LoginUseCase
import com.sologo.app.domain.usecase.auth.LogoutUseCase
import com.sologo.app.domain.usecase.auth.RegisterUseCase
import com.sologo.app.domain.usecase.booking.CancelBookingUseCase
import com.sologo.app.domain.usecase.booking.CreateBookingUseCase
import com.sologo.app.domain.usecase.booking.GetMyBookingsUseCase
import com.sologo.app.domain.usecase.booking.admin.AdminUpdateBookingStatusUseCase
import com.sologo.app.domain.usecase.booking.admin.GetAllBookingsUseCase
import com.sologo.app.domain.usecase.city.GetCitiesUseCase
import com.sologo.app.domain.usecase.city.GetCityByIdUseCase
import com.sologo.app.domain.usecase.city.admin.CreateCityUseCase
import com.sologo.app.domain.usecase.city.admin.DeleteCityUseCase
import com.sologo.app.domain.usecase.city.admin.UpdateCityUseCase
import com.sologo.app.domain.usecase.hotel.GetHotelByIdUseCase
import com.sologo.app.domain.usecase.hotel.GetHotelsUseCase
import com.sologo.app.domain.usecase.hotel.admin.CreateHotelUseCase
import com.sologo.app.domain.usecase.hotel.admin.DeleteHotelUseCase
import com.sologo.app.domain.usecase.hotel.admin.RestoreHotelUseCase
import com.sologo.app.domain.usecase.hotel.admin.UpdateHotelUseCase
import com.sologo.app.domain.usecase.lost.GetMyLostReportsUseCase
import com.sologo.app.domain.usecase.lost.ReportLostUseCase
import com.sologo.app.domain.usecase.lost.admin.GetAllLostReportsUseCase
import com.sologo.app.domain.usecase.lost.admin.UpdateLostReportStatusUseCase
import com.sologo.app.domain.usecase.route.GetRouteByIdUseCase
import com.sologo.app.domain.usecase.route.GetRoutesUseCase
import com.sologo.app.domain.usecase.route.admin.CreateRouteUseCase
import com.sologo.app.domain.usecase.route.admin.DeleteRouteUseCase
import com.sologo.app.domain.usecase.route.admin.UpdateRouteUseCase
import com.sologo.app.domain.usecase.safezone.GetSafeZonesUseCase
import com.sologo.app.domain.usecase.safezone.admin.CreateSafeZoneUseCase
import com.sologo.app.domain.usecase.safezone.admin.DeleteSafeZoneUseCase
import com.sologo.app.domain.usecase.safezone.admin.UpdateSafeZoneUseCase
import com.sologo.app.domain.usecase.user.GetProfileUseCase
import com.sologo.app.domain.usecase.user.UpdatePasswordUseCase
import com.sologo.app.domain.usecase.user.UpdateProfileUseCase
import com.sologo.app.domain.usecase.weather.GetWeatherUseCase
import org.koin.dsl.module

val useCaseModule = module {

    // ========== AUTH ==========
    factory { LoginUseCase(get(), get()) }  // ← два параметра
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }

    // ========== USER ==========
    factory { GetProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }

    // ========== ADMIN ==========
    factory { GetAllUsersUseCase(get()) }
    factory { AdminUpdateUserUseCase(get()) }

    // ========== HOTEL (User) ==========
    factory { GetHotelsUseCase(get()) }
    factory { GetHotelByIdUseCase(get()) }

    // ========== HOTEL (Admin) ==========
    factory { CreateHotelUseCase(get()) }
    factory { UpdateHotelUseCase(get()) }
    factory { DeleteHotelUseCase(get()) }
    factory { RestoreHotelUseCase(get()) }

    // ========== BOOKING (User) ==========
    factory { CreateBookingUseCase(get()) }
    factory { GetMyBookingsUseCase(get()) }
    factory { CancelBookingUseCase(get()) }

    // ========== BOOKING (Admin) ==========
    factory { GetAllBookingsUseCase(get()) }
    factory { AdminUpdateBookingStatusUseCase(get()) }

    // ========== CITY (User) ==========
    factory { GetCitiesUseCase(get()) }
    factory { GetCityByIdUseCase(get()) }

    // ========== CITY (Admin) ==========
    factory { CreateCityUseCase(get()) }
    factory { UpdateCityUseCase(get()) }
    factory { DeleteCityUseCase(get()) }

    // ========== ROUTE (User) ==========
    factory { GetRoutesUseCase(get()) }
    factory { GetRouteByIdUseCase(get()) }

    // ========== ROUTE (Admin) ==========
    factory { CreateRouteUseCase(get()) }
    factory { UpdateRouteUseCase(get()) }
    factory { DeleteRouteUseCase(get()) }

    // ========== LOST (User) ==========
    factory { ReportLostUseCase(get()) }
    factory { GetMyLostReportsUseCase(get()) }

    // ========== LOST (Admin) ==========
    factory { GetAllLostReportsUseCase(get()) }
    factory { UpdateLostReportStatusUseCase(get()) }

    // ========== SAFE ZONE (User) ==========
    factory { GetSafeZonesUseCase(get()) }

    // ========== SAFE ZONE (Admin) ==========
    factory { CreateSafeZoneUseCase(get()) }
    factory { UpdateSafeZoneUseCase(get()) }
    factory { DeleteSafeZoneUseCase(get()) }

    // ========== WEATHER ==========
    factory { GetWeatherUseCase(get()) }
}