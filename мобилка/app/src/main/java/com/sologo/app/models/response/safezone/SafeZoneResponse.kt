package com.sologo.app.models.response.safezone

import com.google.gson.annotations.SerializedName

data class SafeZoneResponse(
    @SerializedName("zone_id")
    val zoneId: Int,

    @SerializedName("district")
    val district: String,

    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("city_name")
    val cityName: String,

    @SerializedName("level")
    val level: String,

    @SerializedName("note")
    val note: String? = null
)