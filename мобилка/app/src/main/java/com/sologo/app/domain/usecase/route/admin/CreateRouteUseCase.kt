package com.sologo.app.domain.usecase.route.admin

import com.sologo.app.domain.model.Route
import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.utils.Result

class CreateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(
        title: String,
        cityId: Int,
        mood: String,
        description: String? = null,
        durationHours: Int = 2,
        image: String? = null
    ): Result<Route> {
        if (title.isBlank()) {
            return Result.Error("Название маршрута не может быть пустым")
        }
        if (cityId <= 0) {
            return Result.Error("Неверный ID города")
        }
        val validMoods = listOf("calm", "active", "cultural")
        if (mood !in validMoods) {
            return Result.Error("Неверный тип настроения. Доступны: ${validMoods.joinToString()}")
        }
        if (durationHours <= 0) {
            return Result.Error("Длительность должна быть больше 0")
        }
        return routeRepository.createRoute(title, cityId, mood, description, durationHours, image)
    }
}