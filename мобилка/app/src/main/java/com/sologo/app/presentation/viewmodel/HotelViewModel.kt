// presentation/viewmodel/HotelViewModel.kt
package com.sologo.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.Hotel
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.usecase.hotel.GetHotelByIdUseCase
import com.sologo.app.domain.usecase.hotel.GetHotelsUseCase
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelViewModel(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val getHotelByIdUseCase: GetHotelByIdUseCase
) : ViewModel() {

    private val _hotelsState = MutableStateFlow<Result<List<Hotel>>>(Result.Idle)
    val hotelsState: StateFlow<Result<List<Hotel>>> = _hotelsState.asStateFlow()

    private val _hotelDetailState = MutableStateFlow<Result<HotelDetail>>(Result.Idle)
    val hotelDetailState: StateFlow<Result<HotelDetail>> = _hotelDetailState.asStateFlow()

    fun loadHotels(cityId: Int? = null, affordable: Boolean = false) {
        viewModelScope.launch {
            _hotelsState.value = Result.Loading
            val result = getHotelsUseCase(cityId, affordable)
            _hotelsState.value = result
        }
    }

    fun loadHotelDetail(hotelId: Int) {
        viewModelScope.launch {
            _hotelDetailState.value = Result.Loading
            val result = getHotelByIdUseCase(hotelId)
            _hotelDetailState.value = result
        }
    }
}