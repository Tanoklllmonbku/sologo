package com.sologo.app.domain.usecase.route.admin

import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.utils.Result

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(routeId: Int): Result<Unit> {
        if (routeId <= 0) {
            return Result.Error("Неверный ID маршрута")
        }
        return routeRepository.deleteRoute(routeId)
    }
}