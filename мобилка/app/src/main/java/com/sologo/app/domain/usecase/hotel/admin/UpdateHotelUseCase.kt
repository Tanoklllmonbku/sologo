package com.sologo.app.domain.usecase.hotel.admin

import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.utils.Result

class UpdateHotelUseCase(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        hotelId: Int,
        name: String? = null,
        cityId: Int? = null,
        address: String? = null,
        pricePerNight: Int? = null,
        description: String? = null,
        rating: Double? = null,
        capacity: Int? = null,
        status: Int? = null,
        mainImage: String? = null,
        roomImages: String? = null
    ): Result<HotelDetail> {
        if (hotelId <= 0) {
            return Result.Error("Неверный ID отеля")
        }
        return hotelRepository.updateHotel(
            hotelId, name, cityId, address, pricePerNight,
            description, rating, capacity, status
        )
    }
}