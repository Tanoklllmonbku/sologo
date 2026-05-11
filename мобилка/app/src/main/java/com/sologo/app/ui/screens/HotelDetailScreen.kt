package com.sologo.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.SoloGoViewModel
import com.sologo.app.data.SampleData
import com.sologo.app.ui.theme.soloGoTopAppBarColors
import coil.compose.AsyncImage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val bookingDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun formatDateFromMillis(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(bookingDateFormatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotelId: String,
    viewModel: SoloGoViewModel,
    onBack: () -> Unit,
) {
    val hotel = SampleData.hotelById(hotelId)
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hotel?.name ?: "Отель") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = soloGoTopAppBarColors(),
            )
        },
    ) { padding ->
        if (hotel == null) {
            Text(
                "Отель не найден",
                modifier = Modifier.padding(padding).padding(16.dp),
            )
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            val existingBooking = bookings.firstOrNull { it.hotelName == hotel.name }
            var checkInDate by remember(existingBooking?.checkInDate) {
                mutableStateOf(existingBooking?.checkInDate ?: "")
            }
            var checkOutDate by remember(existingBooking?.checkOutDate) {
                mutableStateOf(existingBooking?.checkOutDate ?: "")
            }
            var showCheckInDatePicker by remember { mutableStateOf(false) }
            var showCheckOutDatePicker by remember { mutableStateOf(false) }
            val galleryPhotos = remember(hotel.id) {
                listOf(hotel.mainImageRes) + hotel.roomImageRes
            }
            var selectedPhoto by remember(hotel.id) { mutableStateOf(hotel.mainImageRes) }

            if (showCheckInDatePicker) {
                val checkInState = androidx.compose.material3.rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showCheckInDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                checkInState.selectedDateMillis?.let { checkInDate = formatDateFromMillis(it) }
                                showCheckInDatePicker = false
                            },
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCheckInDatePicker = false }) { Text("Отмена") }
                    },
                ) {
                    DatePicker(state = checkInState)
                }
            }

            if (showCheckOutDatePicker) {
                val checkOutState = androidx.compose.material3.rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showCheckOutDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                checkOutState.selectedDateMillis?.let { checkOutDate = formatDateFromMillis(it) }
                                showCheckOutDatePicker = false
                            },
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCheckOutDatePicker = false }) { Text("Отмена") }
                    },
                ) {
                    DatePicker(state = checkOutState)
                }
            }

            AsyncImage(
                model = selectedPhoto,
                contentDescription = "Фото отеля ${hotel.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            if (galleryPhotos.size > 1) {
                Text(
                    text = "Фото номеров",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )
                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                ) {
                    items(galleryPhotos, key = { it }) { photoRes ->
                        AsyncImage(
                            model = photoRes,
                            contentDescription = "Фото номера",
                            modifier = Modifier
                                .height(74.dp)
                                .width(118.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedPhoto = photoRes },
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
            Text(hotel.city, style = MaterialTheme.typography.titleMedium)
            Text(
                "${hotel.pricePerNight} ₽ за ночь · средняя по городу ${hotel.avgCityPrice} ₽",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                hotel.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
            )
            OutlinedTextField(
                value = checkInDate,
                onValueChange = {},
                label = { Text("Дата заезда (дд.мм.гггг)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .clickable { showCheckInDatePicker = true },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showCheckInDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату заезда")
                    }
                },
                singleLine = true,
            )
            OutlinedTextField(
                value = checkOutDate,
                onValueChange = {},
                label = { Text("Дата выезда (дд.мм.гггг)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clickable { showCheckOutDatePicker = true },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showCheckOutDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату выезда")
                    }
                },
                singleLine = true,
            )

            val hasDates = checkInDate.isNotBlank() && checkOutDate.isNotBlank()
            Button(
                onClick = {
                    viewModel.bookHotel(
                        title = hotel.name,
                        checkInDate = checkInDate.trim(),
                        checkOutDate = checkOutDate.trim(),
                    )
                },
                enabled = hasDates,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Text(if (existingBooking != null) "Обновить бронирование" else "Добавить в бронирования")
            }
            if (existingBooking != null) {
                Text(
                    "Текущее бронирование: ${existingBooking.checkInDate} - ${existingBooking.checkOutDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else if (!hasDates) {
                Text(
                    "Укажите даты заезда и выезда, чтобы сохранить бронирование.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
