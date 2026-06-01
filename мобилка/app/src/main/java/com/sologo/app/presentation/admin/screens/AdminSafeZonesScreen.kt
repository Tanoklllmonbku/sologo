// presentation/screens/admin/AdminSafeZonesScreen.kt
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
import com.sologo.app.domain.model.SafeZone
import com.sologo.app.domain.model.SafetyLevel
import com.sologo.app.presentation.admin.viewmodel.AdminSafeZoneViewModel
import com.sologo.app.presentation.screens.admin.components.AdminTopBar
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSafeZonesScreen(
    viewModel: AdminSafeZoneViewModel,
    onNavigateBack: () -> Unit
) {
    val zonesState by viewModel.zonesState.collectAsStateWithLifecycle()
    val cities by viewModel.cities.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingZone by remember { mutableStateOf<SafeZone?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadZones()
        viewModel.loadCities()
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Управление безопасными зонами",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Добавить зону")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (zonesState) {
                is Result.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Result.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text((zonesState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadZones() }) { Text("Повторить") }
                    }
                }
                is Result.Success -> {
                    val zones = (zonesState as Result.Success).data
                    if (zones.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Security, null, modifier = Modifier.size(64.dp))
                            Text("Нет безопасных зон")
                            Button(onClick = { showCreateDialog = true }) { Text("Добавить зону") }
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(zones) { zone ->
                                SafeZoneCard(
                                    zone = zone,
                                    onEdit = { editingZone = zone },
                                    onDelete = { viewModel.deleteZone(zone.id) }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    if (showCreateDialog) {
        SafeZoneFormDialog(
            title = "Добавить безопасную зону",
            cities = cities,
            onDismiss = { showCreateDialog = false },
            onSubmit = { data ->
                viewModel.createZone(data.district, data.cityId, data.level, data.note)
                showCreateDialog = false
            }
        )
    }

    editingZone?.let { zone ->
        SafeZoneFormDialog(
            title = "Редактировать зону",
            zone = zone,
            cities = cities,
            onDismiss = { editingZone = null },
            onSubmit = { data ->
                viewModel.updateZone(zone.id, data.district, data.cityId, data.level, data.note)
                editingZone = null
            }
        )
    }
}

@Composable
fun SafeZoneCard(
    zone: SafeZone,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val levelColor = when (zone.level) {
        SafetyLevel.HIGH -> MaterialTheme.colorScheme.primary
        SafetyLevel.MEDIUM -> MaterialTheme.colorScheme.secondary
        SafetyLevel.LOW -> MaterialTheme.colorScheme.error
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(zone.district, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${zone.cityName}", style = MaterialTheme.typography.bodySmall)
                if (zone.note != null) {
                    Text(zone.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Badge(containerColor = levelColor) {
                    Text(zone.level.name.lowercase().capitalize())
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeZoneFormDialog(
    title: String,
    zone: SafeZone? = null,
    cities: List<com.sologo.app.domain.model.City>,
    onDismiss: () -> Unit,
    onSubmit: (SafeZoneFormData) -> Unit
) {
    var district by remember { mutableStateOf(zone?.district ?: "") }
    var selectedCityId by remember { mutableStateOf(zone?.cityId ?: cities.firstOrNull()?.id ?: 0) }
    var selectedLevel by remember { mutableStateOf(zone?.level ?: SafetyLevel.MEDIUM) }
    var note by remember { mutableStateOf(zone?.note ?: "") }

    var cityExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text("Район *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(expanded = cityExpanded, onExpandedChange = { cityExpanded = it }) {
                    OutlinedTextField(value = cities.find { it.id == selectedCityId }?.name ?: "", onValueChange = {}, readOnly = true, label = { Text("Город *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(cityExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                        cities.forEach { city ->
                            DropdownMenuItem(text = { Text(city.name) }, onClick = { selectedCityId = city.id; cityExpanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(expanded = levelExpanded, onExpandedChange = { levelExpanded = it }) {
                    OutlinedTextField(value = selectedLevel.name.lowercase().capitalize(), onValueChange = {}, readOnly = true, label = { Text("Уровень безопасности *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(levelExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = levelExpanded, onDismissRequest = { levelExpanded = false }) {
                        SafetyLevel.values().forEach { level ->
                            DropdownMenuItem(text = { Text(level.name.lowercase().capitalize()) }, onClick = { selectedLevel = level; levelExpanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Примечание") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (district.isNotBlank() && selectedCityId > 0) {
                    onSubmit(SafeZoneFormData(district, selectedCityId, selectedLevel.name.lowercase(), note.ifEmpty { null }))
                }
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

data class SafeZoneFormData(val district: String, val cityId: Int, val level: String, val note: String?)