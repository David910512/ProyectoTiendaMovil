package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.ErrorColor
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.viewmodel.PurchasesViewModel
import com.trabajo.minitienda.viewmodel.SupplierViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    PageLayout(title = "Registrar Compra", onMenuClick = onMenuClick) {
        Box(Modifier.fillMaxSize()) {
            // --- Scroll general ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ---------- Proveedor ----------
                AppCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Proveedor", style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { showSupplierManager = true }) { Text("Gestionar") }
                        }

                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = selectedSupplier?.nombre ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Selecciona un proveedor") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                suppliers.forEachIndexed { index, s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.nombre}  —  ${s.ruc}") },
                                        onClick = {
                                            selectedSupplierIdx = index
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ---------- Agregar productos ----------
                AppCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Agregar Productos", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = nameOrCode,
                            onValueChange = { nameOrCode = it },
                            label = { Text("Nombre o Código") },
                            leadingIcon = { Icon(Icons.Default.Inventory2, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = qtyTxt,
                                onValueChange = { qtyTxt = it },
                                label = { Text("Cantidad") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = costTxt,
                                onValueChange = { costTxt = it },
                                label = { Text("Costo (S/)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        Button(
                            onClick = {
                                val q = qtyTxt.toIntOrNull() ?: 0
                                val c = costTxt.replace(',', '.').toDoubleOrNull() ?: -1.0
                                if (nameOrCode.isBlank()) {
                                    scope.launch { snackbar.showSnackbar("Ingresa el nombre o código") }
                                    return@Button
                                }
                                if (q <= 0) {
                                    scope.launch { snackbar.showSnackbar("Cantidad inválida") }
                                    return@Button
                                }
                                if (c < 0.0) {
                                    scope.launch { snackbar.showSnackbar("Costo inválido") }
                                    return@Button
                                }
                                vm.addDraftLine(nameOrCode, q, c)
                                nameOrCode = ""
                                qtyTxt = "1"
                                costTxt = ""
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Agregar", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                // ---------- Productos añadidos ----------
                AppCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Productos en la Compra", style = MaterialTheme.typography.titleMedium)

                        if (cart.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) { Text("El carrito está vacío", color = SecondaryText) }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 220.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                cart.forEach { line ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                text = line.nameOrCode,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                text = "${line.qty} × S/ ${fmt(line.cost)} = S/ ${fmt(line.qty * line.cost)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SecondaryText
                                            )
                                        }
                                        IconButton(onClick = { vm.removeDraftLine(line.nameOrCode, line.cost) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
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



                // ---------- Resumen y acciones ----------
                AppCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SummaryRow("Productos", cart.size.toString())
                            SummaryRow("Unidades", cart.sumOf { it.qty }.toString())
                            Divider()
                            SummaryRow("Total", "S/ ${fmt(total)}", MaterialTheme.typography.titleMedium)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { vm.clearDraftCart() },
                                modifier = Modifier.weight(1f),
                                enabled = cart.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    val supplier = selectedSupplier
                                    if (supplier == null) {
                                        scope.launch { snackbar.showSnackbar("Selecciona un proveedor") }
                                        return@Button
                                    }
                                    if (cart.isEmpty()) {
                                        scope.launch { snackbar.showSnackbar("Agrega al menos un producto") }
                                        return@Button
                                    }
                                    vm.finalizeDraftPurchase(supplier.id)
                                    scope.launch { snackbar.showSnackbar("Compra registrada") }
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f),
                                enabled = cart.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Registrar compra")
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }

    // ---- Diálogo: gestionar proveedores ----
    if (showSupplierManager) {
        SupplierManagerDialog(
            onDismiss = { showSupplierManager = false },
            sVm = sVm
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = style)
        Text(value, style = style)
    }
}

@Composable
private fun SupplierManagerDialog(
    onDismiss: () -> Unit,
    sVm: SupplierViewModel
) {
    var ruc by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gestionar Proveedor") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = ruc, onValueChange = { ruc = it }, label = { Text("RUC") })
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                sVm.addSupplier(ruc, nombre, telefono.ifBlank { null })
                onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}


private fun fmt(n: Double) = String.format("%.2f", n)

