package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.RouteViewModel
import com.sologo.app.utils.ImageUrlHelper
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: Int,
    routeViewModel: RouteViewModel,
    onBack: () -> Unit
) {
    val routeDetailState by routeViewModel.routeDetailState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(routeId) { routeViewModel.loadRouteById(routeId) }

    val route = if (routeDetailState is Result.Success) (routeDetailState as Result.Success).data else null
    val isLoading = routeDetailState is Result.Loading
    val error = if (routeDetailState is Result.Error) (routeDetailState as Result.Error).message else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Маршрут") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = {
                    if (route != null) IconButton(onClick = {
                        val shareText = buildString {
                            appendLine("🗺️ ${route.title}")
                            appendLine("📍 Город: ${route.cityName}")
                            appendLine("⏱ Длительность: ${route.durationHours} ч")
                            appendLine("🎭 Настроение: ${getMoodText(route.mood.name)}")
                            if (!route.description.isNullOrBlank()) appendLine("\n📝 ${route.description}")
                        }
                        context.startActivity(android.content.Intent.createChooser(android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }, "Поделиться"))
                    }) { Icon(Icons.Default.Share, null) }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when {
                isLoading -> CircularProgressIndicator(color = SoloGreen)
                error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { routeViewModel.loadRouteById(routeId) }) { Text("Повторить") }
                }
                route != null -> Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Фото маршрута с ImageUrlHelper
                    if (route.image != null) {
                        AsyncImage(
                            model = ImageUrlHelper.toFullImageUrl(route.image),
                            contentDescription = route.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(route.title, style = MaterialTheme.typography.headlineMedium, color = SoloGreen, modifier = Modifier.padding(top = if (route.image != null) 16.dp else 0.dp))
                    Text(route.cityName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard("Длительность", "${route.durationHours} ч", Modifier.weight(1f))
                        InfoCard("Настроение", getMoodText(route.mood.name), Modifier.weight(1f))
                    }
                    if (!route.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Описание", style = MaterialTheme.typography.titleMedium, color = SoloGreen)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(route.description!!, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        val shareText = buildString {
                            appendLine("🗺️ ${route.title}")
                            appendLine("📍 Город: ${route.cityName}")
                            appendLine("⏱ Длительность: ${route.durationHours} ч")
                            appendLine("🎭 Настроение: ${getMoodText(route.mood.name)}")
                            if (!route.description.isNullOrBlank()) appendLine("\n📝 ${route.description}")
                        }
                        context.startActivity(android.content.Intent.createChooser(android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }, "Поделиться"))
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SoloGreen), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.Share, null); Spacer(Modifier.height(4.dp)); Text("Поделиться")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SoloGreen.copy(alpha = 0.1f)), modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = SoloGreen)
        }
    }
}

private fun getMoodText(mood: String): String = when (mood.lowercase()) {
    "calm" -> "Спокойный"; "active" -> "Активный"; "cultural" -> "Культурный"; else -> mood
}