// presentation/screens/HotelListScreen.kt
package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.SoloOffWhite
import com.sologo.app.presentation.theme.SoloWhite
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.HotelViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    hotelViewModel: HotelViewModel,
    onBack: () -> Unit,
    onHotelClick: (Int) -> Unit
) {
    val hotelsState by hotelViewModel.hotelsState.collectAsStateWithLifecycle()
    var onlyAffordable by remember { mutableStateOf(false) }

    LaunchedEffect(onlyAffordable) {
        hotelViewModel.loadHotels(affordable = onlyAffordable)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отели") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = SoloOffWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Фильтр
            Text(
                text = "Фильтр: показать варианты не дороже средней цены по городу",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            FilterChip(
                selected = onlyAffordable,
                onClick = { onlyAffordable = !onlyAffordable },
                label = { Text("Только ≤ средней по городу") },
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (hotelsState) {
                is Result.Loading -> {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SoloGreen)
                    }
                }

                is Result.Success -> {
                    val hotels = (hotelsState as Result.Success).data

                    if (hotels.isEmpty()) {
                        Text(
                            text = "Нет отелей, соответствующих фильтру",
                            modifier = Modifier.padding(top = 32.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            items(hotels, key = { it.id }) { hotel ->
                                HotelCard(
                                    hotel = hotel,
                                    onClick = { onHotelClick(hotel.id) }
                                )
                            }
                        }
                    }
                }

                is Result.Error -> {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ошибка загрузки отелей",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun HotelCard(
    hotel: com.sologo.app.domain.model.Hotel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoloWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Изображение (если есть)
            if (hotel.mainImage != null) {
                AsyncImage(
                    model = hotel.mainImage,
                    contentDescription = hotel.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = hotel.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = if (hotel.mainImage != null) 12.dp else 0.dp)
            )

            Text(
                text = "${hotel.cityName} · ${hotel.pricePerNight} ₽/ночь",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Средняя цена по городу: ${hotel.avgCityPrice} ₽",
                style = MaterialTheme.typography.labelMedium,
                color = SoloGreen,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}