package com.sologo.app.models.response.booking

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BookingResponse (
    @SerializedName("booking_id")
    val bookingId: Int,

    @SerializedName("tracking_number")
    val trackingNumber: String,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("hotel_id")
    val hotelId: Int,

    @SerializedName("hotel_name")
    val hotelName: String,

    @SerializedName("hotel_city")
    val hotelCity: String,

    @SerializedName("guests_count")
    val guestsCount: Int,

    @SerializedName("check_in")
    val checkIn: Date,  // ← теперь Date, не String

    @SerializedName("check_out")
    val checkOut: Date,  // ← теперь Date, не String

    @SerializedName("total_price")
    val totalPrice: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: Date,  // ← теперь Date, не String

    @SerializedName("days")
    val days: Int
)