package com.sologo.app.domain.repository

import com.sologo.app.domain.model.Booking
import com.sologo.app.utils.Result

interface BookingRepository {
    suspend fun createBooking(
        hotelId: Int,
        guestsCount: Int,
        checkIn: String,
        checkOut: String
    ): Result<Booking>
    suspend fun getMyBookings(): Result<List<Booking>>
    suspend fun cancelBooking(trackingNumber: String): Result<Booking>
    suspend fun getAllBookings(): Result<List<Booking>>
    suspend fun adminGetBooking(trackingNumber: String): Result<Booking>
    suspend fun adminUpdateBookingStatus(
        trackingNumber: String,
        status: String
    ): Result<Booking>
}