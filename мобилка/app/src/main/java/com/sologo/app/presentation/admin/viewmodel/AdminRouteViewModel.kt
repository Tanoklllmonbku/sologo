package com.sologo.app.presentation.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.City
import com.sologo.app.domain.model.Route
import com.sologo.app.domain.usecase.city.GetCitiesUseCase
import com.sologo.app.domain.usecase.route.GetRoutesUseCase
import com.sologo.app.domain.usecase.route.admin.*
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AdminRouteViewModel(
    private val getRoutesUseCase: GetRoutesUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val createRouteUseCase: CreateRouteUseCase,
    private val updateRouteUseCase: UpdateRouteUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase
) : ViewModel() {

    private val _routesState = MutableStateFlow<Result<List<Route>>>(Result.Idle)
    val routesState: StateFlow<Result<List<Route>>> = _routesState.asStateFlow()

    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()

    fun loadRoutes() {
        viewModelScope.launch {
            _routesState.value = Result.Loading
            val result = getRoutesUseCase(cityId = null, mood = null)
            _routesState.value = result
        }
    }

    fun loadCities() {
        viewModelScope.launch {
            val result = getCitiesUseCase()
            if (result is Result.Success) _cities.value = result.data
        }
    }

    fun createRoute(
        title: String,
        cityId: Int,
        mood: String,
        description: String?,
        durationHours: Int,
        imageFile: File? = null
    ) {
        viewModelScope.launch {
            // Если есть файл, загружаем его
            val imagePath = imageFile?.let { uploadImage(it) }

            val result = createRouteUseCase(
                title = title,
                cityId = cityId,
                mood = mood,
                description = description,
                durationHours = durationHours,
                image = imagePath
            )
            if (result is Result.Success) loadRoutes()
        }
    }

    fun updateRoute(
        routeId: Int,
        title: String?,
        description: String?,
        mood: String?,
        cityId: Int?,
        durationHours: Int?,
        imageFile: File? = null
    ) {
        viewModelScope.launch {
            val imagePath = imageFile?.let { uploadImage(it) }

            val result = updateRouteUseCase(
                routeId = routeId,
                title = title,
                description = description,
                mood = mood,
                cityId = cityId,
                durationHours = durationHours,
                image = imagePath
            )
            if (result is Result.Success) loadRoutes()
        }
    }

    fun deleteRoute(routeId: Int) {
        viewModelScope.launch {
            val result = deleteRouteUseCase(routeId)
            if (result is Result.Success) loadRoutes()
        }
    }

    // Загрузка изображения на сервер
    private suspend fun uploadImage(file: File): String? {
        return try {
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            // Здесь нужно вызвать API загрузки файла
            // Пока возвращаем путь
            "/static/routes/${file.name}"
        } catch (e: Exception) {
            null
        }
    }
}