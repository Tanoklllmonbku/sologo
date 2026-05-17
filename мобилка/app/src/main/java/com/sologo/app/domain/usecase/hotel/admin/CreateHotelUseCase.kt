package com.sologo.app.domain.usecase.hotel.admin

import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.utils.Result

class CreateHotelUseCase(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        name: String,
        cityId: Int,
        address: String,
        pricePerNight: Int,
        avgCityPrice: Int,
        description: String? = null,
        rating: Double = 0.0,
        capacity: Int = 10,
        managerPhones: List<String>? = null,
        mainImage: String? = null,
        roomImages: String? = null
    ): Result<HotelDetail> {
        if (name.isBlank()) {
            return Result.Error("Название отеля не может быть пустым")
        }
        if (cityId <= 0) {
            return Result.Error("Неверный ID города")
        }
        if (address.isBlank()) {
            return Result.Error("Адрес не может быть пустым")
        }
        if (pricePerNight <= 0) {
            return Result.Error("Цена должна быть больше 0")
        }
        if (capacity < 1) {
            return Result.Error("Вместимость должна быть больше 0")
        }
        return hotelRepository.createHotel(
            name, cityId, address, pricePerNight, avgCityPrice,
            description, rating, capacity, managerPhones, mainImage, roomImages
        )
    }
}