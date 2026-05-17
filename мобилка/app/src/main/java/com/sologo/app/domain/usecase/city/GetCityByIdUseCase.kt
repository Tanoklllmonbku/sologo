package com.sologo.app.domain.usecase.city

import com.sologo.app.domain.model.City
import com.sologo.app.domain.repository.CityRepository
import com.sologo.app.utils.Result

class GetCityByIdUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(cityId: Int): Result<City> {
        if (cityId <= 0) {
            return Result.Error("Неверный ID города")
        }
        return cityRepository.getCityById(cityId)
    }
}