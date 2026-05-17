// data/mapper/HotelMapper.kt
package com.sologo.app.data.mapper

import com.sologo.app.domain.model.Hotel
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.models.response.hotel.HotelListResponse
import com.sologo.app.models.response.hotel.HotelResponse

object HotelMapper {
    fun toDomain(listResponse: HotelListResponse): Hotel {
        return Hotel(
            id = listResponse.hotelId,
            name = listResponse.name,
            cityName = listResponse.cityName,
            pricePerNight = listResponse.pricePerNight,
            avgCityPrice = listResponse.avgCityPrice,
            rating = listResponse.rating,
            mainImage = listResponse.mainImage,
            capacity = listResponse.capacity
        )
    }

    fun toDomain(detailResponse: HotelResponse): HotelDetail {
        return HotelDetail(
            id = detailResponse.hotelId,
            name = detailResponse.name,
            cityId = detailResponse.cityId,
            cityName = detailResponse.cityName,
            address = detailResponse.address,
            description = detailResponse.description,
            pricePerNight = detailResponse.pricePerNight,
            avgCityPrice = detailResponse.avgCityPrice,
            rating = detailResponse.rating,
            capacity = detailResponse.capacity,
            managerPhones = detailResponse.managerPhones,
            mainImage = detailResponse.mainImage,
            roomImages = detailResponse.roomImages,
            status = detailResponse.status,
            createdAt = detailResponse.createdAt
        )
    }
}