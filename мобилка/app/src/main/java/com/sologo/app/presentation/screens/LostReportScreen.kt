// presentation/screens/LostReportScreen.kt
package com.sologo.app.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.SoloOffWhite
import com.sologo.app.presentation.theme.SoloWhite
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.LostViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LostReportScreen(
    lostViewModel: LostViewModel,
    onBack: () -> Unit
) {
    val reportState by lostViewModel.reportState.collectAsStateWithLifecycle()
    val reportsState by lostViewModel.reportsState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var message by remember { mutableStateOf("") }
    var isGettingLocation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lostViewModel.loadMyReports()
    }

    fun getLocation() {
        if (locationPermission.status.isGranted) {
            // Получение геолокации - здесь нужно реализовать через FusedLocationProviderClient
            // Для примера используем моковые координаты
            latitude = 55.751244
            longitude = 37.618423
            isGettingLocation = false
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сообщить о проблеме") },
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
                        text = "Экстренная помощь",
                        style = MaterialTheme.typography.titleLarge,
                        color = SoloGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Если вы потерялись или нуждаетесь в помощи, отправьте свои координаты. Администратор получит уведомление.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { getLocation() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Определить местоположение")
                    }

                    if (isGettingLocation) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(color = SoloGreen)
                    }

                    if (latitude != 0.0 && longitude != 0.0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Координаты: $latitude, $longitude",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoloGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Дополнительная информация (необязательно)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (latitude != 0.0 && longitude != 0.0) {
                                lostViewModel.reportLost(latitude, longitude, message.takeIf { it.isNotBlank() })
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = latitude != 0.0 && longitude != 0.0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (latitude != 0.0 && longitude != 0.0) SoloGreen else MaterialTheme.colorScheme.surfaceVariant
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
                        Text(
                            text = "Сообщение отправлено!",
                            color = SoloGreen,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (reportState is Result.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (reportState as Result.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Мои сообщения
            Text(
                text = "Мои сообщения",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            when (reportsState) {
                is Result.Loading -> {
                    CircularProgressIndicator(color = SoloGreen)
                }
                is Result.Success -> {
                    val reports = (reportsState as Result.Success).data
                    if (reports.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SoloWhite)
                        ) {
                            Text(
                                text = "Нет отправленных сообщений",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        reports.forEach { report ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SoloWhite),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = report.status.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = when (report.status.name) {
                                            "PENDING" -> MaterialTheme.colorScheme.primary
                                            "ACCEPTED" -> SoloGreen
                                            "COMPLETED" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    Text(
                                        text = "Координаты: ${report.lat}, ${report.lng}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (!report.message.isNullOrBlank()) {
                                        Text(
                                            text = report.message,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
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