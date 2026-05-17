package com.sologo.app.domain.usecase.booking.admin

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.utils.Result

class AdminUpdateBookingStatusUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(trackingNumber: String, status: String): Result<Booking> {
        if (trackingNumber.isBlank()) {
            return Result.Error("Номер бронирования не может быть пустым")
        }
        val validStatuses = listOf("pending", "confirmed", "cancelled", "completed")
        if (status !in validStatuses) {
            return Result.Error("Неверный статус. Доступны: ${validStatuses.joinToString()}")
        }
        return bookingRepository.adminUpdateBookingStatus(trackingNumber, status)
    }
}