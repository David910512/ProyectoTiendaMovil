package com.trabajo.minitienda.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.data.dao.MovementDao
import com.trabajo.minitienda.data.dao.PurchaseDao
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.viewmodel.CashClosureViewModel
import java.util.Locale

@Composable
fun CashClosureScreen(
    navController: NavController,
    vm: CashClosureViewModel,
    onMenuClick: () -> Unit,
) {
    val salesTotal by vm.salesTotal.collectAsState()
    val purchasesTotal by vm.purchasesTotal.collectAsState()
    val netIncome by vm.netIncome.collectAsState()
    val salesCount by vm.salesCount.collectAsState()
    val avgTicket by vm.avgTicket.collectAsState()


    PageLayout(title = "Cierre de Caja", onMenuClick = onMenuClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ====== Tarjetas principales (mismo estilo que tus Accesos Rápidos) ======
            MetricCardRow(
                badge = "Hoy",
                title = "Total Ventas",
                subtitle = "Ingresos del día",
                value = money(salesTotal),
                trailingIcon = Icons.Default.TrendingUp
            )

            MetricCardRow(
                badge = "Hoy",
                title = "Total Compras",
                subtitle = "Egresos del día",
                value = money(purchasesTotal),
                trailingIcon = Icons.Default.TrendingDown
            )

            MetricCardRow(
                badge = "Hoy",
                title = "Ganancia Neta",
                subtitle = "${percent(netIncome, salesTotal)} margen",
                value = money(netIncome),
                trailingIcon = Icons.Default.AttachMoney
            )

            MetricCardRow(
                badge = "Hoy",
                title = "Transacciones",
                subtitle = "Ventas realizadas",
                value = salesCount.toString(),
                trailingIcon = Icons.Default.ShoppingCart
            )

            // ====== Resumen Detallado ======
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Text("Resumen Detallado", style = MaterialTheme.typography.titleMedium)

                    SummaryItem(
                        title = "Total de Ingresos",
                        hint = "Por ventas realizadas",
                        value = money(salesTotal),
                        positive = true
                    )

                    SummaryItem(
                        title = "Total de Egresos",
                        hint = "Por compras de inventario",
                        value = money(purchasesTotal),
                        positive = false
                    )

                    Divider()

                    SummaryBig(
                        title = "Ganancia Neta del Día",
                        hint = "Ingresos - Egresos",
                        value = money(netIncome)
                    )

                    SummarySmall("Ticket Promedio", money(avgTicket))
                    SummarySmall("Margen de Ganancia", percent(netIncome, salesTotal))
                }
            }

            // ====== Acciones ======
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    val context = LocalContext.current

                    Button(
                        onClick = {
                            vm.generateReport(context) { uri ->
                                if (uri != null) {
                                    // Aquí puedes abrir un share sheet para descargar/compartir el CSV
                                    val intent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        type = "text/csv"
                                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        android.content.Intent.createChooser(intent, "Compartir reporte")
                                    )
                                } else {
                                    Toast.makeText(context, "Error generando reporte", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Download, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Generar Reporte")
                    }
                    var isResetting by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = {
                            isResetting = true
                            vm.resetDay {
                                isResetting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (isResetting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PrimaryGreen,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reiniciando...")
                        } else {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reiniciar Día")
                        }
                    }

                    WarningNote()
                }
            }
        }
    }
}


/* ======= UI helpers, mismo patrón visual que tus tarjetas ======= */

@Composable
private fun MetricCardRow(
    badge: String,
    title: String,
    subtitle: String,
    value: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    AppCard(Modifier.fillMaxWidth().clickable { }) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(badge, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall)
                    Text(subtitle, style = MaterialTheme.typography.labelMedium, color = SecondaryText,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(
                    modifier = Modifier.size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(trailingIcon, null, tint = PrimaryGreen)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(Icons.Default.ArrowForward, null, tint = SecondaryText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun SummaryItem(title: String, hint: String, value: String, positive: Boolean) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (positive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        null, tint = if (positive) PrimaryGreen else MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(title, style = MaterialTheme.typography.titleSmall)
                }
                Text(hint, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
            }
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SummaryBig(title: String, hint: String, value: String) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(hint, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun SummarySmall(title: String, value: String) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
private fun WarningNote() {
    Surface(
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.06f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            "Importante: Antes de reiniciar el día, asegúrate de generar el reporte. " +
                    "Esta acción podría eliminar o archivar registros del día según tu implementación.",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun money(n: Double) = "S/ " + String.format(Locale.getDefault(), "%.2f", n)
private fun percent(net: Double, sales: Double): String =
    if (sales <= 0.0) "0.0% margen"
    else String.format(Locale.getDefault(),"%.1f%% margen", (net / sales) * 100.0)
