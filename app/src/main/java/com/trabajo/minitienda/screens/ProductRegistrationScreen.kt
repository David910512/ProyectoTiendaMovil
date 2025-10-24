package com.trabajo.minitienda.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun ProductRegistrationScreen(navController: NavController) {
    // Static example values (no editable logic)
    val nombre = "Arroz Extra"
    val codigo = "PRD001"
    val precio = "4.50"
    val stock = "50"
    val descripcion = "Arroz de calidad extra, presentación 1kg."

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
            ImageUploadSection()
            
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
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FormField(
                            value = nombre,
                            onValueChange = {},
                            label = "Nombre del Producto",
                            placeholder = "Ej: Arroz Extra",
                            readOnly = true
                        )
                        
                        FormField(
                            value = codigo,
                            onValueChange = {},
                            label = "Código",
                            placeholder = "Ej: PRD001",
                            readOnly = true
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FormField(
                                value = precio,
                                onValueChange = {},
                                label = "Precio (S/)",
                                placeholder = "0.00",
                                modifier = Modifier.weight(1f),
                                readOnly = true
                            )
                            
                            FormField(
                                value = stock,
                                onValueChange = {},
                                label = "Stock Inicial",
                                placeholder = "0",
                                modifier = Modifier.weight(1f),
                                readOnly = true
                            )
                        }
                        
                        FormField(
                            value = descripcion,
                            onValueChange = {},
                            label = "Descripción",
                            placeholder = "Describe el producto...",
                            singleLine = false,
                            modifier = Modifier.height(100.dp),
                            readOnly = true
                        )
                    }
                }
            }
            
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
                    onClick = { /* TODO */ },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            readOnly = readOnly,
            shape = MaterialTheme.shapes.medium,
            singleLine = singleLine
        )
    }
}