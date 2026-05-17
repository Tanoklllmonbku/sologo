package com.sologo.app.network.api

import com.sologo.app.models.request.city.CityCreateRequest
import com.sologo.app.models.request.city.CityUpdateRequest
import com.sologo.app.models.response.city.CityResponse
import retrofit2.http.*

interface CityApi {
    // Public endpoints
    @GET("cities/")
    suspend fun getCities(): List<CityResponse>

    @GET("cities/{cityId}")
    suspend fun getCityById(@Path("cityId") cityId: Int): CityResponse

    // Admin endpoints
    @POST("cities/")
    suspend fun createCity(@Body request: CityCreateRequest): CityResponse

    @PATCH("cities/{cityId}")
    suspend fun updateCity(
        @Path("cityId") cityId: Int,
        @Body request: CityUpdateRequest
    ): CityResponse

    @DELETE("cities/{cityId}")
    suspend fun deleteCity(@Path("cityId") cityId: Int): Map<String, String>
}