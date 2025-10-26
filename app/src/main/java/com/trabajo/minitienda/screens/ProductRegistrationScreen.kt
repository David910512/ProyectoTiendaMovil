package com.trabajo.minitienda.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.ui.components.*
import com.trabajo.minitienda.ui.theme.*
import com.trabajo.minitienda.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRegistrationScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
) {

    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    PageLayout(
        title = "Nuevo Producto",
        onMenuClick = { /* TODO: Open drawer */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Información del Producto",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // 🔹 Campos reales con estados
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Producto") },
                            placeholder = { Text("Ej: Arroz Extra") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        TextField(
                            value = codigo,
                            onValueChange = { codigo = it },
                            label = { Text("Código") },
                            placeholder = { Text("Ej: PRD001") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextField(
                                value = precio,
                                onValueChange = { precio = it },
                                label = { Text("Precio (S/)") },
                                placeholder = { Text("0.00") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            TextField(
                                value = stock,
                                onValueChange = { stock = it },
                                label = { Text("Stock Inicial") },
                                placeholder = { Text("0") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        TextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            placeholder = { Text("Describe el producto...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            singleLine = false
                        )
                    }
                }
            }
            ImageUploadSection()

            // 🔹 Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                PrimaryButton(
                    text = "Guardar Producto",
                    onClick = {
                        if (nombre.isNotBlank() && codigo.isNotBlank() && precio.isNotBlank() && stock.isNotBlank()) {
                            val product = Product(
                                name = nombre,
                                code = codigo,
                                price = precio.toDoubleOrNull() ?: 0.0,
                                stock = stock.toIntOrNull() ?: 0,
                                descripcion = descripcion
                            )
                            productViewModel.addProduct(product)
                            println(" Producto guardado correctamente: $product")
                            navController.navigateUp()
                        } else {
                            println("Por favor llena todos los campos.")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

            }
        }
    }
}

@Composable
private fun ImageUploadSection() {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(
                        width = 2.dp,
                        color = PrimaryGreen.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Haz clic para seleccionar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }

            Text(
                text = "Formatos soportados: JPG, PNG",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
    }
}
