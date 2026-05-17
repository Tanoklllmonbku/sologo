// data/mapper/SafeZoneMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.model.SafetyLevel
import com.sologo.app.models.response.safezone.SafeZoneResponse

object SafeZoneMapper {
    fun toDomain(response: SafeZoneResponse): SafeZone {
        return SafeZone(
            id = response.zoneId,
            district = response.district,
            cityId = response.cityId,
            cityName = response.cityName,
            level = SafetyLevel.fromString(response.level),
            note = response.note
        )
    }
}