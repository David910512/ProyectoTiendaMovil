package com.trabajo.minitienda.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.trabajo.minitienda.data.database.AppDatabase
import com.trabajo.minitienda.repository.CategoryRepository
import com.trabajo.minitienda.repository.ProductRepository
import com.trabajo.minitienda.viewmodel.CategoryViewModel
import com.trabajo.minitienda.viewmodel.CategoryViewModelFactory
import com.trabajo.minitienda.viewmodel.ProductViewModel
import com.trabajo.minitienda.viewmodel.ProductViewModelFactory


@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // Create DB and repository once per composition but build DB off the main thread
    val context = LocalContext.current

    // Build the database asynchronously to avoid blocking composition
    val dbState by produceState<AppDatabase?>(initialValue = null, key1 = context) {
        value = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "minitienda.db"
        ).build()
    }

    val db = dbState

    // If DB isn't ready yet, show a small loading indicator
    if (db == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val repository = remember { ProductRepository(db.productDao()) }
    val factory = remember { ProductViewModelFactory(repository) }
    // Create a single ViewModel instance scoped to the activity/composition
    val productViewModel: ProductViewModel = viewModel(factory = factory)

    //repo + factory + VM de categoría
    val categoryRepository = remember { CategoryRepository(db.categoryDao()) }
    val categoryFactory = remember { CategoryViewModelFactory(categoryRepository) }
    val categoryViewModel: CategoryViewModel = viewModel(factory = categoryFactory)


    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController, productViewModel) }
        composable("products") { ProductListScreen(navController, productViewModel) }
        composable(
            route = "product_registration/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            // Find product if ID is provided
            val product = if (productId != null) {
                productViewModel.products.value.find { it.id == productId }
            } else null
            ProductRegistrationScreen(navController, productViewModel, categoryViewModel, product)
        }
        composable("product_registration") {
            ProductRegistrationScreen(navController, productViewModel, categoryViewModel)
        }
        composable("sales") { SalesScreen(navController) }
        composable("purchases") { PurchasesScreen(navController) }
        composable("cash_closure") { CashClosureScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}