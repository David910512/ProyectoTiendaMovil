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
import com.trabajo.minitienda.data.model.SaleBrief
import com.trabajo.minitienda.ui.components.ActividadSemanalChart
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.ui.theme.WarningColor
import com.trabajo.minitienda.viewmodel.DashboardViewModel
import com.trabajo.minitienda.viewmodel.ProductViewModel
import com.trabajo.minitienda.viewmodel.SalesViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    salesViewModel: SalesViewModel,
    dashboardViewModel: DashboardViewModel,
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
            // --- Métricas del encabezado ---
            val todaySales = dashboardViewModel.todaySalesCount.collectAsState(initial = 0).value
            val todayUnits = dashboardViewModel.todayUnitsSold.collectAsState(initial = 0).value
            val lastSale  = dashboardViewModel.lastSaleBrief.collectAsState(initial = null).value

            // Si quieres que "Total productos" sea real, toma el size desde aquí:
            val products = productViewModel.products.collectAsState(initial = emptyList()).value
            DashboardStatsGrid(
                todaySales = todaySales,
                todayUnits = todayUnits,
                lastSale   = lastSale,
                totalProducts = products.size
            )

            // --- Gráfico semanal ---
            ActividadSemanalChart(salesViewModel = salesViewModel)

            // --- Banner de bajo stock ---
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

            // --- Accesos rápidos ---
            DashboardQuickActionsGrid { route -> navController.navigate(route) }
        }
    }
}

/* =========================================================
 *                        MÉTRICAS
 * ========================================================= */

private data class DashboardMetric(
    val badge: String?,
    val title: String,
    val helper: String?,
    val value: String,
    val icon: ImageVector
)

@Composable
private fun DashboardMetricCard(m: DashboardMetric) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            m.badge?.let { badge ->
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
                Spacer(Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = m.title, style = MaterialTheme.typography.titleSmall)
                    m.helper?.let { helper ->
                        Text(
                            text = helper,
                            style = MaterialTheme.typography.labelMedium,
                            color = SecondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = m.icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = m.value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun DashboardStatsGrid(
    todaySales: Int,
    todayUnits: Int,
    lastSale: SaleBrief?,
    totalProducts: Int
) {
    val metrics = listOf(
        DashboardMetric(
            badge = "Hoy",
            title = "Ventas",
            helper = "Transacciones del día",
            value = todaySales.toString(),
            icon = Icons.Default.ShoppingCart
        ),
        DashboardMetric(
            badge = "Hoy",
            title = "Unidades vendidas",
            helper = "Sumatoria de ítems",
            value = todayUnits.toString(),
            icon = Icons.Default.Inventory
        ),
        DashboardMetric(
            badge = "Inventario",
            title = "Total productos",
            helper = "Registrados en stock",
            value = totalProducts.toString(),
            icon = Icons.Default.Inventory2
        ),
        DashboardMetric(
            badge = "Última venta",
            title = "Monto",
            helper = lastSale?.let { "ID #${it.id} • ${fechaString(it.fecha)}" } ?: "Sin ventas aún",
            value = "S/ " + String.format("%.2f", lastSale?.total ?: 0.0),
            icon = Icons.Default.AttachMoney
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        metrics.forEach { DashboardMetricCard(it) }
    }
}

private fun fechaString(millis: Long): String {
    if (millis == 0L) return "—"
    val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return df.format(java.util.Date(millis))
}

/* =========================================================
 *                 BAJO STOCK (compacto)
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
 *               ACCESOS RÁPIDOS (igual que antes)
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
            actions.chunked(1).forEach { row ->
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
                .padding(12.dp)
        ) {
            action.badge?.let { badge ->
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
                Spacer(Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = action.title, style = MaterialTheme.typography.titleSmall)
                    action.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = SecondaryText
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = SecondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/* ------------------------- MODELOS LOCALES ------------------------- */

private data class DashboardStat( // (si ya no lo usas, puedes borrarlo)
    val title: String,
    val value: String,
    val helper: String,
    val icon: ImageVector
)

data class DashboardAction(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val badge: String? = null,
    val subtitle: String? = null
)
