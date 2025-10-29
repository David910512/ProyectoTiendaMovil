package com.trabajo.minitienda.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.ActividadSemanalChart
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.ui.theme.WarningColor
import com.trabajo.minitienda.viewmodel.SalesViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    salesViewModel: SalesViewModel,
    onMenuClick: () -> Unit 
) {
    PageLayout(
        title = "Panel de Control",
        onMenuClick = onMenuClick 
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardStatsGrid(productViewModel)
            ActividadSemanalChart(salesViewModel = salesViewModel)
            val products = productViewModel.products.collectAsState().value
            val lowStockItems = products
                .filter { it.stock < 10 }
                .sortedBy { it.stock }
                .map { it.name to it.stock }

            if (lowStockItems.isNotEmpty()) {
                DashboardLowStockBanner(
                    lowStockItems = lowStockItems,
                    onSeeProducts = { navController.navigate("products") }
                )
            }

            // --- ACCESOS RÁPIDOS ---
            DashboardQuickActionsGrid(
                onClick = { route -> navController.navigate(route) }
            )
        }
    }
}

/* =========================================================
 *                    MÉTRICAS (nuevo)
 * ========================================================= */

@Composable
private fun DashboardMetricsSection(
    productViewModel: ProductViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val products by productViewModel.products.collectAsState(initial = emptyList())
    val todaySales by dashboardViewModel.todaySalesCount.collectAsState(initial = 0)
    val todayUnits by dashboardViewModel.todayUnitsSold.collectAsState(initial = 0)
    val lastSale by dashboardViewModel.lastSaleBrief.collectAsState(initial = null)

    val metrics = listOf(
        MetricCard(
            badge = "Hoy",
            title = "Ventas",
            value = todaySales.toString(),
            helper = "Transacciones del día",
            icon = Icons.Default.ShoppingCart
        ),
        MetricCard(
            badge = "Hoy",
            title = "Unidades vendidas",
            value = todayUnits.toString(),
            helper = "Sumatoria de ítems",
            icon = Icons.Default.Inventory
        ),
        MetricCard(
            badge = "Inventario",
            title = "Total productos",
            value = products.size.toString(),
            helper = "Registrados en stock",
            icon = Icons.Default.Inventory2
        ),
        MetricCard(
            badge = "Última venta",
            title = "Monto",
            value = "S/ " + String.format("%.2f", lastSale?.total ?: 0.0),
            helper = lastSale?.let { "ID #${it.id} • ${fechaString(it.fecha)}" } ?: "Sin ventas aún",
            icon = Icons.Default.AttachMoney
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        metrics.forEach { m -> MetricCardView(m) }
    }
}

private data class MetricCard(
    val badge: String,
    val title: String,
    val value: String,
    val helper: String,
    val icon: ImageVector
)

@Composable
private fun MetricCardView(m: MetricCard) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // menos espacio
        ) {
            Text(
                m.badge,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(m.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        m.helper,
                        style = MaterialTheme.typography.labelSmall,  // antes labelMedium
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Iconito más compacto
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        m.icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Valor más chico
            Text(
                m.value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

private fun fechaString(millis: Long): String {
    if (millis == 0L) return "-"
    val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return df.format(Date(millis))
}

/* =========================================================
 *                BAJO STOCK (compacto y responsivo)
 * ========================================================= */

@Composable
private fun DashboardLowStockBanner(
    lowStockItems: List<Pair<String, Int>>,
    onSeeProducts: () -> Unit,
    maxItems: Int = 3
) {
    val bg = WarningColor.copy(alpha = 0.06f)
    val br = WarningColor.copy(alpha = 0.25f)

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, br, RoundedCornerShape(16.dp))
            .background(bg, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            // Cabecera nivelada y adaptable
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val isTiny = maxWidth < 340.dp
                val buttonLabel = if (isTiny) "Ver" else "Ver todos"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = WarningColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Productos con Bajo Stock",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                    }
                    TextButton(
                        onClick = onSeeProducts,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.heightIn(min = 32.dp)
                    ) {
                        Text(buttonLabel, style = MaterialTheme.typography.labelMedium, color = WarningColor)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                lowStockItems.take(maxItems).forEach { (name, qty) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$qty unidades",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningColor
                        )
                    }
                }
            }
        }
    }
}

/* =========================================================
 *              ACCESOS RÁPIDOS (compacto)
 * ========================================================= */

@Composable
private fun DashboardQuickActionsGrid(onClick: (String) -> Unit) {
    val actions = listOf(
        DashboardAction(
            badge = "Gestionar",
            title = "Productos",
            subtitle = "Ir a productos",
            route = "products",
            icon = Icons.Default.Inventory
        ),
        DashboardAction(
            badge = "Registrar",
            title = "Ventas",
            subtitle = "Ir a ventas",
            route = "sales",
            icon = Icons.Default.ShoppingCart
        ),
        DashboardAction(
            badge = "Registrar",
            title = "Compras",
            subtitle = "Ir a compras",
            route = "purchases",
            icon = Icons.Default.TrendingUp
        ),
        DashboardAction(
            badge = "Ver",
            title = "Cierre de Caja",
            subtitle = "Ir a cierre",
            route = "cash_closure",
            icon = Icons.Default.AttachMoney
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Acceso Rápido", style = MaterialTheme.typography.titleLarge)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            actions.chunked(1).forEach { row ->       // ← una tarjeta por fila (como tu ejemplo)
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { a ->
                        Box(Modifier.fillMaxWidth()) {
                            DashboardActionCard(a) { onClick(a.route) }
                        }
                    }
                }
            }
        }
    }
}

private data class DashboardAction(
    val badge: String,
    val title: String,
    val subtitle: String,
    val route: String,
    val icon: ImageVector
)


@Composable
private fun DashboardActionCard(
    action: DashboardAction,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp) // ↓ antes 14–16
        ) {
            Text(
                text = action.badge,
                style = MaterialTheme.typography.labelSmall, // ↓
                color = SecondaryText
            )

            Spacer(Modifier.height(4.dp)) // ↓

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        action.title,
                        style = MaterialTheme.typography.titleSmall // ↓
                    )
                    Text(
                        action.subtitle,
                        style = MaterialTheme.typography.labelMedium, // ↓
                        color = SecondaryText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp) // ↓ antes 40.dp
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp) // ↓
                    )
                }
            }

            Spacer(Modifier.height(4.dp)) // ↓

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = SecondaryText,
                    modifier = Modifier.size(18.dp) // ↓
                )
            }
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
