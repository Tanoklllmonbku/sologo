package com.sologo.app.ui.navigation

object NavRoutes {
    const val HOME = "home"
    const val BOOKINGS = "bookings"
    const val ROUTES = "routes"
    const val HOTELS = "hotels"
    const val HOTEL_DETAIL = "hotel/{hotelId}"
    const val FAQ = "faq"
    const val TRANSLATOR = "translator"
    const val SAFE_ZONES = "safe_zones"
    const val CHAT = "chat"

    fun hotelDetail(hotelId: String) = "hotel/$hotelId"
}
