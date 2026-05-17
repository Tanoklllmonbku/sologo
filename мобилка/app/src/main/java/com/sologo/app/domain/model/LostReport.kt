package com.sologo.app.domain.model

import java.util.Date

data class LostReport(
    val id: Int,
    val userId: Int,
    val userNickname: String,
    val lat: Double,
    val lng: Double,
    val message: String?,
    val status: LostStatus,
    val createdAt: Date
)

enum class LostStatus {
    PENDING, ACCEPTED, COMPLETED, CANCELLED;

    companion object {
        fun fromString(status: String): LostStatus {
            return when (status.lowercase()) {
                "accepted" -> ACCEPTED
                "completed" -> COMPLETED
                "cancelled" -> CANCELLED
                else -> PENDING
            }
        }
    }
}