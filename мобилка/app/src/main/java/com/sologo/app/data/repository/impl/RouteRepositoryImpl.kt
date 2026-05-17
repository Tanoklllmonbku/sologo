// data/repository/impl/RouteRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.RouteMapper
import com.sologo.app.domain.model.Route
import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.models.request.route.RouteCreateRequest
import com.sologo.app.models.request.route.RouteUpdateRequest
import com.sologo.app.network.api.BookingApi
import com.sologo.app.network.api.RouteApi
import com.sologo.app.utils.Result
import java.io.IOException

class RouteRepositoryImpl(
    private val routeApi: RouteApi
) : RouteRepository {


    override suspend fun getRoutes(cityId: Int?, mood: String?): Result<List<Route>> {
        return try {
            val response = routeApi.getRoutes(cityId, mood)
            Result.Success(response.map { RouteMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки маршрутов: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getRouteById(routeId: Int): Result<Route> {
        return try {
            val response = routeApi.getRouteById(routeId)
            Result.Success(RouteMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки маршрута: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun createRoute(
        title: String,
        cityId: Int,
        mood: String,
        description: String?,
        durationHours: Int,
        image: String?
    ): Result<Route> {
        return try {
            val request = RouteCreateRequest(title, description, mood, cityId, durationHours, image)
            val response = routeApi.createRoute(request)
            Result.Success(RouteMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка создания маршрута: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updateRoute(
        routeId: Int,
        title: String?,
        description: String?,
        mood: String?,
        cityId: Int?,
        durationHours: Int?,
        image: String?
    ): Result<Route> {
        return try {
            val request = RouteUpdateRequest(title, description, mood, cityId, durationHours, image)
            val response = routeApi.updateRoute(routeId, request)
            Result.Success(RouteMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления маршрута: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun deleteRoute(routeId: Int): Result<Unit> {
        return try {
            routeApi.deleteRoute(routeId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка удаления маршрута: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}