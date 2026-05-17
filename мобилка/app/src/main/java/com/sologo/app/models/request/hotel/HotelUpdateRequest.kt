package com.sologo.app.models.request.hotel

import com.google.gson.annotations.SerializedName

data class HotelUpdateRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("city_id")
    val cityId: Int? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("price_per_night")
    val pricePerNight: Int? = null,

    @SerializedName("avg_city_price")
    val avgCityPrice: Int? = null,

    @SerializedName("rating")
    val rating: Double? = null,

    @SerializedName("capacity")
    val capacity: Int? = null,

    @SerializedName("manager_phones")
    val managerPhones: List<String>? = null,

    @SerializedName("main_image")
    val mainImage: String? = null,

    @SerializedName("room_images")
    val roomImages: String? = null,

    @SerializedName("status")
    val status: Int? = null
)