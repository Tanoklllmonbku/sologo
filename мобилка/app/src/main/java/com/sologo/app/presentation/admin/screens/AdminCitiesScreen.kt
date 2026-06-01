// presentation/screens/admin/AdminCitiesScreen.kt (полная версия)
package com.sologo.app.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.domain.model.City
import com.sologo.app.presentation.screens.admin.components.AdminTopBar
import com.sologo.app.presentation.admin.viewmodel.AdminCityViewModel
import com.sologo.app.utils.Result
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCitiesScreen(
    viewModel: AdminCityViewModel,
    onNavigateBack: () -> Unit
) {
    val citiesState by viewModel.citiesState.collectAsStateWithLifecycle()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingCity by remember { mutableStateOf<City?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCities()
    }

    // Показываем Snackbar при успешной операции
    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Управление городами",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Добавить город")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Добавить")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (citiesState) {
                is Result.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Result.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (citiesState as Result.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadCities() }) {
                            Text("Повторить")
                        }
                    }
                }
                is Result.Success -> {
                    val cities = (citiesState as Result.Success).data
                    if (cities.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LocationCity,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Нет городов",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { showCreateDialog = true }) {
                                Text("Добавить первый город")
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cities) { city ->
                                CityCard(
                                    city = city,
                                    onEdit = { editingCity = city },
                                    onDelete = { viewModel.deleteCity(city.id) }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    // Диалог создания города
    if (showCreateDialog) {
        CityFormDialog(
            title = "Добавить город",
            onDismiss = { showCreateDialog = false },
            onSubmit = { name, country ->
                viewModel.createCity(name, country)
                showCreateDialog = false
            }
        )
    }

    // Диалог редактирования города
    editingCity?.let { city ->
        CityFormDialog(
            title = "Редактировать город",
            initialName = city.name,
            initialCountry = city.country,
            onDismiss = { editingCity = null },
            onSubmit = { name, country ->
                viewModel.updateCity(city.id, name, country)
                editingCity = null
            }
        )
    }
}

@Composable
fun CityCard(
    city: City,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = city.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Создан: ${dateFormat.format(city.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить город") },
            text = { Text("Вы уверены, что хотите удалить город \"${city.name}\"? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun CityFormDialog(
    title: String,
    initialName: String = "",
    initialCountry: String = "",
    onDismiss: () -> Unit,
    onSubmit: (name: String, country: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var country by remember { mutableStateOf(initialCountry) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var countryError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Название не может быть пустым" else null
                    },
                    label = { Text("Название города") },
                    isError = nameError != null,
                    supportingText = { if (nameError != null) Text(nameError!!) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = country,
                    onValueChange = {
                        country = it
                        countryError = if (it.isBlank()) "Страна не может быть пустой" else null
                    },
                    label = { Text("Страна") },
                    isError = countryError != null,
                    supportingText = { if (countryError != null) Text(countryError!!) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && country.isNotBlank()) {
                        onSubmit(name, country)
                    }
                },
                enabled = name.isNotBlank() && country.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}