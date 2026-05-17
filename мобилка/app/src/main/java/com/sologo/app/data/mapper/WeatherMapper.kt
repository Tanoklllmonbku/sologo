// data/mapper/WeatherMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.Weather
import com.sologo.app.models.response.weather.WeatherResponse

object WeatherMapper {
    fun toDomain(response: WeatherResponse): Weather {
        return Weather(
            city = response.city,
            temperature = response.temperature,
            condition = response.condition,
            humidity = response.humidity,
            windSpeed = response.windSpeed,
            icon = response.icon
        )
    }
}