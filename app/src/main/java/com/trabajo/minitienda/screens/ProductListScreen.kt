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
import com.trabajo.minitienda.data.database.AppDbProvider
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.components.PrimaryButton
import com.trabajo.minitienda.ui.theme.ErrorColor
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.ui.theme.WarningColor
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.lazy.*
import com.trabajo.minitienda.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(
    navController: NavController,
    productViewModel: ProductViewModel
    ) {
    val ctx = LocalContext.current
    val dao = remember { AppDbProvider.get(ctx).productDao() }

    // 👇 Lee DIRECTO de Room (sin ViewModel)
    val products by dao.observeAllProducts().collectAsState(initial = emptyList())

    // 🔧 BLOQUE TEMPORAL DE SEMILLA (AQUÍ MISMO).
    // Si la tabla está vacía, insertamos 1 producto para confirmar que se pinta.
    LaunchedEffect(Unit) {
        val c = dao.countAll()
        if (c == 0) {
            dao.upsertByCode(
                Product(
                    name = "Producto de prueba",
                    code = "PRUEBA_${System.currentTimeMillis()}",
                    price = 1.0,
                    stock = 1,
                    descripcion = "Seed"
                )
            )
        }
    }

    PageLayout(
        title = "Productos",
        onMenuClick = { /* drawer si lo usas */ }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBar()
            ActionsRow(navController = navController, count = products.size)
            ProductList(
                products = products,
                onDelete = { /* dao.deleteProduct(it) si quieres eliminar */ },
                onEdit = { /* TODO: ir a edición */ }
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
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Divider()
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Precio: S/ ${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Stock: ${product.stock} unidades", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onEdit)  { Icon(Icons.Default.Edit,   contentDescription = "Editar",   tint = PrimaryGreen) }
                    IconButton(onClick = onDelete){ Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorColor) }
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
    Surface(color = bg, shape = MaterialTheme.shapes.small) {
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
