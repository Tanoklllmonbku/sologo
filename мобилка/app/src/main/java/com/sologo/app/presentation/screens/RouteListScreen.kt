// presentation/screens/RouteListScreen.kt
package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.theme.SoloOffWhite
import com.sologo.app.presentation.theme.SoloWhite
import com.sologo.app.presentation.theme.soloGoTopAppBarColors
import com.sologo.app.presentation.viewmodel.RouteViewModel
import com.sologo.app.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(
    routeViewModel: RouteViewModel,
    onBack: () -> Unit
) {
    val routesState by routeViewModel.routesState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        routeViewModel.loadRoutes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Маршруты") },
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
        when (routesState) {
            is Result.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = SoloGreen)
                }
            }

            is Result.Success -> {
                val routes = (routesState as Result.Success).data

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(padding)
                ) {
                    items(routes, key = { it.id }) { route ->
                        RouteCard(route = route)
                    }
                }
            }

            is Result.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Ошибка загрузки маршрутов", color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun RouteCard(route: com.sologo.app.domain.model.Route) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoloWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Изображение (если есть)
            if (route.image != null) {
                AsyncImage(
                    model = route.image,
                    contentDescription = route.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = route.title,
                style = MaterialTheme.typography.titleLarge,
                color = SoloGreen,
                modifier = Modifier.padding(top = if (route.image != null) 12.dp else 0.dp)
            )

            Text(
                text = "${route.cityName} · ${route.durationHours} ч",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = route.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}