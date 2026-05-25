package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.CityDetailViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    cityId: Int,
    cityName: String,
    viewModel: CityDetailViewModel,
    onBack: () -> Unit,
    onHotelClick: (Int) -> Unit,
    onRouteClick: (Int) -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val hotelsState by viewModel.hotelsState.collectAsStateWithLifecycle()
    val routesState by viewModel.routesState.collectAsStateWithLifecycle()
    val safeZonesState by viewModel.safeZonesState.collectAsStateWithLifecycle()

    LaunchedEffect(cityId) {
        viewModel.loadCityData(cityId, cityName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, null, tint = SoloGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Погода", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SoloGreen)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        when (weatherState) {
                            is Result.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = SoloGreen, modifier = Modifier.size(32.dp))
                                }
                            }
                            is Result.Success -> {
                                val weather = (weatherState as Result.Success).data
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${weather.temperature}°C", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = SoloGreen)
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(weather.condition, style = MaterialTheme.typography.titleMedium)
                                        Text("Влажность: ${weather.humidity}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Ветер: ${weather.windSpeed} м/с", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            is Result.Error -> {
                                Text("Ошибка загрузки погоды", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            else -> {}
                        }
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hotel, null, tint = SoloGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Отели", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SoloGreen)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        when (hotelsState) {
                            is Result.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = SoloGreen, modifier = Modifier.size(32.dp))
                                }
                            }
                            is Result.Success -> {
                                val hotels = (hotelsState as Result.Success).data
                                if (hotels.isEmpty()) {
                                    Text("Нет отелей в этом городе", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        items(hotels, key = { it.id }) { hotel ->
                                            CityHotelCard(hotel = hotel, onClick = { onHotelClick(hotel.id) })
                                        }
                                    }
                                }
                            }
                            is Result.Error -> {
                                Text("Ошибка загрузки отелей", color = MaterialTheme.colorScheme.error)
                            }
                            else -> {}
                        }
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Route, null, tint = SoloGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Маршруты", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SoloGreen)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        when (routesState) {
                            is Result.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = SoloGreen, modifier = Modifier.size(32.dp))
                                }
                            }
                            is Result.Success -> {
                                val routes = (routesState as Result.Success).data
                                if (routes.isEmpty()) {
                                    Text("Нет маршрутов в этом городе", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        items(routes, key = { it.id }) { route ->
                                            CityRouteCard(route = route, onClick = { onRouteClick(route.id) })
                                        }
                                    }
                                }
                            }
                            is Result.Error -> {
                                Text("Ошибка загрузки маршрутов", color = MaterialTheme.colorScheme.error)
                            }
                            else -> {}
                        }
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = SoloGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Безопасные зоны", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SoloGreen)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        when (safeZonesState) {
                            is Result.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = SoloGreen, modifier = Modifier.size(32.dp))
                                }
                            }
                            is Result.Success -> {
                                val zones = (safeZonesState as Result.Success).data
                                if (zones.isEmpty()) {
                                    Text("Нет отмеченных безопасных зон", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    zones.forEachIndexed { index, zone ->
                                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                            Text(zone.district, style = MaterialTheme.typography.titleMedium, color = SoloGreen)
                                            Text("Уровень безопасности: ${zone.level.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            if (!zone.note.isNullOrBlank()) {
                                                Text(zone.note, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                                            }
                                        }
                                        if (index != zones.lastIndex) Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                            is Result.Error -> {
                                Text("Ошибка загрузки безопасных зон", color = MaterialTheme.colorScheme.error)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityHotelCard(hotel: com.sologo.app.domain.model.Hotel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SoloGreen.copy(alpha = 0.1f)),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(hotel.name, style = MaterialTheme.typography.titleSmall, color = SoloGreen, maxLines = 2)
            Text("${hotel.pricePerNight} ₽/ночь", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("★ ${hotel.rating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CityRouteCard(route: com.sologo.app.domain.model.Route, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SoloGreen.copy(alpha = 0.1f)),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(route.title, style = MaterialTheme.typography.titleSmall, color = SoloGreen, maxLines = 2)
            Text("⏱ ${route.durationHours} ч", style = MaterialTheme.typography.bodySmall)
            Text(route.mood.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}