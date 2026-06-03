// presentation/screens/admin/AdminBookingsScreen.kt
package com.sologo.app.presentation.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.domain.model.Booking
import com.sologo.app.domain.model.BookingStatus
import com.sologo.app.presentation.viewmodel.admin.AdminBookingViewModel
import com.sologo.app.utils.Result
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingsScreen(
    viewModel: AdminBookingViewModel,
    onNavigateBack: () -> Unit
) {
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", LocalLocale.current.platformLocale)

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление бронированиями") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadBookings() }) {
                        Icon(Icons.Default.Refresh, "Обновить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (bookingsState) {
                is Result.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Result.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (bookingsState as Result.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadBookings() }) {
                            Text("Повторить")
                        }
                    }
                }
                is Result.Success -> {
                    val bookings = (bookingsState as Result.Success).data
                    if (bookings.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Bookmark, null, modifier = Modifier.size(64.dp))
                            Text("Нет бронирований", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(bookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    onStatusChange = { newStatus ->
                                        viewModel.updateStatus(booking.trackingNumber, newStatus)
                                    },
                                    dateFormat = dateFormat
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onStatusChange: (String) -> Unit,
    dateFormat: SimpleDateFormat
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    // Получаем цвет в зависимости от статуса (enum)
    val statusColor = when (booking.status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.secondary
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
        BookingStatus.COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }

    // Получаем строковое представление статуса
    val statusText = when (booking.status) {
        BookingStatus.PENDING -> "PENDING"
        BookingStatus.CONFIRMED -> "CONFIRMED"
        BookingStatus.COMPLETED -> "COMPLETED"
        BookingStatus.CANCELLED -> "CANCELLED"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = booking.hotelName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = booking.hotelCity,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    Button(
                        onClick = { showStatusMenu = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = statusColor
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("PENDING") },
                            onClick = {
                                onStatusChange("pending")
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("CONFIRMED") },
                            onClick = {
                                onStatusChange("confirmed")
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("COMPLETED") },
                            onClick = {
                                onStatusChange("completed")
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("CANCELLED") },
                            onClick = {
                                onStatusChange("cancelled")
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📅 ${dateFormat.format(booking.checkIn)} - ${dateFormat.format(booking.checkOut)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "👥 ${booking.guestsCount} чел",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "💰 ${booking.totalPrice} ₽",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "№ ${booking.trackingNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}