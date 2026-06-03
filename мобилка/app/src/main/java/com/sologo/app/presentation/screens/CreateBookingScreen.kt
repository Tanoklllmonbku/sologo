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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.BookingViewModel
import com.sologo.app.utils.Result
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun formatDateFromMillis(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormat)
}

private fun validateBookingDates(checkInStr: String, checkOutStr: String): Pair<Boolean, String> {
    if (checkInStr.isBlank()) return Pair(false, "Введите дату заезда")
    if (checkOutStr.isBlank()) return Pair(false, "Введите дату выезда")

    val checkIn: LocalDate
    val checkOut: LocalDate

    try {
        checkIn = LocalDate.parse(checkInStr.trim(), dateFormat)
    } catch (e: DateTimeParseException) {
        return Pair(false, "Неверный формат даты заезда. Используйте ГГГГ-ММ-ДД")
    }

    try {
        checkOut = LocalDate.parse(checkOutStr.trim(), dateFormat)
    } catch (e: DateTimeParseException) {
        return Pair(false, "Неверный формат даты выезда. Используйте ГГГГ-ММ-ДД")
    }

    val today = LocalDate.now()

    if (checkIn.isBefore(today)) {
        return Pair(false, "Дата заезда не может быть раньше сегодняшнего дня")
    }

    if (!checkOut.isAfter(checkIn)) {
        return Pair(false, "Дата выезда должна быть позже даты заезда")
    }

    return Pair(true, "")
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
    var dateError by remember { mutableStateOf<String?>(null) }

    // Флаг для отслеживания успешного бронирования на этом экране
    var hasSuccess by remember { mutableStateOf(false) }

    // Обработка успеха
    LaunchedEffect(createBookingState) {
        if (createBookingState is Result.Success && !hasSuccess) {
            hasSuccess = true
            bookingViewModel.clearCreateBookingState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Бронирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Данные бронирования", style = MaterialTheme.typography.titleLarge, color = SoloGreen)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Количество гостей", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = guestsCount.toFloat(),
                        onValueChange = { guestsCount = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(thumbColor = SoloGreen, activeTrackColor = SoloGreen)
                    )
                    Text("$guestsCount человек", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = checkInDate, onValueChange = {},
                        label = { Text("Дата заезда") }, placeholder = { Text("ГГГГ-ММ-ДД") },
                        modifier = Modifier.fillMaxWidth().clickable { showCheckInPicker = true }, readOnly = true,
                        isError = dateError != null && dateError!!.contains("заезда"),
                        trailingIcon = { IconButton(onClick = { showCheckInPicker = true }) { Icon(Icons.Default.DateRange, null) } }
                    )

                    OutlinedTextField(
                        value = checkOutDate, onValueChange = {},
                        label = { Text("Дата выезда") }, placeholder = { Text("ГГГГ-ММ-ДД") },
                        modifier = Modifier.fillMaxWidth().clickable { showCheckOutPicker = true }, readOnly = true,
                        isError = dateError != null && (dateError!!.contains("выезда") || dateError!!.contains("позже")),
                        trailingIcon = { IconButton(onClick = { showCheckOutPicker = true }) { Icon(Icons.Default.DateRange, null) } }
                    )

                    if (dateError != null) {
                        Text(dateError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    when {
                        createBookingState is Result.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = SoloGreen)
                            }
                        }
                        createBookingState is Result.Error -> {
                            Column {
                                Text(
                                    text = (createBookingState as Result.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val (isValid, error) = validateBookingDates(checkInDate, checkOutDate)
                                        if (isValid) {
                                            dateError = null
                                            bookingViewModel.createBooking(hotelId, guestsCount, checkInDate, checkOutDate)
                                        } else {
                                            dateError = error
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Повторить")
                                }
                            }
                        }
                        else -> {
                            Button(
                                onClick = {
                                    val (isValid, error) = validateBookingDates(checkInDate, checkOutDate)
                                    if (isValid) {
                                        dateError = null
                                        bookingViewModel.createBooking(hotelId, guestsCount, checkInDate, checkOutDate)
                                    } else {
                                        dateError = error
                                    }
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

    if (showCheckInPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCheckInPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let {
                            checkInDate = formatDateFromMillis(it)
                            dateError = null
                        }
                        showCheckInPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showCheckInPicker = false }) { Text("Отмена") } }
        ) {
            DatePicker(state = state)
        }
    }

    if (showCheckOutPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCheckOutPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let {
                            checkOutDate = formatDateFromMillis(it)
                            dateError = null
                        }
                        showCheckOutPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showCheckOutPicker = false }) { Text("Отмена") } }
        ) {
            DatePicker(state = state)
        }
    }
}