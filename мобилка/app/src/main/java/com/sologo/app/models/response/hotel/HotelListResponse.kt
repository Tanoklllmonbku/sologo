package com.sologo.app.models.response.hotel

import com.google.gson.annotations.SerializedName

data class HotelListResponse(
    @SerializedName("hotel_id")
    val hotelId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("city_name")
    val cityName: String,

    @SerializedName("price_per_night")
    val pricePerNight: Int,

    @SerializedName("avg_city_price")
    val avgCityPrice: Int,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("main_image")
    val mainImage: String? = null,

    @SerializedName("capacity")
    val capacity: Int
)