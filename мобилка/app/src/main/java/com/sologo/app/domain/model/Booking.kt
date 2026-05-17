package com.sologo.app.domain.model

import java.util.Date

data class Booking(
    val id: Int,
    val trackingNumber: String,
    val userId: Int,
    val hotelId: Int,
    val hotelName: String,
    val hotelCity: String,
    val guestsCount: Int,
    val checkIn: Date,
    val checkOut: Date,
    val totalPrice: Int,
    val status: BookingStatus,
    val days: Int,
    val createdAt: Date
)

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED;

    companion object {
        fun fromString(status: String): BookingStatus {
            return when (status.lowercase()) {
                "confirmed" -> CONFIRMED
                "cancelled" -> CANCELLED
                "completed" -> COMPLETED
                else -> PENDING
            }
        }
    }
}