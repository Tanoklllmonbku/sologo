package com.sologo.app.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sologo.app.domain.model.City
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.domain.usecase.city.GetCitiesUseCase
import com.sologo.app.domain.usecase.hotel.GetHotelsUseCase
import com.sologo.app.domain.usecase.hotel.admin.*
import com.sologo.app.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class AdminHotelsViewModel(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val createHotelUseCase: CreateHotelUseCase,
    private val updateHotelUseCase: UpdateHotelUseCase,
    private val deleteHotelUseCase: DeleteHotelUseCase,
    private val restoreHotelUseCase: RestoreHotelUseCase
) : ViewModel() {

    // Используем HotelDetail для полной информации
    private val _hotelsState = MutableStateFlow<Result<List<HotelDetail>>>(Result.Idle)
    val hotelsState: StateFlow<Result<List<HotelDetail>>> = _hotelsState.asStateFlow()

    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()

    private val _operationState = MutableStateFlow<Result<Unit>>(Result.Idle)
    val operationState: StateFlow<Result<Unit>> = _operationState.asStateFlow()

    fun loadHotels() {
        viewModelScope.launch {
            _hotelsState.value = Result.Loading
            // Получаем список отелей
            val result = getHotelsUseCase(cityId = null, affordable = false)

            // Конвертируем Hotel в HotelDetail
            val convertedResult = when (result) {
                is Result.Success -> {
                    val hotelDetails = result.data.map { hotel ->
                        HotelDetail(
                            id = hotel.id,
                            name = hotel.name,
                            cityId = 0, // Будет заполнено из отдельного запроса
                            cityName = hotel.cityName,
                            address = "", // Будет заполнено из отдельного запроса
                            description = null,
                            pricePerNight = hotel.pricePerNight,
                            avgCityPrice = hotel.avgCityPrice,
                            rating = hotel.rating,
                            capacity = hotel.capacity,
                            managerPhones = null,
                            mainImage = hotel.mainImage,
                            roomImages = null,
                            status = 1,
                            createdAt = java.util.Date()
                        )
                    }
                    Result.Success(hotelDetails)
                }
                is Result.Error -> Result.Error(result.message)
                is Result.Loading -> Result.Loading
                is Result.Idle -> Result.Idle
            }
            _hotelsState.value = convertedResult
        }
    }

    fun loadCities() {
        viewModelScope.launch {
            val result = getCitiesUseCase()
            if (result is Result.Success) {
                _cities.value = result.data
            }
        }
    }

    fun createHotel(
        name: String,
        cityId: Int,
        address: String,
        pricePerNight: Int,
        avgCityPrice: Int,
        description: String?,
        rating: Double,
        capacity: Int,
        managerPhones: List<String>?,
        mainImageFile: File? = null,
        roomImageFiles: List<File>? = null
    ) {
        viewModelScope.launch {
            _operationState.value = Result.Loading

            // Загружаем фото
            val mainImagePath = mainImageFile?.let { uploadImage(it, "hotels") }
            val roomImagesPaths = roomImageFiles?.mapNotNull { uploadImage(it, "hotels/rooms") }

            val result = createHotelUseCase(
                name = name,
                cityId = cityId,
                address = address,
                pricePerNight = pricePerNight,
                avgCityPrice = avgCityPrice,
                description = description,
                rating = rating,
                capacity = capacity,
                managerPhones = managerPhones,
                mainImage = mainImagePath,
                roomImages = roomImagesPaths
            )
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Idle
            }
            if (result is Result.Success) {
                loadHotels()
            }
        }
    }

    fun updateHotel(
        hotelId: Int,
        name: String?,
        cityId: Int?,
        address: String?,
        pricePerNight: Int?,
        description: String?,
        rating: Double?,
        capacity: Int?,
        mainImageFile: File? = null,
        roomImageFiles: List<File>? = null
    ) {
        viewModelScope.launch {
            _operationState.value = Result.Loading

            // Загружаем новые фото, если есть
            val mainImagePath = mainImageFile?.let { uploadImage(it, "hotels") }
            val roomImagesPaths = roomImageFiles?.mapNotNull { uploadImage(it, "hotels/rooms") }

            val result = updateHotelUseCase(
                hotelId = hotelId,
                name = name,
                cityId = cityId,
                address = address,
                pricePerNight = pricePerNight,
                description = description,
                rating = rating,
                capacity = capacity,
                mainImage = mainImagePath,
                roomImages = roomImagesPaths
            )
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Idle
            }
            if (result is Result.Success) {
                loadHotels()
            }
        }
    }

    fun deleteHotel(hotelId: Int) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            val result = deleteHotelUseCase(hotelId)
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Idle
            }
            if (result is Result.Success) {
                loadHotels()
            }
        }
    }

    fun restoreHotel(hotelId: Int) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            val result = restoreHotelUseCase(hotelId)
            _operationState.value = when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.message)
                else -> Result.Idle
            }
            if (result is Result.Success) {
                loadHotels()
            }
        }
    }

    fun clearOperationState() {
        _operationState.value = Result.Idle
    }

    // Загрузка изображения на сервер
    private suspend fun uploadImage(file: File, subDir: String): String? {
        return try {
            // TODO: Реализовать вызов API для загрузки файла
            // Пока возвращаем путь
            "/static/$subDir/${file.name}"
        } catch (e: Exception) {
            null
        }
    }
}