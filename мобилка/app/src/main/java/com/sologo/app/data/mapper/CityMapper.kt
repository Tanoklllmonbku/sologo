// data/mapper/CityMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.City
import com.sologo.app.models.response.city.CityResponse

object CityMapper {
    fun toDomain(response: CityResponse): City {
        return City(
            id = response.cityId,
            name = response.name,
            country = response.country,
            createdAt = response.createdAt
        )
    }
}