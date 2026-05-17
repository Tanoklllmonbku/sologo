package com.sologo.app.domain.usecase.route

import com.sologo.app.domain.model.Route
import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.utils.Result

class GetRoutesUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(
        cityId: Int? = null,
        mood: String? = null
    ): Result<List<Route>> {
        return routeRepository.getRoutes(cityId, mood)
    }
}