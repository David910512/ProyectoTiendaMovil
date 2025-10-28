package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.room.Room
import com.trabajo.minitienda.data.database.AppDatabase
import com.trabajo.minitienda.repository.CategoryRepository
import com.trabajo.minitienda.repository.ProductRepository
import com.trabajo.minitienda.repository.SaleRepository
import com.trabajo.minitienda.viewmodel.CategoryViewModel
import com.trabajo.minitienda.viewmodel.CategoryViewModelFactory
import com.trabajo.minitienda.viewmodel.ProductViewModel
import com.trabajo.minitienda.viewmodel.ProductViewModelFactory
import com.trabajo.minitienda.viewmodel.SalesViewModel
import com.trabajo.minitienda.viewmodel.SalesViewModelFactory
import kotlinx.coroutines.launch

// Data class para los ítems del menú
private data class DrawerItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val dbState by produceState<AppDatabase?>(initialValue = null, key1 = context) {
        value = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "minitienda.db"
        ).build()
    }
    val db = dbState

    if (db == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ViewModels
    val repository = remember { ProductRepository(db.productDao()) }
    val factory = remember { ProductViewModelFactory(repository) }
    val productViewModel: ProductViewModel = viewModel(factory = factory)

    val categoryRepository = remember { CategoryRepository(db.categoryDao()) }
    val categoryFactory = remember { CategoryViewModelFactory(categoryRepository) }
    val categoryViewModel: CategoryViewModel = viewModel(factory = categoryFactory)

    val saleRepository = remember { SaleRepository(db, db.saleDao(), db.productDao()) }
    val salesFactory = remember { SalesViewModelFactory(saleRepository, db.productDao()) }
    val salesViewModel: SalesViewModel = viewModel(factory = salesFactory)

    // --- Configuración del Menú Lateral (Navigation Drawer) ---

    // 1. Lista de ítems para el menú
    val drawerItems = listOf(
        DrawerItem("Panel", "dashboard", Icons.Default.Dashboard),
        DrawerItem("Productos", "products", Icons.Default.Inventory),
        DrawerItem("Ventas", "sales", Icons.Default.ShoppingCart),
        DrawerItem("Compras", "purchases", Icons.Default.AddShoppingCart),
        DrawerItem("Cierre", "cash_closure", Icons.Default.AccountBalance),
        DrawerItem("Perfil", "profile", Icons.Default.Person)
    )

    // 2. Estado para saber si el drawer está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 3. Scope para abrir/cerrar el drawer
    val scope = rememberCoroutineScope()
    // 4. Estado para saber qué ítem está seleccionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 5. Función para abrir el menú (la pasaremos a las pantallas)
    val openDrawer: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }

    // --- Contenido Principal (Drawer + NavHost) ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // El contenido del menú
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // --- Aquí van todas tus pantallas ---
        // Fíjate cómo AHORA SÍ pasamos `onMenuClick = openDrawer` a todas
        NavHost(navController = navController, startDestination = "dashboard") {
            
            composable("dashboard") { 
                DashboardScreen(navController, productViewModel, onMenuClick = openDrawer) 
            }
            
            composable("products") { 
                ProductListScreen(navController, productViewModel, onMenuClick = openDrawer) 
            }
            
            composable(
                route = "product_registration/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                val products by productViewModel.products
                    .collectAsState(initial = emptyList())
                val product = productId?.let { id -> products.find { it.id == id } }
                
                ProductRegistrationScreen(navController, productViewModel, categoryViewModel, product, onMenuClick = openDrawer)
            }
            
            composable("product_registration") {
                ProductRegistrationScreen(navController, productViewModel, categoryViewModel, onMenuClick = openDrawer)
            }
            
            composable("sales") { 
                SalesScreen(navController, salesViewModel, onMenuClick = openDrawer) 
            }
            
            composable("purchases") { 
                PurchasesScreen(navController, onMenuClick = openDrawer) 
            }
            
            composable("cash_closure") { 
                CashClosureScreen(navController, onMenuClick = openDrawer) // <--- Pantalla actualizada
            }
            
            composable("profile") { 
                ProfileScreen(navController, onMenuClick = openDrawer) 
            }
        }
    }
}