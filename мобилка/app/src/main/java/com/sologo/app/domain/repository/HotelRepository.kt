package com.sologo.app.domain.repository

import com.sologo.app.domain.model.Hotel
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.utils.Result

interface HotelRepository {
    suspend fun getHotels(cityId: Int?, affordable: Boolean): Result<List<Hotel>>
    suspend fun getHotelById(hotelId: Int): Result<HotelDetail>
    suspend fun createHotel(
        name: String,
        cityId: Int,
        address: String,
        pricePerNight: Int,
        avgCityPrice: Int,
        description: String?,
        rating: Double,
        capacity: Int,
        managerPhones: List<String>?,
        mainImage: String?,
        roomImages: String?
    ): Result<HotelDetail>
    suspend fun updateHotel(
        hotelId: Int,
        name: String?,
        cityId: Int?,
        address: String?,
        pricePerNight: Int?,
        description: String?,
        rating: Double?,
        capacity: Int?,
        status: Int?
    ): Result<HotelDetail>
    suspend fun deleteHotel(hotelId: Int): Result<Unit>
    suspend fun restoreHotel(hotelId: Int): Result<Unit>
}