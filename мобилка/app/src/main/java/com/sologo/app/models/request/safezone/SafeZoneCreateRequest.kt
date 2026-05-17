package com.sologo.app.models.request.safezone

import com.google.gson.annotations.SerializedName

data class SafeZoneCreateRequest(
    @SerializedName("district")
    val district: String,

    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("level")
    val level: String, // "high", "medium", "low"

    @SerializedName("note")
    val note: String? = null
)