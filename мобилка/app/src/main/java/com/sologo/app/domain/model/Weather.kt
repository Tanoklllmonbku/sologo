package com.sologo.app.domain.model

data class Weather(
    val city: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String
)