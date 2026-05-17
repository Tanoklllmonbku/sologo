// presentation/viewmodel/SafeZoneViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.usecase.safezone.GetSafeZonesUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SafeZoneViewModel(
    private val getSafeZonesUseCase: GetSafeZonesUseCase
) : ViewModel() {

    private val _safeZonesState = MutableStateFlow<Result<List<SafeZone>>>(Result.Idle)
    val safeZonesState: StateFlow<Result<List<SafeZone>>> = _safeZonesState.asStateFlow()

    fun loadSafeZones(cityId: Int? = null) {
        viewModelScope.launch {
            _safeZonesState.value = Result.Loading
            val result = getSafeZonesUseCase(cityId)
            _safeZonesState.value = result
        }
    }
}