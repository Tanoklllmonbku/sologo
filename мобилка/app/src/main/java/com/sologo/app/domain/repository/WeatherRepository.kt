package com.sologo.app.domain.repository

import com.sologo.app.domain.model.Weather
import com.sologo.app.utils.Result

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<Weather>
}