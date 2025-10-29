package com.trabajo.minitienda.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trabajo.minitienda.viewmodel.SalesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ActividadSemanalChart(salesViewModel: SalesViewModel) {

    // 1. Obtener los datos de ventas del ViewModel
    val weeklySalesData by salesViewModel.weeklySales.collectAsState()

    // 2. Procesar los datos para el gráfico
    val chartData = remember(weeklySalesData) {

        // Mapa para acceso rápido: "2025-10-28" -> 7300.0
        val salesMap = weeklySalesData.associate { it.saleDate to it.total }

        val entries = mutableListOf<ChartEntry>()
        val days = mutableListOf<String>()

        // Formateadores de fecha
        val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Importante: Locale("es", "ES") para "lun.", "mar.", etc.
        val labelFormat = SimpleDateFormat("E", Locale("es", "ES"))

        // Empezamos 6 días atrás
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6)
        }

        // Generar los 7 días (desde 6 días atrás hasta hoy)
        repeat(7) { i ->
            val dateKey = dbFormat.format(calendar.time)
            val dayLabel = labelFormat.format(calendar.time)
                .replaceFirstChar { it.titlecase(Locale.getDefault()) }

            // Obtener el total de ventas para esa fecha, o 0.0 si no hubo
            val total = salesMap[dateKey] ?: 0.0

            entries.add(entryOf(i, total.toFloat())) // Añade la entrada del gráfico
            days.add(dayLabel) // Añade la etiqueta del día

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        Pair(entries, days)
    }

    // 3. Configurar el gráfico con los datos procesados
    val entries = chartData.first
    val days = chartData.second

    val chartEntryModelProducer = ChartEntryModelProducer(entries)
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        days.getOrNull(value.toInt()) ?: ""
    }

    // 4. El Composable del Gráfico
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Actividad Semanal",
                style = MaterialTheme.typography.titleMedium
            )

            ProvideChartStyle {
                Chart(
                    chart = columnChart(
                        columns = listOf(
                            ColumnChart.Column(
                                component = shapeComponent(
                                    shape = Shapes.roundedCornerShape(all = 4.dp),
                                    color = Color.Black // Color de tu imagen de ejemplo
                                )
                            )
                        )
                    ),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = rememberStartAxis(
                        title = null,
                        labelCount = 5,
                        valueFormatter = { value, _ -> "S/ ${value.toInt()}" } // Eje Y
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = null,
                        valueFormatter = bottomAxisValueFormatter // Eje X (Lun, Mar...)
                    ),
                    modifier = Modifier.height(200.dp)
                )
            }
        }
    }
}