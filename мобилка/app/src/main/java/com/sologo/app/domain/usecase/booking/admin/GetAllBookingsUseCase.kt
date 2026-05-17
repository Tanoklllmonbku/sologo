package com.sologo.app.domain.usecase.booking.admin

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.utils.Result

class GetAllBookingsUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(): Result<List<Booking>> {
        return bookingRepository.getAllBookings()
    }
}