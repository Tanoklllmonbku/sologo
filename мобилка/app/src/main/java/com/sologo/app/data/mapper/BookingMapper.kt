// data/mapper/BookingMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.model.BookingStatus
import com.sologo.app.models.response.booking.BookingResponse

object BookingMapper {
    fun toDomain(response: BookingResponse): Booking {
        return Booking(
            id = response.bookingId,
            trackingNumber = response.trackingNumber,
            userId = response.userId,
            hotelId = response.hotelId,
            hotelName = response.hotelName,
            hotelCity = response.hotelCity,
            guestsCount = response.guestsCount,
            checkIn = response.checkIn,
            checkOut = response.checkOut,
            totalPrice = response.totalPrice,
            status = BookingStatus.fromString(response.status),
            days = response.days,
            createdAt = response.createdAt
        )
    }
}