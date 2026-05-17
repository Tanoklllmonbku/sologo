package com.sologo.app.models.request.booking

import com.google.gson.annotations.SerializedName

data class BookingRequest (
    @SerializedName("hotel_id")
    val hotelId: Int,

    @SerializedName("guests_count")
    val guestsCount: Int,

    @SerializedName("check_in")
    val checkIn: String,

    @SerializedName("check_out")
    val checkOut: String,
)