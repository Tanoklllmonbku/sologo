package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import com.sologo.app.domain.model.UserRole
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.viewmodel.*
import com.sologo.app.utils.ThemeManager

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    hotelViewModel: HotelViewModel,
    bookingViewModel: BookingViewModel,
    cityViewModel: CityViewModel,
    routeViewModel: RouteViewModel,
    safeZoneViewModel: SafeZoneViewModel,
    weatherViewModel: WeatherViewModel,
    onLogout: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    themeManager: ThemeManager
) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == UserRole.ADMIN

    val bottomNavItems = listOf(
        BottomNavItem("home", "Главная", Icons.Default.Home),
        BottomNavItem("weather", "Погода", Icons.Default.WbSunny),
        BottomNavItem("settings", "Настройки", Icons.Default.Settings),
        BottomNavItem("profile", "Профиль", Icons.Default.Person)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                item.title,
                                tint = if (selectedItem == index) SoloGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Главная вкладка
            composable("home") {
                HomeScreen(
                    authViewModel = authViewModel,  // ← добавить
                    onBookings = { navController.navigate("my_bookings") },
                    onRoutes = { navController.navigate("routes") },
                    onHotels = { navController.navigate("hotels") },
                    onCities = { navController.navigate("cities") },
                    onSafeZones = { navController.navigate("safe_zones") },
                    onNavigateToAdmin = onNavigateToAdmin,  // ← добавить
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            // Погода
            composable("weather") {
                WeatherScreen(
                    weatherViewModel,
                    cityViewModel,
                    { navController.popBackStack() }
                )
            }

            // Настройки
            composable("settings") {
                SettingsScreen(themeManager, onLogout)
            }

            // Профиль
            composable("profile") {
                ProfileScreen(
                    userViewModel,
                    authViewModel,
                    { navController.popBackStack() },
                    onLogout
                )
            }

            // Мои бронирования
            composable("my_bookings") {
                MyBookingsScreen(
                    bookingViewModel,
                    { navController.popBackStack() },
                    { navController.navigate("login") { popUpTo("home") { inclusive = true } } },
                    { hotelId: Int -> navController.navigate("hotel_detail/$hotelId") }
                )
            }

            // Маршруты
            composable("routes") {
                RouteListScreen(
                    routeViewModel,
                    cityViewModel,
                    { navController.popBackStack() }
                ) { routeId: Int ->
                    navController.navigate("route_detail/$routeId")
                }
            }

            // Отели
            composable("hotels") {
                HotelListScreen(
                    hotelViewModel,
                    cityViewModel,
                    { navController.popBackStack() }
                ) { hotelId: Int ->
                    navController.navigate("hotel_detail/$hotelId")
                }
            }

            // Города
            composable("cities") {
                CityListScreen(
                    cityViewModel,
                    { navController.popBackStack() }
                ) { cityId: Int, cityName: String ->
                    navController.navigate("city_detail/$cityId/${cityName}")
                }
            }

            // Безопасные зоны
            composable("safe_zones") {
                SafeZoneScreen(safeZoneViewModel, { navController.popBackStack() })
            }

            // Детали отеля
            composable(
                route = "hotel_detail/{hotelId}",
                arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
            ) { backStackEntry ->
                HotelDetailScreen(
                    backStackEntry.arguments?.getInt("hotelId") ?: 0,
                    hotelViewModel,
                    bookingViewModel,
                    { navController.popBackStack() }
                ) { hotelIdInner: Int ->
                    navController.navigate("create_booking/$hotelIdInner")
                }
            }

            // Создание бронирования
            composable(
                route = "create_booking/{hotelId}",
                arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
            ) { backStackEntry ->
                CreateBookingScreen(
                    backStackEntry.arguments?.getInt("hotelId") ?: 0,
                    bookingViewModel,
                    { navController.popBackStack() }
                ) {
                    navController.popBackStack()
                    navController.navigate("my_bookings")
                }
            }

            // Детали города
            composable(
                route = "city_detail/{cityId}/{cityName}",
                arguments = listOf(
                    navArgument("cityId") { type = NavType.IntType },
                    navArgument("cityName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                CityDetailScreen(
                    backStackEntry.arguments?.getInt("cityId") ?: 0,
                    backStackEntry.arguments?.getString("cityName") ?: "",
                    koinViewModel(),
                    { navController.popBackStack() },
                    { hotelId: Int -> navController.navigate("hotel_detail/$hotelId") },
                    { routeId: Int -> navController.navigate("route_detail/$routeId") }
                )
            }

            // Детали маршрута
            composable(
                route = "route_detail/{routeId}",
                arguments = listOf(navArgument("routeId") { type = NavType.IntType })
            ) { backStackEntry ->
                RouteDetailScreen(
                    backStackEntry.arguments?.getInt("routeId") ?: 0,
                    routeViewModel,
                    { navController.popBackStack() }
                )
            }
        }
    }
}