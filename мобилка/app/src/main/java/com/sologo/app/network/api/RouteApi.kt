package com.sologo.app.network.api

import com.sologo.app.models.request.route.RouteCreateRequest
import com.sologo.app.models.request.route.RouteUpdateRequest
import com.sologo.app.models.response.route.RouteResponse
import retrofit2.http.*

interface RouteApi {
    // Public endpoints
    @GET("routes/")
    suspend fun getRoutes(
        @Query("city_id") cityId: Int? = null,
        @Query("mood") mood: String? = null
    ): List<RouteResponse>

    @GET("routes/{routeId}")
    suspend fun getRouteById(@Path("routeId") routeId: Int): RouteResponse

    // Admin endpoints
    @POST("routes/")
    suspend fun createRoute(@Body request: RouteCreateRequest): RouteResponse

    @PATCH("routes/{routeId}")
    suspend fun updateRoute(
        @Path("routeId") routeId: Int,
        @Body request: RouteUpdateRequest
    ): RouteResponse

    @DELETE("routes/{routeId}")
    suspend fun deleteRoute(@Path("routeId") routeId: Int): Map<String, String>
}