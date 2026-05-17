// presentation/viewmodel/WeatherViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Weather
import com.sologo.app.domain.usecase.weather.GetWeatherUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Result<Weather>>(Result.Idle)
    val weatherState: StateFlow<Result<Weather>> = _weatherState.asStateFlow()

    fun loadWeather(city: String) {
        viewModelScope.launch {
            _weatherState.value = Result.Loading
            val result = getWeatherUseCase(city)
            _weatherState.value = result
        }
    }
}