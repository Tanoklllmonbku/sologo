package com.sologo.app.models.response.hotel

import com.google.gson.annotations.SerializedName
import java.util.Date

data class HotelResponse(
    @SerializedName("hotel_id")
    val hotelId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("city_id")
    val cityId: Int,

    @SerializedName("city_name")
    val cityName: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("price_per_night")
    val pricePerNight: Int,

    @SerializedName("avg_city_price")
    val avgCityPrice: Int,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("capacity")
    val capacity: Int = 10,

    @SerializedName("manager_phones")
    val managerPhones: List<String>? = null,

    @SerializedName("main_image")
    val mainImage: String? = null,

    @SerializedName("room_images")
    val roomImages: String? = null,

    @SerializedName("status")
    val status: Int,

    @SerializedName("created_at")
    val createdAt: Date
)