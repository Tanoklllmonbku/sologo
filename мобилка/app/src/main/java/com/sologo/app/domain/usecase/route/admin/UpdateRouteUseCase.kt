package com.sologo.app.domain.usecase.route.admin

import com.sologo.app.domain.model.Route
import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.utils.Result

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(
        routeId: Int,
        title: String? = null,
        description: String? = null,
        mood: String? = null,
        cityId: Int? = null,
        durationHours: Int? = null,
        image: String? = null
    ): Result<Route> {
        if (routeId <= 0) {
            return Result.Error("Неверный ID маршрута")
        }
        if (mood != null) {
            val validMoods = listOf("calm", "active", "cultural")
            if (mood !in validMoods) {
                return Result.Error("Неверный тип настроения. Доступны: ${validMoods.joinToString()}")
            }
        }
        return routeRepository.updateRoute(routeId, title, description, mood, cityId, durationHours, image)
    }
}