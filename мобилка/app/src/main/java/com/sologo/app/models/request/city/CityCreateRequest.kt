package com.sologo.app.models.request.city

import com.google.gson.annotations.SerializedName

data class CityCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String
)