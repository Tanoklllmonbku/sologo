package com.sologo.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import com.sologo.app.presentation.screens.*
import com.sologo.app.presentation.viewmodel.*
import com.sologo.app.utils.ThemeManager

@Composable
fun SoloGoNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // ViewModels
    val authViewModel: AuthViewModel = koinViewModel()
    val userViewModel: UserViewModel = koinViewModel()
    val hotelViewModel: HotelViewModel = koinViewModel()
    val bookingViewModel: BookingViewModel = koinViewModel()
    val cityViewModel: CityViewModel = koinViewModel()
    val routeViewModel: RouteViewModel = koinViewModel()
    val lostViewModel: LostViewModel = koinViewModel()
    val safeZoneViewModel: SafeZoneViewModel = koinViewModel()
    val weatherViewModel: WeatherViewModel = koinViewModel()
    val themeManager: ThemeManager = getKoin().get()

    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "login",
        modifier = modifier
    ) {
        // Экран логина
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Экран регистрации
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Главный экран с нижней навигацией
        composable("main") {
            MainScreen(
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                hotelViewModel = hotelViewModel,
                bookingViewModel = bookingViewModel,
                cityViewModel = cityViewModel,
                routeViewModel = routeViewModel,
                lostViewModel = lostViewModel,
                safeZoneViewModel = safeZoneViewModel,
                weatherViewModel = weatherViewModel,
                themeManager = themeManager,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        // Остальные экраны...
        composable("my_bookings") {
            MyBookingsScreen(
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onHotelClick = { hotelId: Int ->
                    navController.navigate("hotel_detail/$hotelId")
                }
            )
        }

        composable("routes") {
            RouteListScreen(
                routeViewModel = routeViewModel,
                cityViewModel = cityViewModel,
                onBack = { navController.popBackStack() },
                onRouteClick = { routeId: Int ->
                    navController.navigate("route_detail/$routeId")
                }
            )
        }

        composable("hotels") {
            HotelListScreen(
                hotelViewModel = hotelViewModel,
                cityViewModel = cityViewModel,
                onBack = { navController.popBackStack() },
                onHotelClick = { hotelId: Int ->
                    navController.navigate("hotel_detail/$hotelId")
                }
            )
        }

        composable("cities") {
            CityListScreen(
                cityViewModel = cityViewModel,
                onBack = { navController.popBackStack() },
                onCityClick = { cityId: Int, cityName: String ->
                    navController.navigate("city_detail/$cityId/${cityName}")
                }
            )
        }

        composable("safe_zones") {
            SafeZoneScreen(
                safeZoneViewModel = safeZoneViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("lost_report") {
            LostReportScreen(
                lostViewModel = lostViewModel,
                cityViewModel = cityViewModel,  // ← ДОБАВЛЯЕМ cityViewModel
                onBack = { navController.popBackStack() }
            )
        }

        // Экран детали отеля
        composable(
            route = "hotel_detail/{hotelId}",
            arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            HotelDetailScreen(
                hotelId = hotelId,
                hotelViewModel = hotelViewModel,
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onCreateBooking = { hotelIdInner: Int ->
                    navController.navigate("create_booking/$hotelIdInner")
                }
            )
        }

        // Экран создания бронирования
        composable(
            route = "create_booking/{hotelId}",
            arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            CreateBookingScreen(
                hotelId = hotelId,
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate("my_bookings")
                }
            )
        }

        // Экран детали города
        composable(
            route = "city_detail/{cityId}/{cityName}",
            arguments = listOf(
                navArgument("cityId") { type = NavType.IntType },
                navArgument("cityName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getInt("cityId") ?: 0
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val cityDetailViewModel: CityDetailViewModel = koinViewModel()

            CityDetailScreen(
                cityId = cityId,
                cityName = cityName,
                viewModel = cityDetailViewModel,
                onBack = { navController.popBackStack() },
                onHotelClick = { hotelId: Int ->
                    navController.navigate("hotel_detail/$hotelId")
                },
                onRouteClick = { routeId: Int ->
                    navController.navigate("route_detail/$routeId")
                }
            )
        }

        // Экран детали маршрута
        composable(
            route = "route_detail/{routeId}",
            arguments = listOf(navArgument("routeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getInt("routeId") ?: 0
            RouteDetailScreen(
                routeId = routeId,
                routeViewModel = routeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}