package com.sologo.app.models.response.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("city")
    val city: String,

    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("condition")
    val condition: String,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("icon")
    val icon: String
)