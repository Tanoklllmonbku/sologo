package com.sologo.app.models.response.city

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CityResponse(
    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("created_at")
    val createdAt: Date
)