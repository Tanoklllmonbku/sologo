package com.sologo.app.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.SoloOffWhite
import com.sologo.app.presentation.theme.SoloWhite
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.BookingViewModel
import com.sologo.app.utils.Result
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun formatDateFromMillis(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormat)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    hotelId: Int,
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val createBookingState by bookingViewModel.createBookingState.collectAsStateWithLifecycle()

    var guestsCount by remember { mutableStateOf(1) }
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    LaunchedEffect(createBookingState) {
        if (createBookingState is Result.Success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бронирование") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoloWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Данные бронирования",
                        style = MaterialTheme.typography.titleLarge,
                        color = SoloGreen
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Количество гостей
                    Text(
                        text = "Количество гостей",
                        style = MaterialTheme.typography.labelMedium
                    )
                    androidx.compose.material3.Slider(
                        value = guestsCount.toFloat(),
                        onValueChange = { guestsCount = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.SliderDefaults.colors(
                            thumbColor = SoloGreen,
                            activeTrackColor = SoloGreen
                        )
                    )
                    Text(
                        text = "$guestsCount человек",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Дата заезда
                    OutlinedTextField(
                        value = checkInDate,
                        onValueChange = {},
                        label = { Text("Дата заезда") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCheckInPicker = true },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showCheckInPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                            }
                        }
                    )

                    // Дата выезда
                    OutlinedTextField(
                        value = checkOutDate,
                        onValueChange = {},
                        label = { Text("Дата выезда") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCheckOutPicker = true },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showCheckOutPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Кнопка бронирования
                    when (createBookingState) {
                        is Result.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = SoloGreen)
                            }
                        }
                        is Result.Error -> {
                            Column {
                                Text(
                                    text = (createBookingState as Result.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        bookingViewModel.createBooking(
                                            hotelId,
                                            guestsCount,
                                            checkInDate,
                                            checkOutDate
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Забронировать")
                                }
                            }
                        }
                        else -> {
                            Button(
                                onClick = {
                                    bookingViewModel.createBooking(
                                        hotelId,
                                        guestsCount,
                                        checkInDate,
                                        checkOutDate
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = checkInDate.isNotBlank() && checkOutDate.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (checkInDate.isNotBlank() && checkOutDate.isNotBlank()) SoloGreen
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Забронировать")
                            }
                        }
                    }
                }
            }
        }
    }

    // DatePicker диалог для даты заезда (с ограничением от сегодня)
    if (showCheckInPicker) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayInMillis = calendar.timeInMillis

        val checkInState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = todayInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showCheckInPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        checkInState.selectedDateMillis?.let { millis ->
                            if (millis >= todayInMillis) {
                                checkInDate = formatDateFromMillis(millis)
                            }
                        }
                        showCheckInPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showCheckInPicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = checkInState)
        }
    }

    // DatePicker диалог для даты выезда (не может быть раньше даты заезда)
    if (showCheckOutPicker) {
        val checkInMillis = if (checkInDate.isNotBlank()) {
            LocalDate.parse(checkInDate, dateFormat)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

        val checkOutState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = checkInMillis + 86400000
        )

        DatePickerDialog(
            onDismissRequest = { showCheckOutPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        checkOutState.selectedDateMillis?.let { millis ->
                            if (millis > checkInMillis) {
                                checkOutDate = formatDateFromMillis(millis)
                            }
                        }
                        showCheckOutPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showCheckOutPicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = checkOutState)
        }
    }
}