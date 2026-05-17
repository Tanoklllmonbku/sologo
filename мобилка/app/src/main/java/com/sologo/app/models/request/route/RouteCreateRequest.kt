package com.sologo.app.models.request.route

import com.google.gson.annotations.SerializedName

data class RouteCreateRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("mood")
    val mood: String, // "calm", "active", "cultural"

    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("duration_hours")
    val durationHours: Int = 2,

    @SerializedName("image")
    val image: String? = null
)