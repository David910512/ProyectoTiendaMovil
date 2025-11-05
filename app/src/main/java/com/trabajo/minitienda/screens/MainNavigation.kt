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
import com.trabajo.minitienda.repository.PurchaseRepository
import com.trabajo.minitienda.repository.SaleRepository
import com.trabajo.minitienda.utils.ThemeManager
import com.trabajo.minitienda.viewmodel.CashClosureViewModel
import com.trabajo.minitienda.viewmodel.CashClosureViewModelFactory
import com.trabajo.minitienda.viewmodel.CategoryViewModel
import com.trabajo.minitienda.viewmodel.CategoryViewModelFactory
import com.trabajo.minitienda.viewmodel.DashboardViewModel
import com.trabajo.minitienda.viewmodel.DashboardViewModelFactory
import com.trabajo.minitienda.viewmodel.ProductViewModel
import com.trabajo.minitienda.viewmodel.ProductViewModelFactory
import com.trabajo.minitienda.viewmodel.PurchasesViewModel
import com.trabajo.minitienda.viewmodel.PurchasesViewModelFactory
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
fun MainNavigation(themeManager: ThemeManager) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Cargar DB una sola vez
    val dbState by produceState<AppDatabase?>(initialValue = null, key1 = context) {
        value = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "minitienda.db"
        ).build()
    }
    val db = dbState

    if (db == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ViewModels (recuerda envolver en remember para evitar recreaciones)
    val productViewModel: ProductViewModel = run {
        val repository = remember { ProductRepository(db.productDao()) }
        val factory = remember { ProductViewModelFactory(repository) }
        viewModel(factory = factory)
    }

    val categoryViewModel: CategoryViewModel = run {
        val repository = remember { CategoryRepository(db.categoryDao()) }
        val factory = remember { CategoryViewModelFactory(repository) }
        viewModel(factory = factory)
    }

    val salesViewModel: SalesViewModel = run {
        val repository = remember { SaleRepository(db, db.saleDao(), db.productDao()) }
        val factory = remember { SalesViewModelFactory(repository, db.productDao()) }
        viewModel(factory = factory)
    }

    // --- DashboardViewModel --
    val dashboardViewModel: DashboardViewModel = run {
        val factory = remember { DashboardViewModelFactory(db.saleDao()) }
        viewModel(factory = factory)
    }

    // --- PurchasesViewModel ---
    val purchasesViewModel: PurchasesViewModel = run {
        // El repo según tu constructor actual (db, purchaseDao, productDao)
        val repo = remember {
            PurchaseRepository(
                db           = db,
                purchaseDao  = db.purchaseDao(),
                productDao   = db.productDao()
            )
        }

        val factory = remember {
            PurchasesViewModelFactory(
                repo         = repo,
                productDao   = db.productDao(),
                supplierDao  = db.supplierDao()
            )
        }

        viewModel(factory = factory)
    }

    // --- Cash ---
    val cashClosureVM: CashClosureViewModel = run {
        val factory = remember {
            CashClosureViewModelFactory(
                saleDao = db.saleDao(),
                purchaseDao = db.purchaseDao(),
                movementDao = db.movementDao()
            )
        }
        viewModel(factory = factory)
    }


    // Drawer
    val drawerItems = listOf(
        DrawerItem("Panel", "dashboard", Icons.Default.Dashboard),
        DrawerItem("Productos", "products", Icons.Default.Inventory),
        DrawerItem("Ventas", "sales", Icons.Default.ShoppingCart),
        DrawerItem("Compras", "purchases", Icons.Default.AddShoppingCart),
        DrawerItem("Cierre", "cash_closure", Icons.Default.AccountBalance),
        DrawerItem("Perfil", "profile", Icons.Default.Person)
    )
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) { launchSingleTop = true }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "login") {

            composable("login") { LoginScreen(navController) }
            composable("dashboard") {
                // OJO: aquí va la **instancia** salesViewModel, no la clase
                DashboardScreen(
                    navController = navController,
                    productViewModel = productViewModel,
                    salesViewModel = salesViewModel,
                    dashboardViewModel = dashboardViewModel, // <-- nuevo
                    onMenuClick = openDrawer
                )
            }

            composable("products") {
                ProductListScreen(navController, productViewModel, onMenuClick = openDrawer)
            }

            composable(
                route = "product_registration/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                val products by productViewModel.products.collectAsState(initial = emptyList())
                val product = productId?.let { id -> products.find { it.id == id } }

                ProductRegistrationScreen(
                    navController, productViewModel, categoryViewModel, product,
                    onMenuClick = openDrawer
                )
            }

            composable("product_registration") {
                ProductRegistrationScreen(
                    navController, productViewModel, categoryViewModel,
                    onMenuClick = openDrawer
                )
            }

            composable("sales") {
                SalesScreen(navController, salesViewModel, onMenuClick = openDrawer)
            }

            composable("purchases") {
                PurchasesScreen(
                    navController = navController,
                    purchasesVM = purchasesViewModel,  // ← instancia, no la clase
                    onMenuClick = openDrawer
                )
            }

            composable("cash_closure") {
                CashClosureScreen(navController, vm = cashClosureVM, onMenuClick = openDrawer)
            }

            composable("profile") {
                ProfileScreen(
                    navController, onMenuClick = openDrawer, themeManager = themeManager )
            }
        }
    }
}
