package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.components.RouteFiltersSheet
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.CityViewModel
import com.sologo.app.presentation.viewmodel.RouteViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(
    routeViewModel: RouteViewModel,
    cityViewModel: CityViewModel,
    onBack: () -> Unit,
    onRouteClick: (Int) -> Unit = {}
) {
    val filteredRoutesState by routeViewModel.filteredRoutes.collectAsStateWithLifecycle()
    val filters by routeViewModel.filters.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }

    // Загружаем данные при первом входе
    LaunchedEffect(Unit) {
        routeViewModel.refreshRoutes()
    }

    // Сбрасываем состояние при выходе с экрана
    DisposableEffect(Unit) {
        onDispose {
            routeViewModel.clearState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Маршруты") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.FilterList, null, tint = if (filters.cityName != null || filters.mood != null || filters.minDuration != null) SoloGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = soloGoTopAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (filters.cityName != null || filters.mood != null || filters.minDuration != null) {
                    TextButton(onClick = { routeViewModel.clearFilters() }) { Text("Сбросить фильтры", color = SoloGreen) }
                }
            }
            when (filteredRoutesState) {
                is Result.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SoloGreen)
                }
                is Result.Success -> {
                    val routes = (filteredRoutesState as Result.Success).data
                    if (routes.isEmpty()) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("Нет маршрутов, соответствующих фильтрам", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = { routeViewModel.clearFilters() }) { Text("Сбросить фильтры") }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
                            items(routes, key = { it.id }) { route ->
                                RouteCard(route = route, onClick = { onRouteClick(route.id) })
                            }
                        }
                    }
                }
                is Result.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((filteredRoutesState as Result.Error).message, color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { routeViewModel.refreshRoutes() }) { Text("Повторить") }
                    }
                }
                else -> {}
            }
        }
    }
    if (showFilters) {
        RouteFiltersSheet(cityViewModel = cityViewModel, currentFilters = filters,
            onApply = { newFilters -> routeViewModel.updateFilters(newFilters); showFilters = false },
            onDismiss = { showFilters = false })
    }
}

@Composable
private fun RouteCard(route: com.sologo.app.domain.model.Route, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (route.image != null) AsyncImage(model = route.image, contentDescription = route.title,
                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Text(route.title, style = MaterialTheme.typography.titleLarge, color = SoloGreen, modifier = Modifier.padding(top = if (route.image != null) 12.dp else 0.dp))
            Text("${route.cityName} · ${route.durationHours} ч · ${route.mood.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            Text(route.description ?: "", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp), maxLines = 3)
        }
    }
}