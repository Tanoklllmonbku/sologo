package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.BookingViewModel
import com.sologo.app.presentation.viewmodel.HotelViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotelId: Int,
    hotelViewModel: HotelViewModel,
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit,
    onCreateBooking: (Int) -> Unit
) {
    val hotelDetailState by hotelViewModel.hotelDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(hotelId) {
        hotelViewModel.loadHotelDetail(hotelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отель") },
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
        when (hotelDetailState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SoloGreen)
                }
            }
            is Result.Success -> {
                val hotel = (hotelDetailState as Result.Success).data
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
                ) {
                    AsyncImage(
                        model = hotel.mainImage ?: hotel.roomImages,
                        contentDescription = hotel.name,
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(hotel.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
                    Text("${hotel.cityName} · ${hotel.address}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${hotel.pricePerNight} ₽ за ночь · средняя по городу ${hotel.avgCityPrice} ₽", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 12.dp))
                    Text("Вместимость: до ${hotel.capacity} гостей", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                    if (hotel.rating > 0) Text("Рейтинг: ★ ${hotel.rating}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                    if (!hotel.description.isNullOrBlank()) Text(hotel.description!!, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
                    Button(
                        onClick = { onCreateBooking(hotel.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Забронировать", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            is Result.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((hotelDetailState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { hotelViewModel.loadHotelDetail(hotelId) }, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Повторить")
                        }
                    }
                }
            }
            else -> {}
        }
    }
}