package com.sologo.app.domain.repository

import com.sologo.app.domain.model.Route
import com.sologo.app.utils.Result

interface RouteRepository {
    suspend fun getRoutes(cityId: Int?, mood: String?): Result<List<Route>>
    suspend fun getRouteById(routeId: Int): Result<Route>
    suspend fun createRoute(
        title: String,
        cityId: Int,
        mood: String,
        description: String?,
        durationHours: Int,
        image: String?
    ): Result<Route>
    suspend fun updateRoute(
        routeId: Int,
        title: String?,
        description: String?,
        mood: String?,
        cityId: Int?,
        durationHours: Int?,
        image: String?
    ): Result<Route>
    suspend fun deleteRoute(routeId: Int): Result<Unit>
}