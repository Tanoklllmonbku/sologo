package com.sologo.app.models.request.lost

import com.google.gson.annotations.SerializedName

data class LostReportStatusUpdateRequest(
    @SerializedName("status")
    val status: String // "pending", "accepted", "completed", "cancelled"
)