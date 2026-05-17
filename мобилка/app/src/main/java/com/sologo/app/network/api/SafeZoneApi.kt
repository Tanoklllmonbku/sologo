package com.sologo.app.network.api

import com.sologo.app.models.request.safezone.SafeZoneCreateRequest
import com.sologo.app.models.request.safezone.SafeZoneUpdateRequest
import com.sologo.app.models.response.safezone.SafeZoneResponse
import retrofit2.http.*

interface SafeZoneApi {
    // Public endpoints
    @GET("safe-zones/")
    suspend fun getSafeZones(
        @Query("city_id") cityId: Int? = null
    ): List<SafeZoneResponse>

    // Admin endpoints
    @POST("safe-zones/")
    suspend fun createSafeZone(@Body request: SafeZoneCreateRequest): SafeZoneResponse

    @PATCH("safe-zones/{zoneId}")
    suspend fun updateSafeZone(
        @Path("zoneId") zoneId: Int,
        @Body request: SafeZoneUpdateRequest
    ): SafeZoneResponse

    @DELETE("safe-zones/{zoneId}")
    suspend fun deleteSafeZone(@Path("zoneId") zoneId: Int): Map<String, String>
}