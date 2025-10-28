package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.*
import com.trabajo.minitienda.ui.theme.*
import com.trabajo.minitienda.data.mockPurchases
import com.trabajo.minitienda.data.PurchaseStatus
import com.trabajo.minitienda.data.Purchase

@Composable
fun PurchasesScreen(navController: NavController, onMenuClick: () -> Unit) {
    PageLayout(
        title = "Compras",
        onMenuClick = OnMenuClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsRow()
            PurchaseFilters()
            PurchaseList()
        }
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppCard(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Compras del Mes",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "S/ 12,450.00",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "84 compras realizadas",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }
        
        AppCard(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Proveedores Activos",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "12",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "5 compras pendientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningColor
                )
            }
        }
    }
}

@Composable
private fun PurchaseFilters() {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filtros a la izquierda
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { /* TODO */ },
                    label = { Text("Este Mes") }
                )
                FilterChip(
                    selected = false,
                    onClick = { /* TODO */ },
                    label = { Text("Por Proveedor") }
                )
            }
            
            // Botón de nueva compra
            PrimaryButton(
                text = "Nueva Compra",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun PurchaseList() {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Últimas Compras",
                style = MaterialTheme.typography.titleMedium
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockPurchases) { purchase ->
                    PurchaseItem(purchase)
                }
            }
        }
    }
}

@Composable
private fun PurchaseItem(purchase: Purchase) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = purchase.supplier,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Factura: ${purchase.invoiceNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
            
            Text(
                text = purchase.date,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${purchase.items} productos",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/ ${String.format("%.2f", purchase.total)}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Ver detalles",
                        tint = PrimaryGreen
                    )
                }
            }
        }
        
        if (purchase.status == PurchaseStatus.PENDING) {
            Surface(
                color = WarningColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Pendiente de pago",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningColor
                        )
                    }
                    Text(
                        text = "Vence: ${purchase.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarningColor
                    )
                }
            }
        }
        
        if (purchase != mockPurchases.last()) {
            Divider()
        }
    }
}

// uses shared mockPurchases from data.MockData