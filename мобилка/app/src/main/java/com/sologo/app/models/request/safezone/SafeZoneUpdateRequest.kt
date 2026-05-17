package com.sologo.app.models.request.safezone

import com.google.gson.annotations.SerializedName

data class SafeZoneUpdateRequest(
    @SerializedName("district")
    val district: String? = null,

    @SerializedName("city_id")
    val cityId: Int? = null,

    @SerializedName("level")
    val level: String? = null,

    @SerializedName("note")
    val note: String? = null
)