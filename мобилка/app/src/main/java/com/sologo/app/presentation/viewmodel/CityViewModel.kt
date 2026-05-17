// presentation/viewmodel/CityViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.City
import com.sologo.app.domain.usecase.city.GetCitiesUseCase
import com.sologo.app.domain.usecase.city.GetCityByIdUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityViewModel(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getCityByIdUseCase: GetCityByIdUseCase
) : ViewModel() {

    private val _citiesState = MutableStateFlow<Result<List<City>>>(Result.Idle)
    val citiesState: StateFlow<Result<List<City>>> = _citiesState.asStateFlow()

    private val _cityDetailState = MutableStateFlow<Result<City>>(Result.Idle)
    val cityDetailState: StateFlow<Result<City>> = _cityDetailState.asStateFlow()

    fun loadCities() {
        viewModelScope.launch {
            _citiesState.value = Result.Loading
            val result = getCitiesUseCase()
            _citiesState.value = result
        }
    }

    fun loadCityById(cityId: Int) {
        viewModelScope.launch {
            _cityDetailState.value = Result.Loading
            val result = getCityByIdUseCase(cityId)
            _cityDetailState.value = result
        }
    }
}