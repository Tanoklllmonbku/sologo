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

    private var allRoutes: List<Route> = emptyList()

    private val _filteredRoutes = MutableStateFlow<Result<List<Route>>>(Result.Idle)
    val filteredRoutes: StateFlow<Result<List<Route>>> = _filteredRoutes.asStateFlow()

    private val _filters = MutableStateFlow(RouteFilters())
    val filters: StateFlow<RouteFilters> = _filters.asStateFlow()

    private val _routeDetailState = MutableStateFlow<Result<Route>>(Result.Idle)
    val routeDetailState: StateFlow<Result<Route>> = _routeDetailState.asStateFlow()

    fun loadRoutes(cityId: Int? = null, mood: String? = null) {
        viewModelScope.launch {
            _filteredRoutes.value = Result.Loading
            val result = getRoutesUseCase(cityId, mood)
            if (result is Result.Success) {
                allRoutes = result.data
                applyFilters()
            } else if (result is Result.Error) {
                _filteredRoutes.value = result
            }
        }
    }

    fun refreshRoutes() {
        viewModelScope.launch {
            _filteredRoutes.value = Result.Loading
            val result = getRoutesUseCase(cityId = null, mood = null)
            if (result is Result.Success) {
                allRoutes = result.data
                applyFilters()
            } else if (result is Result.Error) {
                _filteredRoutes.value = result
            }
        }
    }

    fun updateFilters(newFilters: RouteFilters) {
        _filters.value = newFilters
        applyFilters()
    }

    fun clearFilters() {
        _filters.value = RouteFilters()
        applyFilters()
    }

    private fun applyFilters() {
        val currentFilters = _filters.value
        val filtered = allRoutes.filter { route ->
            var matches = true

            currentFilters.cityName?.let { city ->
                if (!route.cityName.contains(city, ignoreCase = true)) {
                    matches = false
                }
            }

            currentFilters.mood?.let { mood ->
                if (!route.mood.name.equals(mood, ignoreCase = true)) {
                    matches = false
                }
            }

            currentFilters.minDuration?.let { min ->
                if (route.durationHours < min) matches = false
            }

            currentFilters.maxDuration?.let { max ->
                if (route.durationHours > max) matches = false
            }

            matches
        }
        _filteredRoutes.value = Result.Success(filtered)
    }

    fun loadRouteById(routeId: Int) {
        viewModelScope.launch {
            _routeDetailState.value = Result.Loading
            val result = getRouteByIdUseCase(routeId)
            _routeDetailState.value = result
        }
    }

    fun clearState() {
        _filteredRoutes.value = Result.Idle
        _routeDetailState.value = Result.Idle
        allRoutes = emptyList()
        _filters.value = RouteFilters()
    }
}