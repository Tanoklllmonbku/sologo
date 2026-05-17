package com.sologo.app.domain.usecase.hotel.admin

import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.utils.Result

class RestoreHotelUseCase(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(hotelId: Int): Result<Unit> {
        if (hotelId <= 0) {
            return Result.Error("Неверный ID отеля")
        }
        return hotelRepository.restoreHotel(hotelId)
    }
}