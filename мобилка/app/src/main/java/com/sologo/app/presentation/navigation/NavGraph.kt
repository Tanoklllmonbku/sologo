// presentation/navigation/NavGraph.kt
package com.sologo.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import com.sologo.app.presentation.screens.*
import com.sologo.app.presentation.viewmodel.*

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

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Main
        composable(Screen.Home.route) {
            HomeScreen(
                onBookings = { navController.navigate(Screen.MyBookings.route) },
                onRoutes = { navController.navigate(Screen.RouteList.route) },
                onHotels = { navController.navigate(Screen.HotelList.route) },
                onCities = { navController.navigate(Screen.CityList.route) },
                onSafeZones = { navController.navigate(Screen.SafeZones.route) },
                onWeather = { /* TODO: реализовать погоду */ },
                onProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Hotels
        composable(Screen.HotelList.route) {
            HotelListScreen(
                hotelViewModel = hotelViewModel,
                onBack = { navController.popBackStack() },
                onHotelClick = { hotelId ->
                    navController.navigate(Screen.HotelDetail.passId(hotelId))
                }
            )
        }

        composable(
            route = Screen.HotelDetail.route,
            arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            HotelDetailScreen(
                hotelId = hotelId,
                hotelViewModel = hotelViewModel,
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onCreateBooking = { hotelId ->
                    navController.navigate(Screen.CreateBooking.passId(hotelId))
                }
            )
        }

        // Bookings
        composable(Screen.MyBookings.route) {
            MyBookingsScreen(
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(
            route = Screen.CreateBooking.route,
            arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            CreateBookingScreen(
                hotelId = hotelId,
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate(Screen.MyBookings.route)
                }
            )
        }

        // Cities
        composable(Screen.CityList.route) {
            CityListScreen(
                cityViewModel = cityViewModel,
                onBack = { navController.popBackStack() },
                onCityClick = { cityId ->
                    // TODO: показать отели в городе
                    navController.navigate(Screen.HotelList.route)
                }
            )
        }

        // Routes
        composable(Screen.RouteList.route) {
            RouteListScreen(
                routeViewModel = routeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Lost Report
        composable(Screen.LostReport.route) {
            LostReportScreen(
                lostViewModel = lostViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Safe Zones
        composable(Screen.SafeZones.route) {
            SafeZoneScreen(
                safeZoneViewModel = safeZoneViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}