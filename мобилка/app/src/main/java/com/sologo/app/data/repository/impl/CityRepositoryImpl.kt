// data/repository/impl/CityRepositoryImpl.kt
package com.sologo.app.data.repository.impl

import com.sologo.app.data.mapper.CityMapper
import com.sologo.app.domain.model.City
import com.sologo.app.domain.repository.CityRepository
import com.sologo.app.models.request.city.CityCreateRequest
import com.sologo.app.models.request.city.CityUpdateRequest
import com.sologo.app.network.api.BookingApi
import com.sologo.app.network.api.CityApi
import com.sologo.app.utils.Result
import java.io.IOException

class CityRepositoryImpl(
    private val cityApi: CityApi
) : CityRepository {

    override suspend fun getCities(): Result<List<City>> {
        return try {
            val response = cityApi.getCities()
            Result.Success(response.map { CityMapper.toDomain(it) })
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки городов: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun getCityById(cityId: Int): Result<City> {
        return try {
            val response = cityApi.getCityById(cityId)
            Result.Success(CityMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка загрузки города: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun createCity(name: String, country: String): Result<City> {
        return try {
            val request = CityCreateRequest(name, country)
            val response = cityApi.createCity(request)
            Result.Success(CityMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка создания города: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun updateCity(cityId: Int, name: String?, country: String?): Result<City> {
        return try {
            val request = CityUpdateRequest(name, country)
            val response = cityApi.updateCity(cityId, request)
            Result.Success(CityMapper.toDomain(response))
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка обновления города: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    override suspend fun deleteCity(cityId: Int): Result<Unit> {
        return try {
            cityApi.deleteCity(cityId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Ошибка удаления города: ${e.message ?: "Неизвестная ошибка"}")
        }
    }
}