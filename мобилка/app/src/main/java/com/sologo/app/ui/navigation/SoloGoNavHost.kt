package com.sologo.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sologo.app.SoloGoViewModel
import com.sologo.app.ui.screens.BookingsScreen
import com.sologo.app.ui.screens.ChatScreen
import com.sologo.app.ui.screens.FaqScreen
import com.sologo.app.ui.screens.HomeScreen
import com.sologo.app.ui.screens.HotelDetailScreen
import com.sologo.app.ui.screens.HotelsScreen
import com.sologo.app.ui.screens.RoutesScreen
import com.sologo.app.ui.screens.SafeZonesScreen
import com.sologo.app.ui.screens.TranslatorScreen

@Composable
fun SoloGoNavHost(
    viewModel: SoloGoViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier,
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onBookings = { navController.navigate(NavRoutes.BOOKINGS) },
                onRoutes = { navController.navigate(NavRoutes.ROUTES) },
                onHotels = { navController.navigate(NavRoutes.HOTELS) },
                onFaq = { navController.navigate(NavRoutes.FAQ) },
                onTranslator = { navController.navigate(NavRoutes.TRANSLATOR) },
                onSafeZones = { navController.navigate(NavRoutes.SAFE_ZONES) },
                onChat = { navController.navigate(NavRoutes.CHAT) },
            )
        }
        composable(NavRoutes.ROUTES) {
            RoutesScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.BOOKINGS) {
            BookingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavRoutes.HOTELS) {
            HotelsScreen(
                onBack = { navController.popBackStack() },
                onHotel = { id -> navController.navigate(NavRoutes.hotelDetail(id)) },
            )
        }
        composable(
            NavRoutes.HOTEL_DETAIL,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
            ),
        ) { entry ->
            val id = entry.arguments?.getString("hotelId") ?: return@composable
            HotelDetailScreen(
                hotelId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavRoutes.FAQ) {
            FaqScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.TRANSLATOR) {
            TranslatorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavRoutes.SAFE_ZONES) {
            SafeZonesScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.CHAT) {
            ChatScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
