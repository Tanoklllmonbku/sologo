package com.sologo.app.domain.usecase.hotel

import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.utils.Result

class GetHotelByIdUseCase(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(hotelId: Int): Result<HotelDetail> {
        if (hotelId <= 0) {
            return Result.Error("Неверный ID отеля")
        }
        return hotelRepository.getHotelById(hotelId)
    }
}