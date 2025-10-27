package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.components.PrimaryButton
import com.trabajo.minitienda.ui.theme.ErrorColor
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.ui.theme.WarningColor
import com.trabajo.minitienda.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(
    navController: NavController,
    productViewModel: ProductViewModel
) {
    val products by productViewModel.products.collectAsState()

    PageLayout(
        title = "Productos",
        onMenuClick = { /* TODO */ }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBar()
            ActionsRow(navController = navController, count = products.size)
            ProductList(
                products = products,
                onDelete = { product -> productViewModel.deleteProduct(product) },
                onEdit = { product -> navController.navigate("product_registration/${product.id}") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar productos...") },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        readOnly = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun ActionsRow(navController: NavController, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$count Productos",
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText
        )
        PrimaryButton(
            text = "Nuevo Producto",
            onClick = { navController.navigate("product_registration") }
        )
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    onDelete: (Product) -> Unit,
    onEdit: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay productos aún")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onDelete = { onDelete(product) },
                    onEdit = { onEdit(product) }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(product.name, style = MaterialTheme.typography.titleMedium)
                    Text(product.code, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                StockBadge(product.stock)
            }

            HorizontalDivider()

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Precio: S/ ${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Stock: ${product.stock} unidades", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = PrimaryGreen
                        )
                    }
                    IconButton(onClick = onDelete) {
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
    val (bg, fg) = when {
        stock == 0 -> ErrorColor to Color.White
        stock < 10 -> WarningColor to Color.White
        else -> PrimaryGreen to Color.White
    }
    Surface(
        color = bg,
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
            color = fg
        )
    }
}

