package com.sologo.app.models.request.hotel

import com.google.gson.annotations.SerializedName

data class HotelCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("address")
    val address: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("price_per_night")
    val pricePerNight: Int,

    @SerializedName("avg_city_price")
    val avgCityPrice: Int,

    @SerializedName("rating")
    val rating: Double = 0.0,

    @SerializedName("capacity")
    val capacity: Int = 10,

    @SerializedName("manager_phones")
    val managerPhones: List<String>? = null,

    @SerializedName("main_image")
    val mainImage: String? = null,

    @SerializedName("room_images")
    val roomImages: String? = null
)