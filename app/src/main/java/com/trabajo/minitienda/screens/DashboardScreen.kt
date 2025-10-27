package com.trabajo.minitienda.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.trabajo.minitienda.viewmodel.ProductViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.ui.theme.WarningColor

@Composable
fun DashboardScreen(
    navController: NavController,
    productViewModel: ProductViewModel
) {
    PageLayout(
        title = "Panel de Control",
        onMenuClick = { /* abrir drawer */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardStatsGrid(productViewModel)

            val products = productViewModel.products.collectAsState().value
            val lowStockItems = products
                .filter { it.stock < 10 } // productos con menos de 10 unidades
                .map { it.name to it.stock }
            
            if (lowStockItems.isNotEmpty()) {
                DashboardLowStockBanner(
                    lowStockItems = lowStockItems,
                    onSeeProducts = { navController.navigate("products") }
                )
            }

            DashboardQuickActionsGrid(
                onClick = { route -> navController.navigate(route) }
            )
        }
    }
}

/* ------------------------- MÉTRICAS ------------------------- */

@Composable
private fun DashboardStatsGrid(productViewModel: ProductViewModel) {
    val products = productViewModel.products.collectAsState()
    val stats = listOf(
        DashboardStat("Ventas del Día", "S/ 6,350", "+12% desde ayer", Icons.Default.AttachMoney),
        DashboardStat("Productos Vendidos", "47", "En 23 transacciones", Icons.Default.ShoppingCart),
        DashboardStat("Total Productos", products.value.size.toString(), "En inventario", Icons.Default.Inventory),
        DashboardStat("Ganancia Neta", "S/ 4,230", "Hoy", Icons.Default.TrendingUp)
    )

    // Pintamos en filas de 2 tarjetas
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        stats.chunked(2).forEach { rowItems ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { s ->
                    Box(Modifier.weight(1f)) { DashboardStatCard(s) }
                }
                // Si la fila tiene solo 1, rellenamos el espacio para que no se estire
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DashboardStatCard(stat: DashboardStat) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(text = stat.value, style = MaterialTheme.typography.headlineMedium)
            Text(text = stat.helper, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
        }
    }
}

/* ---------------------- ALERTA BAJO STOCK ---------------------- */

@Composable
private fun DashboardLowStockBanner(
    lowStockItems: List<Pair<String, Int>>,
    onSeeProducts: () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Productos con Bajo Stock",
                    style = MaterialTheme.typography.titleMedium,
                    color = WarningColor
                )
                TextButton(onClick = onSeeProducts) {
                    Text("Ver productos")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lowStockItems.forEach { (name, qty) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$qty unidades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarningColor
                        )
                    }
                }
            }
        }
    }
}

/* ------------------------ ACCESOS RÁPIDOS ------------------------ */

@Composable
private fun DashboardQuickActionsGrid(onClick: (String) -> Unit) {
    val actions = listOf(
        DashboardAction("Productos", "products", Icons.Default.Inventory),
        DashboardAction("Ventas", "sales", Icons.Default.ShoppingCart),
        DashboardAction("Compras", "purchases", Icons.Default.AddShoppingCart),
        DashboardAction("Cierre", "cash_closure", Icons.Default.AccountBalance)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Acceso Rápido", style = MaterialTheme.typography.titleLarge)

        // También en filas de 2
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            actions.chunked(2).forEach { rowItems ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { a ->
                        Box(Modifier.weight(1f)) {
                            DashboardActionCard(a) { onClick(a.route) }
                        }
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DashboardActionCard(action: DashboardAction, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = action.icon, contentDescription = null, tint = PrimaryGreen)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(action.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Ir a ${action.title.lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = SecondaryText)
        }
    }
}

/* ------------------------- MODELOS LOCALES ------------------------- */

private data class DashboardStat(
    val title: String,
    val value: String,
    val helper: String,
    val icon: ImageVector
)

private data class DashboardAction(
    val title: String,
    val route: String,
    val icon: ImageVector
)
