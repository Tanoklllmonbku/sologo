package com.sologo.app.domain.model

import java.util.Date

data class Route(
    val id: Int,
    val title: String,
    val description: String?,
    val mood: RouteMood,
    val cityId: Int,
    val cityName: String,
    val durationHours: Int,
    val image: String?,
    val createdAt: Date
)

enum class RouteMood {
    CALM, ACTIVE, CULTURAL;

    companion object {
        fun fromString(mood: String): RouteMood {
            return when (mood.lowercase()) {
                "active" -> ACTIVE
                "cultural" -> CULTURAL
                else -> CALM
            }
        }
    }
}