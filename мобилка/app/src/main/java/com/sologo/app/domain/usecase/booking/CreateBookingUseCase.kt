package com.sologo.app.domain.usecase.booking

import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.utils.Result
import com.sologo.app.utils.validators.DateValidator

class CreateBookingUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(
        hotelId: Int,
        guestsCount: Int,
        checkIn: String,
        checkOut: String
    ): Result<Booking> {
        if (hotelId <= 0) {
            return Result.Error("Неверный ID отеля")
        }
        if (guestsCount < 1) {
            return Result.Error("Минимум 1 гость")
        }
        if (guestsCount > 10) {
            return Result.Error("Максимум 10 гостей")
        }
        if (!DateValidator.isValid(checkIn)) {
            return Result.Error("Неверный формат даты заезда")
        }
        if (!DateValidator.isValid(checkOut)) {
            return Result.Error("Неверный формат даты выезда")
        }
        // Используем исправленный метод - разрешает сегодня
        if (!DateValidator.isFutureOrToday(checkIn)) {
            return Result.Error("Дата заезда не может быть в прошлом")
        }
        if (!DateValidator.isAfter(checkOut, checkIn)) {
            return Result.Error("Дата выезда должна быть позже даты заезда")
        }
        return bookingRepository.createBooking(hotelId, guestsCount, checkIn, checkOut)
    }
}