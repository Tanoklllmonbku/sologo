// data/mapper/RouteMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.Route
import com.sologo.app.domain.model.RouteMood
import com.sologo.app.models.response.route.RouteResponse

object RouteMapper {
    fun toDomain(response: RouteResponse): Route {
        return Route(
            id = response.routeId,
            title = response.title,
            description = response.description,
            mood = RouteMood.fromString(response.mood),
            cityId = response.cityId,
            cityName = response.cityName,
            durationHours = response.durationHours,
            image = response.image,
            createdAt = response.createdAt
        )
    }
}