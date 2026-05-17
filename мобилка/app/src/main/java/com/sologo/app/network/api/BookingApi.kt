package com.sologo.app.network.api

import com.sologo.app.models.request.booking.BookingRequest
import com.sologo.app.models.request.booking.BookingStatusUpdateRequest
import com.sologo.app.models.response.booking.BookingResponse
import retrofit2.http.*

interface BookingApi {

    // ========== USER BOOKINGS ==========

    // Создать бронирование
    @POST("bookings/")
    suspend fun createBooking(
        @Body request: BookingRequest
    ): BookingResponse

    // Получить мои бронирования
    @GET("bookings/my")
    suspend fun getMyBookings(): List<BookingResponse>

    // Отменить бронирование по tracking number
    @PATCH("bookings/cancel/{trackingNumber}")
    suspend fun cancelBooking(
        @Path("trackingNumber") trackingNumber: String
    ): BookingResponse

    // ========== ADMIN BOOKINGS ==========

    // Получить все бронирования (admin)
    @GET("bookings/admin/all")
    suspend fun getAllBookings(): List<BookingResponse>

    // Получить бронирование по tracking number (admin)
    @GET("bookings/admin/{trackingNumber}")
    suspend fun adminGetBooking(
        @Path("trackingNumber") trackingNumber: String
    ): BookingResponse

    // Обновить статус бронирования (admin)
    @PATCH("bookings/admin/{trackingNumber}/status")
    suspend fun adminUpdateBookingStatus(
        @Path("trackingNumber") trackingNumber: String,
        @Body request: BookingStatusUpdateRequest
    ): BookingResponse
}