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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.*
import com.trabajo.minitienda.ui.theme.*
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.data.mockProducts

@Composable
fun ProductListScreen(navController: NavController) {
    PageLayout(
        title = "Productos",
        onMenuClick = { /* TODO: Open drawer */ }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBar()
            ActionsRow(navController)
            ProductList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar() {
    // Static (non-interactive) search field placeholder
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar productos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        readOnly = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun ActionsRow(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${mockProducts.size} Productos",
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText
        )
        // Navigate to product registration (UI only)
        PrimaryButton(
            text = "Nuevo Producto",
            onClick = { navController.navigate("product_registration") }
        )
    }
}

@Composable
private fun ProductList() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mockProducts) { product ->
            ProductCard(product)
        }
    }
}

@Composable
private fun ProductCard(product: Product) {
    AppCard (
        modifier = Modifier.fillMaxWidth()
    ) {
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
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = product.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
                StockBadge(product.stock)
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Precio: S/ ${product.price}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Stock: ${product.stock} unidades",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = PrimaryGreen
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = ErrorColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StockBadge(stock: Int) {
    val (backgroundColor, textColor) = when {
        stock == 0 -> ErrorColor to Color.White
        stock < 10 -> WarningColor to Color.White
        else -> PrimaryGreen to Color.White
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = when {
                stock == 0 -> "Sin Stock"
                stock < 10 -> "Stock Bajo"
                else -> "Disponible"
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

// shared mock data used above