package com.sologo.app.models.response.lost

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LostReportResponse(
    @SerializedName("report_id")
    val reportId: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("user_nickname")
    val userNickname: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: Date
)