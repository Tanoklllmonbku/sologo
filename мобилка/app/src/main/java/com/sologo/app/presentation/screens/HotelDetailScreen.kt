package com.sologo.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.BookingViewModel
import com.sologo.app.presentation.viewmodel.HotelViewModel
import com.sologo.app.utils.ImageUrlHelper
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotelId: Int,
    hotelViewModel: HotelViewModel,
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit,
    onCreateBooking: (Int) -> Unit
) {
    val hotelDetailState by hotelViewModel.hotelDetailState.collectAsStateWithLifecycle()

    // Состояние для диалога просмотра фото
    var showFullScreenImage by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableIntStateOf(0) }
    var fullScreenImages by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(hotelId) {
        hotelViewModel.loadHotelDetail(hotelId)
    }

    // Собираем все фото в один список
    fun getAllImages(hotel: com.sologo.app.domain.model.HotelDetail): List<String> {
        val images = mutableListOf<String>()
        hotel.mainImage?.let { images.add(it) }
        hotel.roomImages?.let { images.addAll(it) }
        return images.mapNotNull { ImageUrlHelper.toFullImageUrl(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отель") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (hotelDetailState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SoloGreen)
                }
            }
            is Result.Success -> {
                val hotel = (hotelDetailState as Result.Success).data
                val allImages = getAllImages(hotel)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Основное фото (кликабельное)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if (allImages.isNotEmpty()) {
                                        fullScreenImages = allImages
                                        currentImageIndex = 0
                                        showFullScreenImage = true
                                    }
                                }
                            }
                    ) {
                        AsyncImage(
                            model = allImages.firstOrNull(),
                            contentDescription = hotel.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Галерея фото номеров (кликабельные)
                    if (hotel.roomImages.isNullOrEmpty().not()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Фото номеров",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoloGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(hotel.roomImages ?: emptyList()) { index, imageUrl ->
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .pointerInput(Unit) {
                                            detectTapGestures {
                                                fullScreenImages = allImages
                                                currentImageIndex = index + 1
                                                showFullScreenImage = true
                                            }
                                        }
                                ) {
                                    AsyncImage(
                                        model = ImageUrlHelper.toFullImageUrl(imageUrl),
                                        contentDescription = "Фото номера",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = "${hotel.cityName} · ${hotel.address}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${hotel.pricePerNight} ₽ за ночь · средняя по городу ${hotel.avgCityPrice} ₽",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Text(
                        text = "Вместимость: до ${hotel.capacity} гостей",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (hotel.rating > 0) {
                        Text(
                            text = "Рейтинг: ★ ${hotel.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (!hotel.description.isNullOrBlank()) {
                        Text(
                            text = hotel.description!!,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    // Телефоны менеджеров
                    if (!hotel.managerPhones.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Контакты",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoloGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        hotel.managerPhones.forEach { phone ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = SoloGreen
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { onCreateBooking(hotel.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoloGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Забронировать", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            is Result.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((hotelDetailState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(
                            onClick = { hotelViewModel.loadHotelDetail(hotelId) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
            else -> {}
        }
    }

    // Диалог просмотра фото в полный экран
    if (showFullScreenImage && fullScreenImages.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))  // ← ИСПРАВЛЕНО: добавлен background
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { showFullScreenImage = false }
                    )
                }
        ) {
            // Текущее фото
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullScreenImages.getOrNull(currentImageIndex),
                    contentDescription = "Полноэкранное фото",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Кнопка закрытия
            IconButton(
                onClick = { showFullScreenImage = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = Color.White
                )
            }

            // Количество фото и индикатор
            if (fullScreenImages.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Кнопка "Назад" (стрелка влево)
                    IconButton(
                        onClick = {
                            currentImageIndex = (currentImageIndex - 1 + fullScreenImages.size) % fullScreenImages.size
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Предыдущее",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "${currentImageIndex + 1} / ${fullScreenImages.size}",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    // Кнопка "Вперёд" (стрелка вправо) - ИСПРАВЛЕНО
                    IconButton(
                        onClick = {
                            currentImageIndex = (currentImageIndex + 1) % fullScreenImages.size
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Следующее",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}