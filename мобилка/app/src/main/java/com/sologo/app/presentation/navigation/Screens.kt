// presentation/navigation/Screens.kt
package com.sologo.app.presentation.navigation

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

    // Routes
    object RouteList : Screen("routes")

    // Lost Report
    object LostReport : Screen("lost_report")

    // Safe Zones
    object SafeZones : Screen("safe_zones")
}