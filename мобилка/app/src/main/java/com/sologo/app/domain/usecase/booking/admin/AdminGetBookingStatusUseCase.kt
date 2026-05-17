package com.sologo.app.domain.usecase.booking.admin

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.utils.Result

class AdminGetBookingStatusUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(trackingNumber: String): Result<Booking> {
        if (trackingNumber.isBlank()) {
            return Result.Error("Номер бронирования не может быть пустым")
        }
        return bookingRepository.adminGetBooking(trackingNumber)
    }
}