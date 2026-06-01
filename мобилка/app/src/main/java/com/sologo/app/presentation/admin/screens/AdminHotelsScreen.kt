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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.sologo.app.domain.model.City
import com.sologo.app.domain.model.HotelDetail
import com.sologo.app.presentation.screens.admin.components.AdminTopBar
import com.sologo.app.presentation.viewmodel.admin.AdminHotelsViewModel
import com.sologo.app.utils.Result
import com.sologo.app.utils.ImageUrlHelper
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHotelsScreen(
    viewModel: AdminHotelsViewModel,
    onNavigateBack: () -> Unit
) {
    val hotelsState by viewModel.hotelsState.collectAsStateWithLifecycle()
    val cities by viewModel.cities.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingHotel by remember { mutableStateOf<HotelDetail?>(null) }
    var showDeleteDialog by remember { mutableStateOf<HotelDetail?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadHotels()
        viewModel.loadCities()
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Управление отелями",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Добавить отель")
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
            when (hotelsState) {
                is Result.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Result.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (hotelsState as Result.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadHotels() }) {
                            Text("Повторить")
                        }
                    }
                }
                is Result.Success -> {
                    val hotels = (hotelsState as Result.Success).data
                    if (hotels.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Hotel, null, modifier = Modifier.size(64.dp))
                            Text("Нет отелей", style = MaterialTheme.typography.bodyLarge)
                            Button(onClick = { showCreateDialog = true }) {
                                Text("Добавить первый отель")
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(hotels, key = { it.id }) { hotel ->
                                HotelCard(
                                    hotel = hotel,
                                    onEdit = { editingHotel = hotel },
                                    onDelete = { showDeleteDialog = hotel }
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    // Диалог создания отеля
    if (showCreateDialog) {
        HotelFormDialog(
            title = "Добавить отель",
            cities = cities,
            onDismiss = { showCreateDialog = false },
            onSubmit = { hotelData, mainImageFile, roomImageFiles ->
                viewModel.createHotel(
                    name = hotelData.name,
                    cityId = hotelData.cityId,
                    address = hotelData.address,
                    pricePerNight = hotelData.pricePerNight,
                    avgCityPrice = hotelData.avgCityPrice,
                    description = hotelData.description,
                    rating = hotelData.rating,
                    capacity = hotelData.capacity,
                    managerPhones = hotelData.managerPhones,
                    mainImageFile = mainImageFile,
                    roomImageFiles = roomImageFiles
                )
                showCreateDialog = false
            }
        )
    }

    // Диалог редактирования отеля
    editingHotel?.let { hotel ->
        HotelFormDialog(
            title = "Редактировать отель",
            hotel = hotel,
            cities = cities,
            onDismiss = { editingHotel = null },
            onSubmit = { hotelData, mainImageFile, roomImageFiles ->
                viewModel.updateHotel(
                    hotelId = hotel.id,
                    name = hotelData.name,
                    cityId = hotelData.cityId,
                    address = hotelData.address,
                    pricePerNight = hotelData.pricePerNight,
                    description = hotelData.description,
                    rating = hotelData.rating,
                    capacity = hotelData.capacity,
                    mainImageFile = mainImageFile,
                    roomImageFiles = roomImageFiles
                )
                editingHotel = null
            }
        )
    }

    // Диалог удаления
    showDeleteDialog?.let { hotel ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Удалить отель") },
            text = { Text("Вы уверены, что хотите удалить отель \"${hotel.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHotel(hotel.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun HotelCard(
    hotel: HotelDetail,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (hotel.mainImage != null) {
                Image(
                    painter = rememberAsyncImagePainter(ImageUrlHelper.toFullImageUrl(hotel.mainImage)),
                    contentDescription = hotel.name,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Hotel, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${hotel.cityName}, ${hotel.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "💰 ${hotel.pricePerNight} ₽/ночь",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "⭐ ${hotel.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "👥 ${hotel.capacity} мест",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (hotel.status == 0) {
                    Badge(
                        modifier = Modifier.padding(top = 4.dp),
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text("Удалён")
                    }
                }
            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelFormDialog(
    title: String,
    hotel: HotelDetail? = null,
    cities: List<City>,
    onDismiss: () -> Unit,
    onSubmit: (HotelFormData, File?, List<File>?) -> Unit
) {
    var name by remember { mutableStateOf(hotel?.name ?: "") }
    var selectedCityId by remember { mutableStateOf(hotel?.cityId ?: cities.firstOrNull()?.id ?: 0) }
    var address by remember { mutableStateOf(hotel?.address ?: "") }
    var pricePerNight by remember { mutableStateOf(hotel?.pricePerNight?.toString() ?: "") }
    var avgCityPrice by remember { mutableStateOf(hotel?.avgCityPrice?.toString() ?: "") }
    var description by remember { mutableStateOf(hotel?.description ?: "") }
    var rating by remember { mutableStateOf(hotel?.rating?.toString() ?: "0.0") }
    var capacity by remember { mutableStateOf(hotel?.capacity?.toString() ?: "10") }

    // Фото
    var mainImageUri by remember { mutableStateOf<Uri?>(null) }
    var mainImageFile by remember { mutableStateOf<File?>(null) }
    var roomImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var roomImageFiles by remember { mutableStateOf<List<File>>(emptyList()) }

    var cityExpanded by remember { mutableStateOf(false) }
    var showRoomImagePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Лаунчер для выбора главного фото
    val mainImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        mainImageUri = uri
        mainImageFile = uri?.let { saveImageToCache(context, it, "hotel_main") }
    }

    // Лаунчер для выбора фото номеров
    val roomImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        roomImageUris = uris
        roomImageFiles = uris.mapNotNull { saveImageToCache(context, it, "hotel_room") }
    }

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
                // Основное фото
                Text("Основное фото", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                if (mainImageUri != null || hotel?.mainImage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                mainImageUri ?: ImageUrlHelper.toFullImageUrl(hotel?.mainImage)
                            ),
                            contentDescription = "Превью",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                mainImageUri = null
                                mainImageFile = null
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Close, "Удалить фото", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Button(
                    onClick = { mainImagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (mainImageUri != null || hotel?.mainImage != null) "Изменить основное фото" else "Добавить основное фото")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Фото номеров
                Text("Фото номеров", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                if (roomImageUris.isNotEmpty() || hotel?.roomImages?.isNotEmpty() == true) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 8.dp)
                    ) {
                        val currentImages = if (roomImageUris.isNotEmpty()) roomImageUris else hotel?.roomImages?.mapNotNull { Uri.parse(it) } ?: emptyList()
                        items(currentImages.size) { index ->
                            Box(
                                modifier = Modifier.size(100.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        roomImageUris.getOrNull(index) ?: ImageUrlHelper.toFullImageUrl(hotel?.roomImages?.getOrNull(index))
                                    ),
                                    contentDescription = "Фото номера",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = {
                                        roomImageUris = roomImageUris.toMutableList().apply { removeAt(index) }
                                        roomImageFiles = roomImageFiles.toMutableList().apply { removeAt(index) }
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Close, "Удалить", modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { roomImagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Добавить фото номеров")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название отеля *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { cityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = cities.find { it.id == selectedCityId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Город *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
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

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Адрес *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pricePerNight,
                    onValueChange = { pricePerNight = it },
                    label = { Text("Цена за ночь (₽) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = avgCityPrice,
                    onValueChange = { avgCityPrice = it },
                    label = { Text("Средняя цена по городу (₽)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Рейтинг (0-5)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Вместимость (чел) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = pricePerNight.toIntOrNull()
                    val avgPrice = avgCityPrice.toIntOrNull() ?: price ?: 0
                    val ratingValue = rating.toDoubleOrNull() ?: 0.0
                    val capacityValue = capacity.toIntOrNull() ?: 10

                    if (name.isNotBlank() && selectedCityId > 0 && address.isNotBlank() && price != null) {
                        onSubmit(
                            HotelFormData(
                                name = name,
                                cityId = selectedCityId,
                                address = address,
                                pricePerNight = price,
                                avgCityPrice = avgPrice,
                                description = description.ifEmpty { null },
                                rating = ratingValue,
                                capacity = capacityValue,
                                managerPhones = null
                            ),
                            mainImageFile,
                            roomImageFiles.ifEmpty { null }
                        )
                    }
                },
                enabled = name.isNotBlank() && selectedCityId > 0 && address.isNotBlank() && pricePerNight.toIntOrNull() != null
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
private fun saveImageToCache(context: Context, uri: Uri, prefix: String): File {
    val inputStream = context.contentResolver.openInputStream(uri) ?: throw Exception("Cannot open image")
    val tempFile = File.createTempFile("hotel_${prefix}_", ".jpg", context.cacheDir)
    FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}

data class HotelFormData(
    val name: String,
    val cityId: Int,
    val address: String,
    val pricePerNight: Int,
    val avgCityPrice: Int,
    val description: String?,
    val rating: Double,
    val capacity: Int,
    val managerPhones: List<String>?
)