package com.trabajo.minitienda.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.components.PrimaryButton
import com.trabajo.minitienda.ui.theme.*

@Composable
fun CashClosureScreen(
    navController: NavController,
    onMenuClick: () -> Unit // <--- ¡AQUÍ ESTÁ EL CAMBIO!
) {
    // Static example value (no editable logic)
    val efectivoContado = "S/ 1,850.00"

    PageLayout(
        title = "Cierre de Caja",
        onMenuClick = onMenuClick // <--- Ahora esto funciona
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen del día (2/3)
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Estadísticas principales
                StatsGrid()

                // Transacciones del día
                TransactionsCard()
            }

            // Panel de cierre (1/3)
            ClosurePanel(
                efectivoContado = efectivoContado,
                onEfectivoContadoChange = { /* static: read-only */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "Ventas Totales",
            value = "S/ 2,450.00",
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Efectivo Esperado",
            value = "S/ 1,850.00",
            icon = Icons.Default.Payments,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Tarjetas",
            value = "S/ 600.00",
            icon = Icons.Default.CreditCard,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    AppCard (modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun TransactionsCard() {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Transacciones del Día",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionRow(
                    title = "Ventas en Efectivo",
                    transactions = 45,
                    amount = 1850.00
                )
                TransactionRow(
                    title = "Ventas con Tarjeta",
                    transactions = 15,
                    amount = 600.00
                )
                TransactionRow(
                    title = "Devoluciones",
                    transactions = 2,
                    amount = -150.00,
                    textColor = ErrorColor
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                TransactionRow(
                    title = "Total del Día",
                    transactions = 62,
                    amount = 2300.00,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(
    title: String,
    transactions: Int,
    amount: Double,
    textColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = style,
                color = textColor
            )
            if (style != MaterialTheme.typography.titleMedium) {
                Text(
                    text = "$transactions transacciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }
        Text(
            text = "S/ ${String.format("%.2f", amount)}",
            style = style,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClosurePanel(
    efectivoContado: String,
    onEfectivoContadoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Cierre de Caja",
                    style = MaterialTheme.typography.titleMedium
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Efectivo Contado",
                        style = MaterialTheme.typography.labelMedium
                    )
                    TextField(
                        value = efectivoContado,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("S/ 0.00") },
                        singleLine = true,
                        readOnly = true,
                    )
                }

                Surface(
                    color = WarningColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Diferencia",
                                style = MaterialTheme.typography.labelMedium,
                                color = WarningColor
                            )
                            Text(
                                text = "S/ -50.00",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarningColor
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningColor
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    text = "Realizar Cierre",
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}