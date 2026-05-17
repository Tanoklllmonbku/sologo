package com.sologo.app.models.request.booking

import com.google.gson.annotations.SerializedName

data class BookingStatusUpdateRequest (
    @SerializedName("status")
    val status: String
)