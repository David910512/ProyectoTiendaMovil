package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.data.CartItem
import com.trabajo.minitienda.data.mockCart
import com.trabajo.minitienda.data.mockProducts
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.components.PrimaryButton
import com.trabajo.minitienda.ui.theme.ErrorColor
import com.trabajo.minitienda.ui.theme.SecondaryText


@Composable
fun SalesScreen(navController: NavController) {
    // Static UI only: use first product and a static cart
    val selectedProduct = mockProducts.firstOrNull()
    val quantity = "1"
    val cartItems = mockCart

    PageLayout(
        title = "Nueva Venta",
        onMenuClick = { /* TODO: Open drawer */ }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de selección de productos (2/3)
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de producto y cantidad
                AppCard (modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Static product selector display (no interaction)
                        OutlinedTextField(
                            value = selectedProduct?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Producto") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = {},
                                label = { Text("Cantidad") },
                                modifier = Modifier.width(120.dp),
                                readOnly = true
                            )
                            
                            PrimaryButton(
                                text = "Agregar al Carrito",
                                onClick = {},
                                enabled = false
                            )
                        }
                    }
                }
                
                // Lista de productos en el carrito
                AppCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Productos en el Carrito",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (cartItems.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "El carrito está vacío",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SecondaryText
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(cartItems) { cartItem ->
                                    CartItemRow(
                                        item = cartItem,
                                        onRemove = { /* static: no-op */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Resumen de la venta (1/3)
            AppCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Resumen de Venta",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryRow("Subtotal", "S/ ${calculateSubtotal(cartItems)}")
                            SummaryRow("IGV (18%)", "S/ ${calculateTax(cartItems)}")
                            Divider()
                            SummaryRow(
                                "Total",
                                "S/ ${calculateTotal(cartItems)}",
                                MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PrimaryButton(
                            text = "Registrar Venta",
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            enabled = cartItems.isNotEmpty()
                        )
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

// Product selector removed (static UI only)

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${item.quantity} x S/ ${item.product.price}",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "S/ ${item.quantity * item.product.price}",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = ErrorColor
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = style)
        Text(text = value, style = style)
    }
}

private fun calculateSubtotal(items: List<CartItem>): String {
    return String.format("%.2f", items.sumOf { it.quantity * it.product.price })
}

private fun calculateTax(items: List<CartItem>): String {
    return String.format("%.2f", items.sumOf { it.quantity * it.product.price } * 0.18)
}

private fun calculateTotal(items: List<CartItem>): String {
    val subtotal = items.sumOf { it.quantity * it.product.price }
    return String.format("%.2f", subtotal * 1.18)
}
