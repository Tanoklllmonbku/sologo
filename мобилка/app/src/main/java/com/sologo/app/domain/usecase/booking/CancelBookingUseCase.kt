package com.sologo.app.domain.usecase.booking

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.utils.Result

class CancelBookingUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(trackingNumber: String): Result<Booking> {
        if (trackingNumber.isBlank()) {
            return Result.Error("Номер бронирования не может быть пустым")
        }
        return bookingRepository.cancelBooking(trackingNumber)
    }
}