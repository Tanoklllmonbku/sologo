// presentation/viewmodel/RouteViewModel.kt
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

class RouteViewModel(
    private val getRoutesUseCase: GetRoutesUseCase,
    private val getRouteByIdUseCase: GetRouteByIdUseCase
) : ViewModel() {

    private val _routesState = MutableStateFlow<Result<List<Route>>>(Result.Idle)
    val routesState: StateFlow<Result<List<Route>>> = _routesState.asStateFlow()

    private val _routeDetailState = MutableStateFlow<Result<Route>>(Result.Idle)
    val routeDetailState: StateFlow<Result<Route>> = _routeDetailState.asStateFlow()

    fun loadRoutes(cityId: Int? = null, mood: String? = null) {
        viewModelScope.launch {
            _routesState.value = Result.Loading
            val result = getRoutesUseCase(cityId, mood)
            _routesState.value = result
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