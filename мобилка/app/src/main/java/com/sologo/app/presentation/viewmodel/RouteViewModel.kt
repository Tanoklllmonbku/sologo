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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ← ДОБАВИТЬ ОПРЕДЕЛЕНИЕ RouteFilters
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

    // ← ДОБАВИТЬ ДЛЯ ФИЛЬТРАЦИИ
    private val _allRoutes = MutableStateFlow<List<Route>>(emptyList())
    private val _filters = MutableStateFlow(RouteFilters())

    private val _filteredRoutes = MutableStateFlow<Result<List<Route>>>(Result.Idle)
    val filteredRoutes: StateFlow<Result<List<Route>>> = _filteredRoutes.asStateFlow()

    val filters: StateFlow<RouteFilters> = _filters.asStateFlow()

    private val _routeDetailState = MutableStateFlow<Result<Route>>(Result.Idle)
    val routeDetailState: StateFlow<Result<Route>> = _routeDetailState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_allRoutes, _filters) { routes, filters ->
                applyFilters(routes, filters)
            }.collect { filtered ->
                _filteredRoutes.value = Result.Success(filtered)
            }
        }
    }

    fun loadRoutes(cityId: Int? = null, mood: String? = null) {
        viewModelScope.launch {
            _filteredRoutes.value = Result.Loading
            val result = getRoutesUseCase(cityId, mood)
            if (result is Result.Success) {
                _allRoutes.value = result.data
            } else if (result is Result.Error) {
                _filteredRoutes.value = result
            }
        }
    }

    // ← ДОБАВИТЬ МЕТОДЫ ДЛЯ ФИЛЬТРОВ
    fun updateFilters(newFilters: RouteFilters) {
        _filters.value = newFilters
    }

    fun clearFilters() {
        _filters.value = RouteFilters()
    }

    private fun applyFilters(routes: List<Route>, filters: RouteFilters): List<Route> {
        return routes.filter { route ->
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
    }

    fun loadRouteById(routeId: Int) {
        viewModelScope.launch {
            _routeDetailState.value = Result.Loading
            val result = getRouteByIdUseCase(routeId)
            _routeDetailState.value = result
        }
    }
}