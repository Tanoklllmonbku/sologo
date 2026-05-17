package com.sologo.app.domain.repository

import com.sologo.app.domain.model.City
import com.sologo.app.utils.Result

interface CityRepository {
    suspend fun getCities(): Result<List<City>>
    suspend fun getCityById(cityId: Int): Result<City>
    suspend fun createCity(name: String, country: String): Result<City>
    suspend fun updateCity(cityId: Int, name: String?, country: String?): Result<City>
    suspend fun deleteCity(cityId: Int): Result<Unit>
}