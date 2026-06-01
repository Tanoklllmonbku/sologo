package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.components.HotelFiltersSheet
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.CityViewModel
import com.sologo.app.presentation.viewmodel.HotelViewModel
import com.sologo.app.utils.ImageUrlHelper
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    hotelViewModel: HotelViewModel,
    cityViewModel: CityViewModel,
    onBack: () -> Unit,
    onHotelClick: (Int) -> Unit
) {
    val filteredHotelsState by hotelViewModel.filteredHotels.collectAsStateWithLifecycle()
    val filters by hotelViewModel.filters.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отели") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.FilterList, null,
                            tint = if (filters.cityName != null || filters.minPrice != null || filters.minRating != null)
                                SoloGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                label = { Text("Поиск отеля") }, placeholder = { Text("Введите название отеля...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = filters.onlyAffordable,
                    onClick = { hotelViewModel.updateFilters(filters.copy(onlyAffordable = !filters.onlyAffordable)) },
                    label = { Text("Только доступные") }, modifier = Modifier.padding(bottom = 12.dp)
                )
                if (filters.cityName != null || filters.minPrice != null || filters.minRating != null || searchQuery.isNotBlank()) {
                    TextButton(onClick = { hotelViewModel.clearFilters(); searchQuery = "" }) {
                        Text("Сбросить все", color = SoloGreen)
                    }
                }
            }
            when (filteredHotelsState) {
                is Result.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SoloGreen)
                }
                is Result.Success -> {
                    val allHotels = (filteredHotelsState as Result.Success).data
                    val hotels = if (searchQuery.isNotBlank()) allHotels.filter { it.name.contains(searchQuery, ignoreCase = true) || it.cityName.contains(searchQuery, ignoreCase = true) } else allHotels
                    if (hotels.isEmpty()) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(if (searchQuery.isNotBlank()) "Не найдено отелей по запросу \"$searchQuery\"" else "Нет отелей, соответствующих фильтрам", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = { hotelViewModel.clearFilters(); searchQuery = "" }) { Text("Сбросить фильтры") }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
                            items(hotels, key = { it.id }) { hotel ->
                                HotelCard(hotel = hotel, onClick = { onHotelClick(hotel.id) })
                            }
                        }
                    }
                }
                is Result.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((filteredHotelsState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { hotelViewModel.loadHotels() }, modifier = Modifier.padding(top = 16.dp)) { Text("Повторить") }
                    }
                }
                else -> {}
            }
        }
    }
    if (showFilters) {
        HotelFiltersSheet(
            cityViewModel = cityViewModel, currentFilters = filters,
            onApply = { newFilters -> hotelViewModel.updateFilters(newFilters); showFilters = false },
            onDismiss = { showFilters = false }
        )
    }
}

@Composable
private fun HotelCard(hotel: com.sologo.app.domain.model.Hotel, onClick: () -> Unit) {
    Card(
        onClick = onClick, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (hotel.mainImage != null) {
                AsyncImage(
                    model = ImageUrlHelper.toFullImageUrl(hotel.mainImage),
                    contentDescription = hotel.name,
                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Text(hotel.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = if (hotel.mainImage != null) 12.dp else 0.dp))
            Text("${hotel.cityName} · ${hotel.pricePerNight} ₽/ночь", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            Text("Вместимость: до ${hotel.capacity} гостей · Рейтинг: ★ ${hotel.rating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Средняя цена по городу: ${hotel.avgCityPrice} ₽", style = MaterialTheme.typography.labelMedium, color = SoloGreen, modifier = Modifier.padding(top = 4.dp))
        }
    }
}