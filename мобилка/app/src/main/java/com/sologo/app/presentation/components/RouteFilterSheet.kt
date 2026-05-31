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
import com.sologo.app.presentation.viewmodel.RouteFilters  // ← ДОБАВИТЬ ЭТОТ ИМПОРТ
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFiltersSheet(
    cityViewModel: CityViewModel,
    currentFilters: RouteFilters,
    onApply: (RouteFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCityName by remember { mutableStateOf(currentFilters.cityName ?: "") }
    var selectedMood by remember { mutableStateOf(currentFilters.mood ?: "") }
    var minDuration by remember { mutableStateOf(currentFilters.minDuration?.toString() ?: "") }
    var maxDuration by remember { mutableStateOf(currentFilters.maxDuration?.toString() ?: "") }

    var cityExpanded by remember { mutableStateOf(false) }
    var moodExpanded by remember { mutableStateOf(false) }

    val citiesState by cityViewModel.citiesState.collectAsStateWithLifecycle()
    val cities = (citiesState as? Result.Success)?.data ?: emptyList()

    val moods = listOf("calm", "active", "cultural")

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
                text = "Фильтры маршрутов",
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

            // Выпадающий список настроения
            ExposedDropdownMenuBox(
                expanded = moodExpanded,
                onExpandedChange = { moodExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedMood,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Настроение") },
                    placeholder = { Text("Выберите настроение") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = moodExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = moodExpanded,
                    onDismissRequest = { moodExpanded = false }
                ) {
                    moods.forEach { mood ->
                        DropdownMenuItem(
                            text = { Text(mood.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedMood = mood
                                moodExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = minDuration,
                onValueChange = { minDuration = it },
                label = { Text("Длительность от (часов)") },
                placeholder = { Text("1") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxDuration,
                onValueChange = { maxDuration = it },
                label = { Text("Длительность до (часов)") },
                placeholder = { Text("12") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onApply(
                        RouteFilters(
                            cityName = selectedCityName.takeIf { it.isNotBlank() },
                            mood = selectedMood.takeIf { it.isNotBlank() },
                            minDuration = minDuration.toIntOrNull(),
                            maxDuration = maxDuration.toIntOrNull()
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
                    selectedMood = ""
                    minDuration = ""
                    maxDuration = ""
                    onApply(RouteFilters())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сбросить все фильтры")
            }
        }
    }
}