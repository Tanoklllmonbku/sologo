package com.sologo.app.network.api

import com.sologo.app.models.request.hotel.HotelCreateRequest
import com.sologo.app.models.request.hotel.HotelUpdateRequest
import com.sologo.app.models.response.hotel.HotelListResponse
import com.sologo.app.models.response.hotel.HotelResponse
import retrofit2.http.*

interface HotelApi {
    // Public endpoints
    @GET("hotels/")
    suspend fun getHotels(
        @Query("city_id") cityId: Int? = null,
        @Query("affordable") affordable: Boolean = false
    ): List<HotelListResponse>

    @GET("hotels/{hotelId}")
    suspend fun getHotelById(@Path("hotelId") hotelId: Int): HotelResponse

    // Admin endpoints
    @POST("hotels/")
    suspend fun createHotel(@Body request: HotelCreateRequest): HotelResponse

    @PATCH("hotels/{hotelId}")
    suspend fun updateHotel(
        @Path("hotelId") hotelId: Int,
        @Body request: HotelUpdateRequest
    ): HotelResponse

    @DELETE("hotels/{hotelId}")
    suspend fun deleteHotel(@Path("hotelId") hotelId: Int): Map<String, String>

    @PATCH("hotels/admin/{hotelId}/restore")
    suspend fun restoreHotel(@Path("hotelId") hotelId: Int): Map<String, String>
}