package com.trabajo.minitienda.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("products") { ProductListScreen(navController) }
        composable("product_registration") { ProductRegistrationScreen(navController) }
        composable("sales") { SalesScreen(navController) }
        composable("purchases") { PurchasesScreen(navController) }
        composable("cash_closure") { CashClosureScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}