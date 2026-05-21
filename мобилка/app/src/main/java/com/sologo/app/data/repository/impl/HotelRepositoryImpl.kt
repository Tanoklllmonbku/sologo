// data/repository/impl/HotelRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.HotelMapper
import com.sologo.app.domain.model.Hotel
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.models.request.hotel.HotelCreateRequest
import com.sologo.app.models.request.hotel.HotelUpdateRequest
import com.sologo.app.network.api.BookingApi
import com.sologo.app.network.api.HotelApi
import com.sologo.app.utils.Result
import java.io.IOException

class HotelRepositoryImpl(
    private val hotelApi: HotelApi
) : HotelRepository {


    override suspend fun getHotels(cityId: Int?, affordable: Boolean): Result<List<Hotel>> {
        return try {
            val response = hotelApi.getHotels(cityId, affordable)
            Result.Success(response.map { HotelMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки отелей: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getHotelById(hotelId: Int): Result<HotelDetail> {
        return try {
            val response = hotelApi.getHotelById(hotelId)
            Result.Success(HotelMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки отеля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun createHotel(
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
        roomImages: List<String>?
    ): Result<HotelDetail> {
        return try {
            val request = HotelCreateRequest(
                name, cityId, address, description, pricePerNight,
                avgCityPrice, rating, capacity, managerPhones, mainImage, roomImages
            )
            val response = hotelApi.createHotel(request)
            Result.Success(HotelMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка создания отеля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updateHotel(
        hotelId: Int,
        name: String?,
        cityId: Int?,
        address: String?,
        pricePerNight: Int?,
        description: String?,
        rating: Double?,
        capacity: Int?,
        status: Int?
    ): Result<HotelDetail> {
        return try {
            val request = HotelUpdateRequest(
                name, cityId, address, description, pricePerNight,
                null, rating, capacity, null, null, null, status
            )
            val response = hotelApi.updateHotel(hotelId, request)
            Result.Success(HotelMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления отеля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun deleteHotel(hotelId: Int): Result<Unit> {
        return try {
            hotelApi.deleteHotel(hotelId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка удаления отеля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun restoreHotel(hotelId: Int): Result<Unit> {
        return try {
            hotelApi.restoreHotel(hotelId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка восстановления отеля: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}