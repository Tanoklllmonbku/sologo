package com.sologo.app.presentation.admin.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.sologo.app.domain.model.Route
import com.sologo.app.presentation.admin.viewmodel.AdminRouteViewModel
import com.sologo.app.presentation.screens.admin.components.AdminTopBar
import com.sologo.app.utils.Result
import com.sologo.app.utils.ImageUrlHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRoutesScreen(
    viewModel: AdminRouteViewModel,
    onNavigateBack: () -> Unit
) {
    val routesState by viewModel.routesState.collectAsStateWithLifecycle()
    val cities by viewModel.cities.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingRoute by remember { mutableStateOf<Route?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadRoutes()
        viewModel.loadCities()
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Управление маршрутами",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Добавить маршрут")
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
            when (routesState) {
                is Result.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Result.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text((routesState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadRoutes() }) { Text("Повторить") }
                    }
                }
                is Result.Success -> {
                    val routes = (routesState as Result.Success).data
                    if (routes.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Map, null, modifier = Modifier.size(64.dp))
                            Text("Нет маршрутов")
                            Button(onClick = { showCreateDialog = true }) { Text("Добавить маршрут") }
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(routes) { route ->
                                RouteCard(
                                    route = route,
                                    onEdit = { editingRoute = route },
                                    onDelete = { viewModel.deleteRoute(route.id) }
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
        RouteFormDialog(
            title = "Добавить маршрут",
            cities = cities,
            onDismiss = { showCreateDialog = false },
            onSubmit = { data, imageFile ->
                viewModel.createRoute(
                    title = data.title,
                    cityId = data.cityId,
                    mood = data.mood,
                    description = data.description,
                    durationHours = data.durationHours,
                    imageFile = imageFile
                )
                showCreateDialog = false
            }
        )
    }

    editingRoute?.let { route ->
        RouteFormDialog(
            title = "Редактировать маршрут",
            route = route,
            cities = cities,
            onDismiss = { editingRoute = null },
            onSubmit = { data, imageFile ->
                viewModel.updateRoute(
                    routeId = route.id,
                    title = data.title,
                    description = data.description,
                    mood = data.mood,
                    cityId = data.cityId,
                    durationHours = data.durationHours,
                    imageFile = imageFile
                )
                editingRoute = null
            }
        )
    }
}

@Composable
fun RouteCard(
    route: Route,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            if (route.image != null) {
                Image(
                    painter = rememberAsyncImagePainter(ImageUrlHelper.toFullImageUrl(route.image)),
                    contentDescription = route.title,
                    modifier = Modifier.size(80.dp).padding(end = 12.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(80.dp).padding(end = 12.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(route.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${route.cityName} • ${route.durationHours} ч", style = MaterialTheme.typography.bodySmall)
                Text(route.mood.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall)
                if (route.description != null) {
                    Text(route.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                }
            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFormDialog(
    title: String,
    route: Route? = null,
    cities: List<com.sologo.app.domain.model.City>,
    onDismiss: () -> Unit,
    onSubmit: (RouteFormData, File?) -> Unit
) {
    var titleText by remember { mutableStateOf(route?.title ?: "") }
    var selectedCityId by remember { mutableStateOf(route?.cityId ?: cities.firstOrNull()?.id ?: 0) }
    var selectedMood by remember { mutableStateOf(route?.mood?.name?.lowercase() ?: "calm") }
    var description by remember { mutableStateOf(route?.description ?: "") }
    var durationHours by remember { mutableStateOf(route?.durationHours?.toString() ?: "2") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }

    var cityExpanded by remember { mutableStateOf(false) }
    var moodExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Лаунчер для выбора фото из галереи
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        imageFile = uri?.let { saveImageToCache(context, it) }
    }

    val moods = listOf("calm", "active", "cultural")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Превью фото
                if (imageUri != null || route?.image != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                imageUri ?: ImageUrlHelper.toFullImageUrl(route?.image)
                            ),
                            contentDescription = "Превью",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                imageUri = null
                                imageFile = null
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Close, "Удалить фото", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Кнопка выбора фото
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (imageUri != null || route?.image != null) "Изменить фото" else "Добавить фото")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Название *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Город
                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { cityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = cities.find { it.id == selectedCityId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Город *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(cityExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city.name) },
                                onClick = {
                                    selectedCityId = city.id
                                    cityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Настроение
                ExposedDropdownMenuBox(
                    expanded = moodExpanded,
                    onExpandedChange = { moodExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMood.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Настроение *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(moodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = moodExpanded,
                        onDismissRequest = { moodExpanded = false }
                    ) {
                        moods.forEach { mood ->
                            DropdownMenuItem(
                                text = { Text(mood.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedMood = mood
                                    moodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = durationHours,
                    onValueChange = { durationHours = it },
                    label = { Text("Длительность (часы)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val duration = durationHours.toIntOrNull() ?: 2
                    if (titleText.isNotBlank() && selectedCityId > 0) {
                        onSubmit(
                            RouteFormData(
                                title = titleText,
                                cityId = selectedCityId,
                                mood = selectedMood,
                                description = description.ifEmpty { null },
                                durationHours = duration
                            ),
                            imageFile
                        )
                    }
                }
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

// Функция сохранения URI во временный файл
private fun saveImageToCache(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri) ?: throw Exception("Cannot open image")
    val tempFile = File.createTempFile("route_image_", ".jpg", context.cacheDir)
    FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}

data class RouteFormData(
    val title: String,
    val cityId: Int,
    val mood: String,
    val description: String?,
    val durationHours: Int
)