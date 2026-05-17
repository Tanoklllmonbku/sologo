// data/repository/impl/WeatherRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.WeatherMapper
import com.sologo.app.domain.model.Weather
import com.sologo.app.domain.repository.WeatherRepository
import com.sologo.app.network.api.BookingApi
import com.sologo.app.network.api.WeatherApi
import com.sologo.app.utils.Result
import java.io.IOException

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(city: String): Result<Weather> {
        return try {
            val response = weatherApi.getWeather(city)
            Result.Success(WeatherMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки погоды: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}