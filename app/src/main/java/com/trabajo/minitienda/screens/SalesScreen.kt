package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.ErrorColor
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.viewmodel.SalesViewModel
import com.trabajo.minitienda.data.database.AppDbProvider
import com.trabajo.minitienda.data.model.Product
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    navController: NavController,
    vm: SalesViewModel,
    onMenuClick: () -> Unit = {}
) {
    val cart by vm.cart.collectAsState()
    val total by vm.total.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    // datos de productos (Room directo)
    val ctx = LocalContext.current
    val productDao = remember { AppDbProvider.get(ctx).productDao() }
    val products by productDao.observeAllProducts().collectAsState(initial = emptyList())

    // entrada
    var code by remember { mutableStateOf("") }
    var qtyTxt by remember { mutableStateOf("1") }

    // búsqueda/selector
    var productQuery by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val filtered: List<Product> = remember(productQuery, products) {
        if (productQuery.isBlank()) products.take(12)
        else products.filter {
            it.name.contains(productQuery, ignoreCase = true) ||
                    it.code.contains(productQuery, ignoreCase = true)
        }.take(12)
    }

    LaunchedEffect(Unit) {
        vm.events.collect { msg -> snackbar.showSnackbar(msg) }
    }

    PageLayout(title = "Registrar Venta",
        onMenuClick = onMenuClick
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 600.dp

            val leftPane: @Composable () -> Unit = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // ------------ Agregar Productos ------------
                    AppCard(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Agregar Productos", style = MaterialTheme.typography.titleMedium)

                            // Selector con búsqueda (nombre o código)
                            ExposedDropdownMenuBox(
                                expanded = menuExpanded,
                                onExpandedChange = { menuExpanded = !menuExpanded }
                            ) {
                                OutlinedTextField(
                                    value = productQuery,
                                    onValueChange = {
                                        productQuery = it
                                        menuExpanded = true
                                    },
                                    label = { Text("Producto") },
                                    placeholder = { Text("Selecciona o escribe…") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    singleLine = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) }
                                )

                                ExposedDropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    if (filtered.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("Sin resultados") },
                                            onClick = { menuExpanded = false }
                                        )
                                    } else {
                                        filtered.forEach { p ->
                                            DropdownMenuItem(
                                                text = { Text("${p.name}  —  ${p.code}") },
                                                onClick = {
                                                    // setea el código y cierra
                                                    code = p.code
                                                    productQuery = p.name
                                                    menuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Responsivo: en pantallas angostas, botón abajo
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                val isNarrow = maxWidth < 360.dp

                                if (isNarrow) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = code,
                                                onValueChange = { code = it },
                                                label = { Text("Código") },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                            )
                                            OutlinedTextField(
                                                value = qtyTxt,
                                                onValueChange = { qtyTxt = it },
                                                label = { Text("Cantidad") },
                                                modifier = Modifier.widthIn(min = 96.dp),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                val q = qtyTxt.toIntOrNull() ?: -1
                                                vm.addByCodeSafe(code, q)      // ← usa el método seguro
                                                // Limpieza solo si quieres mantenerla inmediata:
                                                code = ""
                                                productQuery = ""
                                                qtyTxt = "1"
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Agregar", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = code,
                                            onValueChange = { code = it },
                                            label = { Text("Código") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                        )
                                        OutlinedTextField(
                                            value = qtyTxt,
                                            onValueChange = { qtyTxt = it },
                                            label = { Text("Cantidad") },
                                            modifier = Modifier.widthIn(min = 96.dp),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                        Button(
                                            onClick = {
                                                val q = qtyTxt.toIntOrNull() ?: -1
                                                vm.addByCodeSafe(code, q)      // ← usa el método seguro
                                                // Limpieza solo si quieres mantenerla inmediata:
                                                code = ""
                                                productQuery = ""
                                                qtyTxt = "1"
                                            },
                                            modifier = Modifier
                                                .height(48.dp)
                                                .widthIn(min = 140.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Agregar", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ------------ Carrito ------------
                    AppCard(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Productos en la Venta", style = MaterialTheme.typography.titleMedium)

                            if (cart.isEmpty()) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("El carrito está vacío", color = SecondaryText)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxWidth()
                                        .heightIn(min = 0.dp, max = 320.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(cart, key = { it.product.id }) { line ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(Modifier.weight(1f)) {
                                                Text(line.product.name, style = MaterialTheme.typography.bodyLarge)
                                                Text(
                                                    "${line.qty} × S/ ${fmt(line.product.price)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = SecondaryText
                                                )
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("S/ ${fmt(line.product.price * line.qty)}")
                                                IconButton(onClick = { vm.remove(line.product.id) }) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Quitar",
                                                        tint = ErrorColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val rightPane: @Composable () -> Unit = {
                AppCard(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Resumen de Venta", style = MaterialTheme.typography.titleMedium)

                        val subtotal = cart.sumOf { it.product.price * it.qty }
                        val igv = subtotal * 0.18

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SummaryRow("Productos", cart.size.toString())
                            SummaryRow("Unidades", cart.sumOf { it.qty }.toString())
                            Divider()
                            SummaryRow("Subtotal", "S/ ${fmt(subtotal)}")
                            SummaryRow("IGV (18%)", "S/ ${fmt(igv)}")
                            Divider()
                            SummaryRow(
                                "Total",
                                "S/ ${fmt(total)}",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // Botones grandes y parejos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { vm.clearCart() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                enabled = cart.isNotEmpty()
                            ) { Text("Cancelar") }

                            Button(
                                onClick = { vm.finalizeSale() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                enabled = cart.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Registrar Venta", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(Modifier.weight(2f)) { leftPane() }
                    Column(Modifier.weight(1f)) { rightPane() }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                        .imePadding()
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    leftPane()
                    rightPane()
                    Spacer(Modifier.height(8.dp))
                }
            }

            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(12.dp))
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = style)
        Text(value, style = style)
    }
}

private fun fmt(n: Double) = String.format("%.2f", n)





