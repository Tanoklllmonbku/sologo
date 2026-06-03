package com.sologo.app.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")

    // Main
    object Home : Screen("home")
    object Profile : Screen("profile")

    // Hotels
    object HotelList : Screen("hotels")
    object HotelDetail : Screen("hotel/{hotelId}") {
        fun passId(hotelId: Int): String = "hotel/$hotelId"
    }

    // Bookings
    object MyBookings : Screen("my_bookings")
    object CreateBooking : Screen("create_booking/{hotelId}") {
        fun passId(hotelId: Int): String = "create_booking/$hotelId"
    }

    // Cities
    object CityList : Screen("cities")
    object CityDetail : Screen("city/{cityId}/{cityName}") {
        fun passId(cityId: Int, cityName: String): String = "city/$cityId/${Uri.encode(cityName)}"
    }

    // Routes
    object RouteList : Screen("routes")
    object RouteDetail : Screen("route/{routeId}") {
        fun passId(routeId: Int): String = "route/$routeId"
    }

    // Lost Report
    object LostReport : Screen("lost_report")

    // Safe Zones
    object SafeZones : Screen("safe_zones")

    object Weather : Screen("weather")
    object AdminPanel : Screen("admin_panel")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminCities : Screen("admin_cities")
    object AdminHotels : Screen("admin_hotels")
    object AdminBookings : Screen("admin_bookings")
    object AdminRoutes : Screen("admin_routes")
    object AdminSafeZones : Screen("admin_safe_zones")
    object AdminUsers : Screen("admin_users")
}