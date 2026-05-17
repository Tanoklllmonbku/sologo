package com.sologo.app.domain.usecase.weather

import com.sologo.app.domain.model.Weather
import com.sologo.app.domain.repository.WeatherRepository
import com.sologo.app.utils.Result

class GetWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(city: String): Result<Weather> {
        if (city.isBlank()) {
            return Result.Error("Название города не может быть пустым")
        }
        return weatherRepository.getWeather(city)
    }
}