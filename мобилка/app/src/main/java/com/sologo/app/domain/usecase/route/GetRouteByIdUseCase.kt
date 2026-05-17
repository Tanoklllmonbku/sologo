package com.sologo.app.domain.usecase.route

import com.sologo.app.domain.model.Route
import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.utils.Result

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(routeId: Int): Result<Route> {
        if (routeId <= 0) {
            return Result.Error("Неверный ID маршрута")
        }
        return routeRepository.getRouteById(routeId)
    }
}