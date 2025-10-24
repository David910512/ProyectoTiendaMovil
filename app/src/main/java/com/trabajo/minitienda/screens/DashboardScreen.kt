package com.trabajo.minitienda.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.components.PrimaryButton
import com.trabajo.minitienda.ui.theme.*

@Composable
fun DashboardScreen(navController: NavController) {
    PageLayout(
        title = "Dashboard",
        onMenuClick = { /* TODO: Open drawer */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsGrid(navController)
            AlertCard(navController)
            QuickActionsGrid(navController)
        }
    }
}

@Composable
private fun StatsGrid(navController: NavController) {
    val stats = listOf(
        StatItem("Ventas Hoy", "S/ 1,250.00", Icons.Default.ShoppingCart),
        StatItem("Productos", "145", Icons.Default.Inventory),
        StatItem("Compras Hoy", "S/ 850.00", Icons.Default.AddShoppingCart),
        StatItem("Ganancia", "S/ 400.00", Icons.Default.Payments)
    )

    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.forEach { stat ->
                    StatCard(stat)
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: StatItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = stat.icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stat.value,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stat.title,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )
    }
}

@Composable
private fun AlertCard(navController: NavController) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "¡Alerta de Stock Bajo!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WarningColor
                )
                Text(
                    text = "5 productos necesitan reabastecimiento",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            PrimaryButton(
                text = "Ver Productos",
                onClick = { navController.navigate("products") }
            )
        }
    }
}

@Composable
private fun QuickActionsGrid(navController: NavController) {
    val actions = listOf(
        ActionItem("Productos", "products", Icons.Default.Inventory),
        ActionItem("Ventas", "sales", Icons.Default.ShoppingCart),
        ActionItem("Compras", "purchases", Icons.Default.AddShoppingCart),
        ActionItem("Cierre", "cash_closure", Icons.Default.AccountBalance)
    )

    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                actions.forEach { action ->
                    ActionCard(action) {
                        navController.navigate(action.route)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(action: ActionItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = action.title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private data class StatItem(
    val title: String,
    val value: String,
    val icon: ImageVector
)

private data class ActionItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)