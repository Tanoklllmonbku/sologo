package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.BookingMapper
import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.BookingRepository
import com.sologo.app.models.request.booking.BookingRequest
import com.sologo.app.models.request.booking.BookingStatusUpdateRequest
import com.sologo.app.network.api.BookingApi
import com.sologo.app.utils.Result
import java.io.IOException
import retrofit2.HttpException

class BookingRepositoryImpl(
    private val bookingApi: BookingApi
) : BookingRepository {

    override suspend fun createBooking(
        hotelId: Int,
        guestsCount: Int,
        checkIn: String,
        checkOut: String
    ): Result<Booking> {
        return try {
            val request = BookingRequest(hotelId, guestsCount, checkIn, checkOut)
            val response = bookingApi.createBooking(request)
            Result.Success(BookingMapper.toDomain(response))
        } catch (e: HttpException) {
            // Логируем для отладки
            android.util.Log.e("BookingRepo", "HTTP Error: ${e.code()}")
            android.util.Log.e("BookingRepo", "Message: ${e.message()}")
            android.util.Log.e("BookingRepo", "Response: ${e.response()?.errorBody()?.string()}")

            val errorMessage = when (e.code()) {
                400 -> "Нет свободных мест в отеле на выбранные даты"
                401 -> "Необходимо авторизоваться"
                404 -> "Отель не найден"
                else -> "Ошибка создания бронирования. Попробуйте позже."
            }
            Result.Error(errorMessage)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getMyBookings(): Result<List<Booking>> {
        return try {
            val response = bookingApi.getMyBookings()
            Result.Success(response.map { BookingMapper.toDomain(it) })
        } catch (e: HttpException) {
            Result.Error("Ошибка загрузки бронирований: ${e.code()}")
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки бронирований: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun cancelBooking(trackingNumber: String): Result<Booking> {
        return try {
            val response = bookingApi.cancelBooking(trackingNumber)
            Result.Success(BookingMapper.toDomain(response))
        } catch (e: HttpException) {
            Result.Error("Ошибка отмены бронирования: ${e.code()}")
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка отмены бронирования: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getAllBookings(): Result<List<Booking>> {
        return try {
            val response = bookingApi.getAllBookings()
            Result.Success(response.map { BookingMapper.toDomain(it) })
        } catch (e: HttpException) {
            Result.Error("Ошибка загрузки бронирований: ${e.code()}")
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки бронирований: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun adminGetBooking(trackingNumber: String): Result<Booking> {
        return try {
            val response = bookingApi.adminGetBooking(trackingNumber)
            Result.Success(BookingMapper.toDomain(response))
        } catch (e: HttpException) {
            Result.Error("Ошибка поиска бронирования: ${e.code()}")
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка поиска бронирования: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun adminUpdateBookingStatus(
        trackingNumber: String,
        status: String
    ): Result<Booking> {
        return try {
            val request = BookingStatusUpdateRequest(status)
            val response = bookingApi.adminUpdateBookingStatus(trackingNumber, request)
            Result.Success(BookingMapper.toDomain(response))
        } catch (e: HttpException) {
            Result.Error("Ошибка обновления статуса: ${e.code()}")
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления статуса: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}