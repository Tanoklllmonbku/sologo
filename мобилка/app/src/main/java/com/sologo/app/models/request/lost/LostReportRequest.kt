package com.sologo.app.models.request.lost

import com.google.gson.annotations.SerializedName

data class LostReportRequest(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("message")
    val message: String? = null
)