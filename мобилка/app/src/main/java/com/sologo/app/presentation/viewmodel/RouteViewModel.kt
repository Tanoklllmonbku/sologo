package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Route
import com.sologo.app.domain.usecase.route.GetRouteByIdUseCase
import com.sologo.app.domain.usecase.route.GetRoutesUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RouteFilters(
    val cityName: String? = null,
    val mood: String? = null,
    val minDuration: Int? = null,
    val maxDuration: Int? = null
)

class RouteViewModel(
    private val getRoutesUseCase: GetRoutesUseCase,
    private val getRouteByIdUseCase: GetRouteByIdUseCase
) : ViewModel() {

    // Все маршруты (оригинал)
    private val _allRoutes = MutableStateFlow<List<Route>>(emptyList())

    // Отфильтрованные маршруты
    private val _filteredRoutes = MutableStateFlow<Result<List<Route>>>(Result.Idle)
    val filteredRoutes: StateFlow<Result<List<Route>>> = _filteredRoutes.asStateFlow()

    // Текущие фильтры
    private val _filters = MutableStateFlow(RouteFilters())
    val filters: StateFlow<RouteFilters> = _filters.asStateFlow()

    // Детали маршрута
    private val _routeDetailState = MutableStateFlow<Result<Route>>(Result.Idle)
    val routeDetailState: StateFlow<Result<Route>> = _routeDetailState.asStateFlow()  // ← ИСПРАВЛЕНО

    // Загрузка всех маршрутов
    fun loadRoutes() {
        viewModelScope.launch {
            _filteredRoutes.value = Result.Loading
            val result = getRoutesUseCase(cityId = null, mood = null)
            if (result is Result.Success) {
                _allRoutes.value = result.data
                applyFilters()
            } else if (result is Result.Error) {
                _filteredRoutes.value = result
            }
        }
    }

    // Применение фильтров
    fun updateFilters(newFilters: RouteFilters) {
        _filters.value = newFilters
        applyFilters()
    }

    // Сброс фильтров
    fun clearFilters() {
        _filters.value = RouteFilters()
        applyFilters()
    }

    // Применить фильтры к _allRoutes
    private fun applyFilters() {
        val routes = _allRoutes.value
        val filters = _filters.value

        val filtered = routes.filter { route ->
            var matches = true

            filters.cityName?.let { city ->
                if (!route.cityName.contains(city, ignoreCase = true)) {
                    matches = false
                }
            }

            filters.mood?.let { mood ->
                if (!route.mood.name.equals(mood, ignoreCase = true)) {
                    matches = false
                }
            }

            filters.minDuration?.let { min ->
                if (route.durationHours < min) matches = false
            }

            filters.maxDuration?.let { max ->
                if (route.durationHours > max) matches = false
            }

            matches
        }

        _filteredRoutes.value = Result.Success(filtered)
    }

    // Загрузка деталей маршрута
    fun loadRouteById(routeId: Int) {
        viewModelScope.launch {
            _routeDetailState.value = Result.Loading
            val result = getRouteByIdUseCase(routeId)
            _routeDetailState.value = result
        }
    }
}