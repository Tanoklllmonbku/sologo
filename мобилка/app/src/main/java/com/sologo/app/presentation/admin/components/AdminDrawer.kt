// presentation/screens/admin/components/AdminDrawer.kt
package com.sologo.app.presentation.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class AdminMenuItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val route: String
)

@Composable
fun AdminDrawer(
    currentRoute: String,
    onMenuItemClick: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        AdminMenuItem("Дашборд", { Icon(Icons.Default.Dashboard, null) }, "admin_dashboard"),
        AdminMenuItem("Города", { Icon(Icons.Default.LocationCity, null) }, "admin_cities"),
        AdminMenuItem("Отели", { Icon(Icons.Default.Hotel, null) }, "admin_hotels"),
        AdminMenuItem("Бронирования", { Icon(Icons.Default.Bookmark, null) }, "admin_bookings"),
        AdminMenuItem("Маршруты", { Icon(Icons.Default.Map, null) }, "admin_routes"),
        AdminMenuItem("Безопасные зоны", { Icon(Icons.Default.Security, null) }, "admin_safe_zones"),
        AdminMenuItem("Lost-сообщения", { Icon(Icons.Default.Warning, null) }, "admin_lost_reports"),
        AdminMenuItem("Пользователи", { Icon(Icons.Default.People, null) }, "admin_users")
    )

    ModalDrawerSheet(
        modifier = modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "SoloGo Admin",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Панель управления",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        NavigationDrawerItem(
            label = { Text("Главная") },
            icon = { Icon(Icons.Default.Home, null) },
            selected = currentRoute == "admin_dashboard",
            onClick = { onMenuItemClick("admin_dashboard") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        menuItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title) },
                icon = item.icon,
                selected = currentRoute == item.route,
                onClick = { onMenuItemClick(item.route) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Выйти") },
            icon = { Icon(Icons.Default.Logout, null) },
            selected = false,
            onClick = { onMenuItemClick("logout") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}