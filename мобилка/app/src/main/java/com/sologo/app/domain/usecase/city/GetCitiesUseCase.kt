package com.sologo.app.domain.usecase.city

import com.sologo.app.domain.model.City
import com.sologo.app.domain.repository.CityRepository
import com.sologo.app.utils.Result

class GetCitiesUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(): Result<List<City>> {
        return cityRepository.getCities()
    }
}