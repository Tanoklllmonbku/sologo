package com.sologo.app.domain.model

import java.util.Date

data class Hotel(
    val id: Int,
    val name: String,
    val cityName: String,
    val pricePerNight: Int,
    val avgCityPrice: Int,
    val rating: Double,
    val mainImage: String?,
    val capacity: Int
)

data class HotelDetail(
    val id: Int,
    val name: String,
    val cityId: Int,
    val cityName: String,
    val address: String,
    val description: String?,
    val pricePerNight: Int,
    val avgCityPrice: Int,
    val rating: Double,
    val capacity: Int,
    val managerPhones: List<String>?,
    val mainImage: String?,
    val roomImages: String?,
    val status: Int,
    val createdAt: Date
)