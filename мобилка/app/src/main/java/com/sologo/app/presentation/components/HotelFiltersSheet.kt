package com.sologo.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.viewmodel.CityViewModel
import com.sologo.app.presentation.viewmodel.HotelFilters
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelFiltersSheet(
    cityViewModel: CityViewModel,
    currentFilters: HotelFilters,
    onApply: (HotelFilters) -> Unit,
    onDismiss: () -> Unit
) {
    // Состояния фильтров
    var selectedCityName by remember { mutableStateOf(currentFilters.cityName ?: "") }
    var selectedCityId by remember { mutableStateOf<Int?>(null) }
    var minPrice by remember { mutableStateOf(currentFilters.minPrice?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(currentFilters.maxPrice?.toString() ?: "") }
    var minCapacity by remember { mutableStateOf(currentFilters.minCapacity?.toString() ?: "") }
    var maxCapacity by remember { mutableStateOf(currentFilters.maxCapacity?.toString() ?: "") }
    var minRating by remember { mutableStateOf(currentFilters.minRating?.toString() ?: "") }

    // Состояния для выпадающего списка городов
    var cityExpanded by remember { mutableStateOf(false) }
    val citiesState by cityViewModel.citiesState.collectAsStateWithLifecycle()
    val cities = (citiesState as? Result.Success)?.data ?: emptyList()

    // Загружаем города при открытии
    LaunchedEffect(Unit) {
        cityViewModel.loadCities()
    }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Фильтры отелей",
                style = MaterialTheme.typography.headlineSmall
            )

            // Выпадающий список городов
            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { cityExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCityName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Город") },
                    placeholder = { Text("Выберите город") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { cityExpanded = false }
                ) {
                    cities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text("${city.name}, ${city.country}") },
                            onClick = {
                                selectedCityName = city.name
                                selectedCityId = city.id
                                cityExpanded = false
                            }
                        )
                    }
                    if (cities.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Загрузка городов...") },
                            onClick = { }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = minPrice,
                onValueChange = { minPrice = it },
                label = { Text("Цена за ночь от") },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                label = { Text("Цена за ночь до") },
                placeholder = { Text("50000") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = minCapacity,
                onValueChange = { minCapacity = it },
                label = { Text("Вместимость от") },
                placeholder = { Text("1") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxCapacity,
                onValueChange = { maxCapacity = it },
                label = { Text("Вместимость до") },
                placeholder = { Text("10") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Минимальный рейтинг: ${minRating.toDoubleOrNull()?.let { String.format("%.1f", it) } ?: "0"} ★",
                style = MaterialTheme.typography.bodySmall
            )
            Slider(
                value = minRating.toDoubleOrNull()?.toFloat() ?: 0f,
                onValueChange = { minRating = it.toDouble().toString() },
                valueRange = 0f..5f,
                steps = 10,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = SoloGreen,
                    activeTrackColor = SoloGreen
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onApply(
                        HotelFilters(
                            cityName = selectedCityName.takeIf { it.isNotBlank() },
                            minPrice = minPrice.toIntOrNull(),
                            maxPrice = maxPrice.toIntOrNull(),
                            minCapacity = minCapacity.toIntOrNull(),
                            maxCapacity = maxCapacity.toIntOrNull(),
                            minRating = minRating.toDoubleOrNull(),
                            onlyAffordable = currentFilters.onlyAffordable
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Применить фильтры")
            }

            TextButton(
                onClick = {
                    selectedCityName = ""
                    selectedCityId = null
                    minPrice = ""
                    maxPrice = ""
                    minCapacity = ""
                    maxCapacity = ""
                    minRating = ""
                    onApply(HotelFilters())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сбросить все фильтры")
            }
        }
    }
}