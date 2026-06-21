package com.example.carrentalapp.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.carrentalapp.localcache.TokenStorage
import com.example.carrentalapp.presentation.admin.AdminScreen
import com.example.carrentalapp.presentation.auth.LoginScreen
import com.example.carrentalapp.presentation.auth.RegisterScreen
import com.example.carrentalapp.presentation.booking.BookingScreen
import com.example.carrentalapp.presentation.catalog.CarDetailScreen
import com.example.carrentalapp.presentation.catalog.CatalogScreen
import com.example.carrentalapp.presentation.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController, context: Context) {
    val startDestination = if (TokenStorage.hasToken(context)) "catalog" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("catalog") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("catalog") {
            CatalogScreen(
                onCarClick = { car -> navController.navigate("car_detail/${car.id}") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable("car_detail/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            CarDetailScreen(
                carId = carId,
                onBookClick = { car -> navController.navigate("booking/${car.id}") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("booking/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            BookingScreen(
                carId = carId,
                onBookingConfirmed = {
                    navController.navigate("catalog") {
                        popUpTo("catalog") { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onAdminClick = { navController.navigate("admin") }
            )
        }

        composable("admin") {
            AdminScreen(onBack = { navController.popBackStack() })
        }
    }
}
