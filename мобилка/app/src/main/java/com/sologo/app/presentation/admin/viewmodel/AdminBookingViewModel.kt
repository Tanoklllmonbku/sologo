// presentation/viewmodel/admin/AdminBookingViewModel.kt
package com.sologo.app.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.usecase.booking.admin.AdminUpdateBookingStatusUseCase
import com.sologo.app.domain.usecase.booking.admin.GetAllBookingsUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminBookingViewModel(
    private val getAllBookingsUseCase: GetAllBookingsUseCase,
    private val updateBookingStatusUseCase: AdminUpdateBookingStatusUseCase
) : ViewModel() {

    private val _bookingsState = MutableStateFlow<Result<List<Booking>>>(Result.Idle)
    val bookingsState: StateFlow<Result<List<Booking>>> = _bookingsState.asStateFlow()

    fun loadBookings() {
        viewModelScope.launch {
            _bookingsState.value = Result.Loading
            val result = getAllBookingsUseCase()
            _bookingsState.value = result
        }
    }

    fun updateStatus(trackingNumber: String, status: String) {
        viewModelScope.launch {
            val result = updateBookingStatusUseCase(trackingNumber, status)
            if (result is Result.Success) {
                loadBookings()
            }
        }
    }
}