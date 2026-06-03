// presentation/viewmodel/BookingViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.repository.AuthRepository
import com.sologo.app.domain.usecase.booking.CancelBookingUseCase
import com.sologo.app.domain.usecase.booking.CreateBookingUseCase
import com.sologo.app.domain.usecase.booking.GetMyBookingsUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(
    private val createBookingUseCase: CreateBookingUseCase,
    private val getMyBookingsUseCase: GetMyBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn()

    private val _bookingsState = MutableStateFlow<Result<List<Booking>>>(Result.Idle)
    val bookingsState: StateFlow<Result<List<Booking>>> = _bookingsState.asStateFlow()

    private val _createBookingState = MutableStateFlow<Result<Booking>>(Result.Idle)
    val createBookingState: StateFlow<Result<Booking>> = _createBookingState.asStateFlow()

    private val _cancelState = MutableStateFlow<Result<Booking>>(Result.Idle)
    val cancelState: StateFlow<Result<Booking>> = _cancelState.asStateFlow()

    fun loadMyBookings() {
        viewModelScope.launch {
            _bookingsState.value = Result.Loading
            val result = getMyBookingsUseCase()
            _bookingsState.value = result
        }
    }

    fun createBooking(hotelId: Int, guestsCount: Int, checkIn: String, checkOut: String) {
        viewModelScope.launch {
            _createBookingState.value = Result.Loading
            val result = createBookingUseCase(hotelId, guestsCount, checkIn, checkOut)
            _createBookingState.value = result
            if (result is Result.Success) {
                loadMyBookings() // Обновляем список
            }
        }
    }

    fun clearCreateBookingState() {
        _createBookingState.value = Result.Idle
    }

    fun cancelBooking(trackingNumber: String) {
        viewModelScope.launch {
            _cancelState.value = Result.Loading
            val result = cancelBookingUseCase(trackingNumber)
            _cancelState.value = result
            if (result is Result.Success) {
                loadMyBookings() // Обновляем список
            }
        }
    }
}