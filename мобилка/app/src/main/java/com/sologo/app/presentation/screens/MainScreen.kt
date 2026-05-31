package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.presentation.viewmodel.*
import com.sologo.app.utils.ThemeManager

data class BottomNavItem(val route: String, val title: String, val icon: ImageVector)

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    hotelViewModel: HotelViewModel,
    bookingViewModel: BookingViewModel,
    cityViewModel: CityViewModel,
    routeViewModel: RouteViewModel,
    lostViewModel: LostViewModel,
    safeZoneViewModel: SafeZoneViewModel,
    weatherViewModel: WeatherViewModel,
    onLogout: () -> Unit,
    themeManager: ThemeManager
) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem("home", "", Icons.Default.Home),
        BottomNavItem("weather", "", Icons.Default.WbSunny),
        BottomNavItem("settings", "", Icons.Default.Settings),
        BottomNavItem("profile", "", Icons.Default.Person)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
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
                        icon = { Icon(item.icon, item.title, tint = if (selectedItem == index) SoloGreen else MaterialTheme.colorScheme.onSurfaceVariant) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
            composable("home") {
                HomeScreen(
                    onBookings = { navController.navigate("my_bookings") },
                    onRoutes = { navController.navigate("routes") },
                    onHotels = { navController.navigate("hotels") },
                    onCities = { navController.navigate("cities") },
                    onSafeZones = { navController.navigate("safe_zones") },
                    onNavigateToLogin = { navController.navigate("login") { popUpTo("home") { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("weather") { WeatherScreen(weatherViewModel, cityViewModel, {}) }
            composable("settings") { SettingsScreen(themeManager, onLogout) }
            composable("profile") { ProfileScreen(userViewModel, authViewModel, {}, onLogout) }
            composable("my_bookings") {
                MyBookingsScreen(bookingViewModel, { navController.popBackStack() },
                    { navController.navigate("login") { popUpTo("home") { inclusive = true } } },
                    { hotelId: Int -> navController.navigate("hotel_detail/$hotelId") })
            }
            composable("routes") {
                RouteListScreen(routeViewModel, cityViewModel, { navController.popBackStack() }) { routeId: Int ->
                    navController.navigate("route_detail/$routeId")
                }
            }
            composable("hotels") {
                HotelListScreen(hotelViewModel, cityViewModel, { navController.popBackStack() }) { hotelId: Int ->
                    navController.navigate("hotel_detail/$hotelId")
                }
            }
            composable("cities") {
                CityListScreen(cityViewModel, { navController.popBackStack() }) { cityId: Int, cityName: String ->
                    navController.navigate("city_detail/$cityId/${cityName}")
                }
            }
            composable("safe_zones") { SafeZoneScreen(safeZoneViewModel, { navController.popBackStack() }) }
            composable(route = "hotel_detail/{hotelId}", arguments = listOf(navArgument("hotelId") { type = NavType.IntType })) { backStackEntry ->
                HotelDetailScreen(backStackEntry.arguments?.getInt("hotelId") ?: 0, hotelViewModel, bookingViewModel, { navController.popBackStack() }) { hotelIdInner: Int ->
                    navController.navigate("create_booking/$hotelIdInner")
                }
            }
            composable(route = "create_booking/{hotelId}", arguments = listOf(navArgument("hotelId") { type = NavType.IntType })) { backStackEntry ->
                CreateBookingScreen(backStackEntry.arguments?.getInt("hotelId") ?: 0, bookingViewModel, { navController.popBackStack() }) {
                    navController.popBackStack(); navController.navigate("my_bookings")
                }
            }
            composable(route = "city_detail/{cityId}/{cityName}", arguments = listOf(navArgument("cityId") { type = NavType.IntType }, navArgument("cityName") { type = NavType.StringType })) { backStackEntry ->
                CityDetailScreen(backStackEntry.arguments?.getInt("cityId") ?: 0, backStackEntry.arguments?.getString("cityName") ?: "", koinViewModel(), { navController.popBackStack() },
                    { hotelId: Int -> navController.navigate("hotel_detail/$hotelId") },
                    { routeId: Int -> navController.navigate("route_detail/$routeId") })
            }
            composable(route = "route_detail/{routeId}", arguments = listOf(navArgument("routeId") { type = NavType.IntType })) { backStackEntry ->
                RouteDetailScreen(backStackEntry.arguments?.getInt("routeId") ?: 0, routeViewModel, { navController.popBackStack() })
            }
        }
    }
}