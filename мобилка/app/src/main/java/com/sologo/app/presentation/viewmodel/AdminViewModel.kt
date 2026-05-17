// presentation/viewmodel/AdminViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.model.User
import com.sologo.app.domain.usecase.booking.admin.AdminUpdateBookingStatusUseCase
import com.sologo.app.domain.usecase.booking.admin.GetAllBookingsUseCase
import com.sologo.app.domain.usecase.hotel.admin.CreateHotelUseCase
import com.sologo.app.domain.usecase.hotel.admin.DeleteHotelUseCase
import com.sologo.app.domain.usecase.hotel.admin.UpdateHotelUseCase
import com.sologo.app.domain.usecase.user.admin.AdminUpdateUserUseCase
import com.sologo.app.domain.usecase.user.admin.GetAllUsersUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val adminUpdateUserUseCase: AdminUpdateUserUseCase,
    private val getAllBookingsUseCase: GetAllBookingsUseCase,
    private val adminUpdateBookingStatusUseCase: AdminUpdateBookingStatusUseCase,
    private val createHotelUseCase: CreateHotelUseCase,
    private val updateHotelUseCase: UpdateHotelUseCase,
    private val deleteHotelUseCase: DeleteHotelUseCase
) : ViewModel() {

    // Users
    private val _usersState = MutableStateFlow<Result<List<User>>>(Result.Idle)
    val usersState: StateFlow<Result<List<User>>> = _usersState.asStateFlow()

    // Bookings
    private val _allBookingsState = MutableStateFlow<Result<List<Booking>>>(Result.Idle)
    val allBookingsState: StateFlow<Result<List<Booking>>> = _allBookingsState.asStateFlow()

    // Hotels
    private val _createHotelState = MutableStateFlow<Result<HotelDetail>>(Result.Idle)
    val createHotelState: StateFlow<Result<HotelDetail>> = _createHotelState.asStateFlow()

    private val _updateHotelState = MutableStateFlow<Result<HotelDetail>>(Result.Idle)
    val updateHotelState: StateFlow<Result<HotelDetail>> = _updateHotelState.asStateFlow()

    private val _deleteHotelState = MutableStateFlow<Result<Unit>>(Result.Idle)
    val deleteHotelState: StateFlow<Result<Unit>> = _deleteHotelState.asStateFlow()

    // Users
    fun loadAllUsers() {
        viewModelScope.launch {
            _usersState.value = Result.Loading
            val result = getAllUsersUseCase()
            _usersState.value = result
        }
    }

    fun updateUser(userId: Int, nickname: String?, email: String?, phoneNumber: String?) {
        viewModelScope.launch {
            val result = adminUpdateUserUseCase(userId, nickname, email, phoneNumber)
            if (result is Result.Success) {
                loadAllUsers() // Обновляем список
            }
        }
    }

    // Bookings
    fun loadAllBookings() {
        viewModelScope.launch {
            _allBookingsState.value = Result.Loading
            val result = getAllBookingsUseCase()
            _allBookingsState.value = result
        }
    }

    fun updateBookingStatus(trackingNumber: String, status: String) {
        viewModelScope.launch {
            val result = adminUpdateBookingStatusUseCase(trackingNumber, status)
            if (result is Result.Success) {
                loadAllBookings() // Обновляем список
            }
        }
    }

    // Hotels
    fun createHotel(
        name: String,
        cityId: Int,
        address: String,
        pricePerNight: Int,
        avgCityPrice: Int,
        description: String? = null,
        rating: Double = 0.0,
        capacity: Int = 10
    ) {
        viewModelScope.launch {
            _createHotelState.value = Result.Loading
            val result = createHotelUseCase(
                name, cityId, address, pricePerNight, avgCityPrice,
                description, rating, capacity, null, null, null
            )
            _createHotelState.value = result
        }
    }

    fun updateHotel(
        hotelId: Int,
        name: String? = null,
        cityId: Int? = null,
        address: String? = null,
        pricePerNight: Int? = null,
        description: String? = null,
        rating: Double? = null,
        capacity: Int? = null,
        status: Int? = null
    ) {
        viewModelScope.launch {
            _updateHotelState.value = Result.Loading
            val result = updateHotelUseCase(
                hotelId, name, cityId, address, pricePerNight,
                description, rating, capacity, status
            )
            _updateHotelState.value = result
        }
    }

    fun deleteHotel(hotelId: Int) {
        viewModelScope.launch {
            _deleteHotelState.value = Result.Loading
            val result = deleteHotelUseCase(hotelId)
            _deleteHotelState.value = result
        }
    }

    fun clearStates() {
        _createHotelState.value = Result.Idle
        _updateHotelState.value = Result.Idle
        _deleteHotelState.value = Result.Idle
    }
}