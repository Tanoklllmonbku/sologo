package com.sologo.app.domain.model

data class SafeZone(
    val id: Int,
    val district: String,
    val cityId: Int,
    val cityName: String,
    val level: SafetyLevel,
    val note: String?
)

enum class SafetyLevel {
    HIGH, MEDIUM, LOW;

    companion object {
        fun fromString(level: String): SafetyLevel {
            return when (level.lowercase()) {
                "high" -> HIGH
                "low" -> LOW
                else -> MEDIUM
            }
        }
    }
}