// presentation/viewmodel/admin/AdminCityViewModel.kt
package com.sologo.app.presentation.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.City
import com.sologo.app.domain.usecase.city.GetCitiesUseCase
import com.sologo.app.domain.usecase.city.admin.*
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminCityViewModel(
    private val getAllCitiesUseCase: GetCitiesUseCase,
    private val createCityUseCase: CreateCityUseCase,
    private val updateCityUseCase: UpdateCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase
) : ViewModel() {

    private val _citiesState = MutableStateFlow<Result<List<City>>>(Result.Idle)
    val citiesState: StateFlow<Result<List<City>>> = _citiesState.asStateFlow()

    private val _operationState = MutableStateFlow<Result<Unit>>(Result.Idle)
    val operationState: StateFlow<Result<Unit>> = _operationState.asStateFlow()

    fun loadCities() {
        viewModelScope.launch {
            _citiesState.value = Result.Loading
            val result = getAllCitiesUseCase()
            _citiesState.value = result
        }
    }

    fun createCity(name: String, country: String) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            val result = createCityUseCase(name, country)
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                is Result.Loading -> Result.Loading
                is Result.Idle -> Result.Idle
            }
            if (result is Result.Success) {
                loadCities()
            }
        }
    }

    fun updateCity(cityId: Int, name: String, country: String) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            val result = updateCityUseCase(cityId, name, country)
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                is Result.Loading -> Result.Loading
                is Result.Idle -> Result.Idle
            }
            if (result is Result.Success) {
                loadCities()
            }
        }
    }

    fun deleteCity(cityId: Int) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            val result = deleteCityUseCase(cityId)
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                is Result.Loading -> Result.Loading
                is Result.Idle -> Result.Idle
            }
            if (result is Result.Success) {
                loadCities()
            }
        }
    }

    fun clearOperationState() {
        _operationState.value = Result.Idle
    }
}