package com.sologo.app.presentation.screens

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
import androidx.compose.material.icons.filled.LocationCity
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.CityViewModel
import com.sologo.app.presentation.viewmodel.LostViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostReportScreen(
    lostViewModel: LostViewModel,
    cityViewModel: CityViewModel,
    onBack: () -> Unit
) {
    val reportState by lostViewModel.reportState.collectAsStateWithLifecycle()
    val reportsState by lostViewModel.reportsState.collectAsStateWithLifecycle()
    val citiesState by cityViewModel.citiesState.collectAsStateWithLifecycle()
    val cities = (citiesState as? Result.Success)?.data ?: emptyList()

    var selectedCityName by remember { mutableStateOf("") }
    var cityExpanded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        lostViewModel.loadMyReports()
        cityViewModel.loadCities()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сообщить о проблеме") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Экстренная помощь", style = MaterialTheme.typography.titleLarge, color = SoloGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Если вы потерялись или нуждаетесь в помощи, выберите город. Администратор получит уведомление.", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCityName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Выберите город") },
                            placeholder = { Text("Город") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false }
                        ) {
                            if (cities.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Загрузка городов...") },
                                    onClick = { }
                                )
                            } else {
                                cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text("${city.name}, ${city.country}") },
                                        onClick = {
                                            selectedCityName = city.name
                                            cityExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = message, onValueChange = { message = it },
                        label = { Text("Дополнительная информация (необязательно)") },
                        modifier = Modifier.fillMaxWidth(), minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedCityName.isNotBlank()) {
                                lostViewModel.reportLost(0.0, 0.0, message.takeIf { it.isNotBlank() })
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedCityName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCityName.isNotBlank()) SoloGreen else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        when (reportState) {
                            is Result.Loading -> CircularProgressIndicator(color = SoloGreen)
                            else -> Text("Отправить сообщение")
                        }
                    }

                    if (reportState is Result.Success) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Сообщение отправлено!", color = SoloGreen, style = MaterialTheme.typography.bodySmall)
                    }

                    if (reportState is Result.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text((reportState as Result.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text("Мои сообщения", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))

            when (reportsState) {
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SoloGreen)
                    }
                }
                is Result.Success -> {
                    val reports = (reportsState as Result.Success).data
                    if (reports.isEmpty()) {
                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Text("Нет отправленных сообщений", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        reports.forEach { report ->
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(report.status.name, style = MaterialTheme.typography.labelLarge,
                                        color = when (report.status.name) {
                                            "PENDING" -> MaterialTheme.colorScheme.primary
                                            "ACCEPTED" -> SoloGreen
                                            "COMPLETED" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.error
                                        })
                                    Text("Координаты: ${report.lat}, ${report.lng}", style = MaterialTheme.typography.bodySmall)
                                    if (!report.message.isNullOrBlank()) Text(report.message!!, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}