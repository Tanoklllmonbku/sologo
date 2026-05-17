package com.sologo.app.domain.usecase.city.admin

import com.sologo.app.domain.model.City
import com.sologo.app.domain.repository.CityRepository
import com.sologo.app.utils.Result

class UpdateCityUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(
        cityId: Int,
        name: String? = null,
        country: String? = null
    ): Result<City> {
        if (cityId <= 0) {
            return Result.Error("Неверный ID города")
        }
        return cityRepository.updateCity(cityId, name, country)
    }
}