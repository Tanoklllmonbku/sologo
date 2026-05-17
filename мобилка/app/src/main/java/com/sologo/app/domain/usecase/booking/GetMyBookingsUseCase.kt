package com.sologo.app.domain.usecase.booking

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.utils.Result

class GetMyBookingsUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(): Result<List<Booking>> {
        return bookingRepository.getMyBookings()
    }
}