// presentation/viewmodel/admin/AdminSafeZoneViewModel.kt
package com.sologo.app.presentation.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.City
import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.usecase.city.GetCitiesUseCase
import com.sologo.app.domain.usecase.safezone.GetSafeZonesUseCase
import com.sologo.app.domain.usecase.safezone.admin.*
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminSafeZoneViewModel(
    private val getSafeZonesUseCase: GetSafeZonesUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val createSafeZoneUseCase: CreateSafeZoneUseCase,
    private val updateSafeZoneUseCase: UpdateSafeZoneUseCase,
    private val deleteSafeZoneUseCase: DeleteSafeZoneUseCase
) : ViewModel() {

    private val _zonesState = MutableStateFlow<Result<List<SafeZone>>>(Result.Idle)
    val zonesState: StateFlow<Result<List<SafeZone>>> = _zonesState.asStateFlow()

    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()

    fun loadZones() {
        viewModelScope.launch {
            _zonesState.value = Result.Loading
            val result = getSafeZonesUseCase(cityId = null)
            _zonesState.value = result
        }
    }

    fun loadCities() {
        viewModelScope.launch {
            val result = getCitiesUseCase()
            if (result is Result.Success) _cities.value = result.data
        }
    }

    fun createZone(district: String, cityId: Int, level: String, note: String?) {
        viewModelScope.launch {
            val result = createSafeZoneUseCase(district, cityId, level, note)
            if (result is Result.Success) loadZones()
        }
    }

    fun updateZone(zoneId: Int, district: String?, cityId: Int?, level: String?, note: String?) {
        viewModelScope.launch {
            val result = updateSafeZoneUseCase(zoneId, district, cityId, level, note)
            if (result is Result.Success) loadZones()
        }
    }

    fun deleteZone(zoneId: Int) {
        viewModelScope.launch {
            val result = deleteSafeZoneUseCase(zoneId)
            if (result is Result.Success) loadZones()
        }
    }
}