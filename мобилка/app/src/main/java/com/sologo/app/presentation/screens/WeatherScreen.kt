package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.CityViewModel
import com.sologo.app.presentation.viewmodel.WeatherViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel, cityViewModel: CityViewModel, onBack: () -> Unit) {
    var selectedCity by remember { mutableStateOf("") }
    var cityExpanded by remember { mutableStateOf(false) }
    var searchedCity by remember { mutableStateOf("") }
    val weatherState by weatherViewModel.weatherState.collectAsStateWithLifecycle()
    val citiesState by cityViewModel.citiesState.collectAsStateWithLifecycle()
    val cities = (citiesState as? Result.Success)?.data ?: emptyList()

    LaunchedEffect(Unit) { cityViewModel.loadCities() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Погода") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }, colors = soloGoTopAppBarColors())
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Выберите город", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SoloGreen)
                    ExposedDropdownMenuBox(expanded = cityExpanded, onExpandedChange = { cityExpanded = it }) {
                        OutlinedTextField(value = selectedCity, onValueChange = {}, readOnly = true, label = { Text("Город") }, placeholder = { Text("Выберите город из списка") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                        ExposedDropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                            if (cities.isEmpty()) DropdownMenuItem(text = { Text("Загрузка городов...") }, onClick = {})
                            else cities.forEach { city -> DropdownMenuItem(text = { Text("${city.name}, ${city.country}") }, onClick = { selectedCity = city.name; cityExpanded = false }) }
                        }
                    }
                    Button(onClick = { if (selectedCity.isNotBlank()) { searchedCity = selectedCity; weatherViewModel.loadWeather(selectedCity) } }, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SoloGreen), shape = RoundedCornerShape(12.dp), enabled = selectedCity.isNotBlank()) {
                        Icon(Icons.Default.Search, null); Spacer(modifier = Modifier.width(8.dp)); Text("Узнать погоду")
                    }
                }
            }
            if (searchedCity.isNotBlank()) {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                    when (weatherState) {
                        is Result.Loading -> Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = SoloGreen) }
                        is Result.Success -> {
                            val weather = (weatherState as Result.Success).data
                            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(weather.city, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SoloGreen)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("${weather.temperature.toInt()}°C", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    Text(getWeatherEmoji(weather.condition), style = MaterialTheme.typography.displayMedium)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(weather.condition, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    WeatherDetailItem("Влажность", "${weather.humidity}%")
                                    WeatherDetailItem("Ветер", "${weather.windSpeed} м/с")
                                }
                            }
                        }
                        is Result.Error -> Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.WbSunny, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Город не найден", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                            Text((weatherState as Result.Error).message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { weatherViewModel.loadWeather(searchedCity) }, colors = ButtonDefaults.buttonColors(containerColor = SoloGreen), shape = RoundedCornerShape(12.dp)) { Text("Повторить") }
                        }
                        else -> {}
                    }
                }
            } else {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WbSunny, null, modifier = Modifier.size(80.dp), tint = SoloGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Поиск погоды", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SoloGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Выберите город из списка,\nчтобы узнать текущую погоду", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SoloGreen)
    }
}

private fun getWeatherEmoji(condition: String): String = when {
    condition.contains("ясно", ignoreCase = true) -> "☀️"
    condition.contains("облачно", ignoreCase = true) -> "☁️"
    condition.contains("пасмурно", ignoreCase = true) -> "☁️"
    condition.contains("дождь", ignoreCase = true) -> "🌧️"
    condition.contains("ливень", ignoreCase = true) -> "🌧️"
    condition.contains("снег", ignoreCase = true) -> "❄️"
    condition.contains("гроза", ignoreCase = true) -> "⛈️"
    condition.contains("туман", ignoreCase = true) -> "🌫️"
    else -> "🌡️"
}