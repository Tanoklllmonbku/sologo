package com.sologo.app.domain.usecase.city.admin

import com.sologo.app.domain.model.City
import com.sologo.app.domain.repository.CityRepository
import com.sologo.app.utils.Result

class CreateCityUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(name: String, country: String): Result<City> {
        if (name.isBlank()) {
            return Result.Error("Название города не может быть пустым")
        }
        if (country.isBlank()) {
            return Result.Error("Название страны не может быть пустым")
        }
        return cityRepository.createCity(name, country)
    }
}