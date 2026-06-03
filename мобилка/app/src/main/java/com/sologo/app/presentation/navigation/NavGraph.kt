package com.sologo.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sologo.app.domain.model.UserRole
import com.sologo.app.presentation.admin.screens.AdminBookingsScreen
import com.sologo.app.presentation.admin.screens.AdminDashboardScreen
import com.sologo.app.presentation.admin.screens.AdminHotelsScreen
import com.sologo.app.presentation.admin.screens.AdminRoutesScreen
import com.sologo.app.presentation.admin.screens.AdminSafeZonesScreen
import com.sologo.app.presentation.admin.screens.AdminUsersScreen
import com.sologo.app.presentation.screens.*
import com.sologo.app.presentation.screens.admin.*
import com.sologo.app.presentation.viewmodel.*
import com.sologo.app.presentation.admin.viewmodel.*
import com.sologo.app.presentation.viewmodel.admin.AdminBookingViewModel
import com.sologo.app.presentation.viewmodel.admin.AdminHotelsViewModel
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

    // Admin ViewModels
    val adminCityViewModel: AdminCityViewModel = koinViewModel()
    val adminHotelsViewModel: AdminHotelsViewModel = koinViewModel()
    val adminRouteViewModel: AdminRouteViewModel = koinViewModel()
    val adminSafeZoneViewModel: AdminSafeZoneViewModel = koinViewModel()
    val adminBookingViewModel: AdminBookingViewModel = koinViewModel()
    val adminUserViewModel: AdminUserViewModel = koinViewModel()

    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == UserRole.ADMIN

    // Определяем стартовый экран
    val startDestination = when {
        !isLoggedIn -> "login"
        isAdmin -> "admin_dashboard"
        else -> "main"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ========== ЭКРАНЫ АВТОРИЗАЦИИ ==========

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    // После входа проверяем роль и направляем на нужный экран
                    val destination = if (isAdmin) "admin_dashboard" else "main"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

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

        // ========== ОСНОВНОЙ UI (ДЛЯ ОБЫЧНЫХ ПОЛЬЗОВАТЕЛЕЙ) ==========

        composable("main") {
            LaunchedEffect(Unit) {
                if (!isLoggedIn) {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            }

            MainScreen(
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                hotelViewModel = hotelViewModel,
                bookingViewModel = bookingViewModel,
                cityViewModel = cityViewModel,
                routeViewModel = routeViewModel,
                safeZoneViewModel = safeZoneViewModel,
                weatherViewModel = weatherViewModel,
                themeManager = themeManager,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    if (isAdmin) {
                        navController.navigate("admin_dashboard")
                    }
                }
            )
        }

        // ========== ПОЛЬЗОВАТЕЛЬСКИЕ ЭКРАНЫ ==========

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
                    navController.navigate("city_detail/$cityId/$cityName")
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
                onBack = { navController.popBackStack() }
            )
        }

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

        // ========== ADMIN ЭКРАНЫ ==========

        composable("admin_dashboard") {
            LaunchedEffect(Unit) {
                if (!isAdmin) {
                    navController.navigate("main") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
            }

            AdminDashboardScreen(
                onNavigate = { route ->
                    when (route) {
                        "logout" -> {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> navController.navigate(route)
                    }
                }
            )
        }

        composable("admin_cities") {
            AdminCitiesScreen(
                viewModel = adminCityViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("admin_hotels") {
            AdminHotelsScreen(
                viewModel = adminHotelsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("admin_bookings") {
            AdminBookingsScreen(
                viewModel = adminBookingViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("admin_routes") {
            AdminRoutesScreen(
                viewModel = adminRouteViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("admin_safe_zones") {
            AdminSafeZonesScreen(
                viewModel = adminSafeZoneViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("admin_users") {
            AdminUsersScreen(
                viewModel = adminUserViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}