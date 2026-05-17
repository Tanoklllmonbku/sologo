package com.sologo.app.network.api

import com.sologo.app.models.response.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather/")
    suspend fun getWeather(
        @Query("city") city: String
    ): WeatherResponse
}