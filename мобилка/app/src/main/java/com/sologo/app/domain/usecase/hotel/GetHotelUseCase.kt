package com.sologo.app.domain.usecase.hotel

import com.sologo.app.domain.model.Hotel
import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.utils.Result

class GetHotelsUseCase(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        cityId: Int? = null,
        affordable: Boolean = false
    ): Result<List<Hotel>> {
        return hotelRepository.getHotels(cityId, affordable)
    }
}