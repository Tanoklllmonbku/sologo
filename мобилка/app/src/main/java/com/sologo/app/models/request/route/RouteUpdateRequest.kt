package com.sologo.app.models.request.route

import com.google.gson.annotations.SerializedName

data class RouteUpdateRequest(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("mood")
    val mood: String? = null,

    @SerializedName("city_id")
    val cityId: Int? = null,

    @SerializedName("duration_hours")
    val durationHours: Int? = null,

    @SerializedName("image")
    val image: String? = null
)