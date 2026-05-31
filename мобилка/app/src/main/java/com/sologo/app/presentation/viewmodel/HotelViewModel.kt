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

data class HotelFilters(
    val cityName: String? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val minCapacity: Int? = null,
    val maxCapacity: Int? = null,
    val minRating: Double? = null,
    val onlyAffordable: Boolean = false
)

class HotelViewModel(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val getHotelByIdUseCase: GetHotelByIdUseCase
) : ViewModel() {

    // Все отели (оригинал)
    private val _allHotels = MutableStateFlow<List<Hotel>>(emptyList())

    // Отфильтрованные отели
    private val _filteredHotels = MutableStateFlow<Result<List<Hotel>>>(Result.Idle)
    val filteredHotels: StateFlow<Result<List<Hotel>>> = _filteredHotels.asStateFlow()

    // Текущие фильтры
    private val _filters = MutableStateFlow(HotelFilters())
    val filters: StateFlow<HotelFilters> = _filters.asStateFlow()

    // Детали отеля
    private val _hotelDetailState = MutableStateFlow<Result<HotelDetail>>(Result.Idle)
    val hotelDetailState: StateFlow<Result<HotelDetail>> = _hotelDetailState.asStateFlow()

    // Загрузка всех отелей
    fun loadHotels() {
        viewModelScope.launch {
            _filteredHotels.value = Result.Loading
            val result = getHotelsUseCase(cityId = null, affordable = false)
            if (result is Result.Success) {
                _allHotels.value = result.data
                applyFilters()
            } else if (result is Result.Error) {
                _filteredHotels.value = result
            }
        }
    }

    // Применение фильтров
    fun updateFilters(newFilters: HotelFilters) {
        _filters.value = newFilters
        applyFilters()
    }

    // Сброс фильтров
    fun clearFilters() {
        _filters.value = HotelFilters()
        applyFilters()
    }

    // Применить фильтры к _allHotels
    private fun applyFilters() {
        val hotels = _allHotels.value
        val filters = _filters.value

        val filtered = hotels.filter { hotel ->
            var matches = true

            filters.cityName?.let { city ->
                if (!hotel.cityName.contains(city, ignoreCase = true)) {
                    matches = false
                }
            }

            filters.minPrice?.let { min ->
                if (hotel.pricePerNight < min) matches = false
            }

            filters.maxPrice?.let { max ->
                if (hotel.pricePerNight > max) matches = false
            }

            filters.minCapacity?.let { min ->
                if (hotel.capacity < min) matches = false
            }

            filters.maxCapacity?.let { max ->
                if (hotel.capacity > max) matches = false
            }

            filters.minRating?.let { min ->
                if (hotel.rating < min) matches = false
            }

            if (filters.onlyAffordable) {
                if (hotel.pricePerNight > hotel.avgCityPrice) matches = false
            }

            matches
        }

        _filteredHotels.value = Result.Success(filtered)
    }

    // Загрузка деталей отеля
    fun loadHotelDetail(hotelId: Int) {
        viewModelScope.launch {
            _hotelDetailState.value = Result.Loading
            val result = getHotelByIdUseCase(hotelId)
            _hotelDetailState.value = result
        }
    }
}