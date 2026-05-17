package com.sologo.app.models.request.city

import com.google.gson.annotations.SerializedName

data class CityUpdateRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("country")
    val country: String? = null
)