// presentation/viewmodel/LostViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.LostReport
import com.sologo.app.domain.usecase.lost.GetMyLostReportsUseCase
import com.sologo.app.domain.usecase.lost.ReportLostUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LostViewModel(
    private val reportLostUseCase: ReportLostUseCase,
    private val getMyLostReportsUseCase: GetMyLostReportsUseCase
) : ViewModel() {

    private val _reportState = MutableStateFlow<Result<LostReport>>(Result.Idle)
    val reportState: StateFlow<Result<LostReport>> = _reportState.asStateFlow()

    private val _reportsState = MutableStateFlow<Result<List<LostReport>>>(Result.Idle)
    val reportsState: StateFlow<Result<List<LostReport>>> = _reportsState.asStateFlow()

    fun reportLost(lat: Double, lng: Double, message: String?) {
        viewModelScope.launch {
            _reportState.value = Result.Loading
            val result = reportLostUseCase(lat, lng, message)
            _reportState.value = result
            if (result is Result.Success) {
                loadMyReports() // Обновляем список
            }
        }
    }

    fun loadMyReports() {
        viewModelScope.launch {
            _reportsState.value = Result.Loading
            val result = getMyLostReportsUseCase()
            _reportsState.value = result
        }
    }
}