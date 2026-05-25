package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Hotel
import com.sologo.app.domain.model.Route
import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.model.Weather
import com.sologo.app.domain.repository.HotelRepository
import com.sologo.app.domain.repository.RouteRepository
import com.sologo.app.domain.repository.SafeZoneRepository
import com.sologo.app.domain.repository.WeatherRepository
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityDetailViewModel(
    private val weatherRepository: WeatherRepository,
    private val hotelRepository: HotelRepository,
    private val routeRepository: RouteRepository,
    private val safeZoneRepository: SafeZoneRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Result<Weather>>(Result.Idle)
    val weatherState: StateFlow<Result<Weather>> = _weatherState.asStateFlow()

    private val _hotelsState = MutableStateFlow<Result<List<Hotel>>>(Result.Idle)
    val hotelsState: StateFlow<Result<List<Hotel>>> = _hotelsState.asStateFlow()

    private val _routesState = MutableStateFlow<Result<List<Route>>>(Result.Idle)
    val routesState: StateFlow<Result<List<Route>>> = _routesState.asStateFlow()

    private val _safeZonesState = MutableStateFlow<Result<List<SafeZone>>>(Result.Idle)
    val safeZonesState: StateFlow<Result<List<SafeZone>>> = _safeZonesState.asStateFlow()

    fun loadCityData(cityId: Int, cityName: String) {
        viewModelScope.launch {
            // Загружаем все параллельно
            listOf(
                launch { loadWeather(cityName) },
                launch { loadHotels(cityId) },
                launch { loadRoutes(cityId) },
                launch { loadSafeZones(cityId) }
            )
        }
    }

    private suspend fun loadWeather(cityName: String) {
        _weatherState.value = Result.Loading
        val result = weatherRepository.getWeather(cityName)
        _weatherState.value = result
    }

    private suspend fun loadHotels(cityId: Int) {
        _hotelsState.value = Result.Loading
        val result = hotelRepository.getHotels(cityId = cityId, affordable = false)
        _hotelsState.value = result
    }

    private suspend fun loadRoutes(cityId: Int) {
        _routesState.value = Result.Loading
        val result = routeRepository.getRoutes(cityId = cityId, mood = null)
        _routesState.value = result
    }

    private suspend fun loadSafeZones(cityId: Int) {
        _safeZonesState.value = Result.Loading
        val result = safeZoneRepository.getSafeZones(cityId = cityId)
        _safeZonesState.value = result
    }
}