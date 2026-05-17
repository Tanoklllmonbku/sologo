package com.sologo.app.models.response.route

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RouteResponse(
    @SerializedName("route_id")
    val routeId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("mood")
    val mood: String,

    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("city_name")
    val cityName: String,

    @SerializedName("duration_hours")
    val durationHours: Int,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("created_at")
    val createdAt: Date
)