package com.sologo.app.domain.usecase.city.admin

import com.sologo.app.domain.repository.CityRepository
import com.sologo.app.utils.Result

class DeleteCityUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(cityId: Int): Result<Unit> {
        if (cityId <= 0) {
            return Result.Error("Неверный ID города")
        }
        return cityRepository.deleteCity(cityId)
    }
}