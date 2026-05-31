package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.domain.model.Booking
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.BookingViewModel
import com.sologo.app.utils.Result
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onHotelClick: (Int) -> Unit = {}
) {
    val bookingsState by bookingViewModel.bookingsState.collectAsStateWithLifecycle()
    val isLoggedIn = bookingViewModel.isLoggedIn
    var cancelingId by remember { mutableStateOf<String?>(null) }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    LaunchedEffect(Unit) { if (isLoggedIn) bookingViewModel.loadMyBookings() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои бронирования") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (!isLoggedIn) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Для просмотра бронирований нужно войти в аккаунт", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onNavigateToLogin, colors = ButtonDefaults.buttonColors(containerColor = SoloGreen), shape = RoundedCornerShape(12.dp)) {
                        Text("Войти", color = Color.White)
                    }
                }
            }
            return@Scaffold
        }

        when (bookingsState) {
            is Result.Loading -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = SoloGreen) }
            is Result.Success -> {
                val bookings = (bookingsState as Result.Success).data
                if (bookings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("У вас пока нет бронирований", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Перейдите в раздел Отели и забронируйте номер", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(bookings, key = { it.trackingNumber }) { booking ->
                            BookingCard(
                                booking = booking, dateFormat = dateFormat,
                                isCanceling = cancelingId == booking.trackingNumber,
                                onCancel = { cancelingId = booking.trackingNumber; bookingViewModel.cancelBooking(booking.trackingNumber) },
                                onClick = { onHotelClick(booking.hotelId) }
                            )
                        }
                    }
                }
            }
            is Result.Error -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((bookingsState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { bookingViewModel.loadMyBookings() }, modifier = Modifier.padding(top = 16.dp)) { Text("Повторить") }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun BookingCard(
booking: Booking, dateFormat: SimpleDateFormat, isCanceling: Boolean,
onCancel: () -> Unit, onClick: () -> Unit
) {
    Card(
        onClick = onClick, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(booking.hotelName, style = MaterialTheme.typography.titleLarge, color = SoloGreen)
            Text(booking.hotelCity, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            Text("${dateFormat.format(booking.checkIn)} - ${dateFormat.format(booking.checkOut)}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            Text("Гостей: ${booking.guestsCount} · ${booking.days} ночей", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Итого: ${booking.totalPrice} ₽", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            Text("Статус: ${getStatusText(booking.status.name)}", style = MaterialTheme.typography.labelMedium, color = getStatusColor(booking.status.name), modifier = Modifier.padding(top = 4.dp))
            if (booking.status.name == "PENDING" && !isCanceling) {
                Button(onClick = onCancel, modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.Cancel, null); Text("Отменить бронирование")
                }
            }
            if (isCanceling) Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SoloGreen)
            }
        }
    }
}

private fun getStatusText(status: String): String = when (status) {
    "PENDING" -> "Ожидает подтверждения"; "CONFIRMED" -> "Подтверждено"
    "CANCELLED" -> "Отменено"; "COMPLETED" -> "Завершено"; else -> status
}

@Composable
private fun getStatusColor(status: String): Color = when (status) {
    "PENDING" -> MaterialTheme.colorScheme.primary; "CONFIRMED" -> SoloGreen
    "CANCELLED" -> MaterialTheme.colorScheme.error; "COMPLETED" -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}