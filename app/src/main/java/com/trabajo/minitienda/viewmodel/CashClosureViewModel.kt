package com.trabajo.minitienda.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.dao.MovementDao
import com.trabajo.minitienda.data.dao.PurchaseDao
import com.trabajo.minitienda.data.dao.SaleDao
import com.trabajo.minitienda.data.model.MovimientoInventario
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CashClosureViewModel(
    private val saleDao: SaleDao,
    private val movementDao: MovementDao,
    private val purchaseDao: PurchaseDao,
) : ViewModel() {

    val salesTotal: StateFlow<Double> =
        saleDao.todaySalesTotal().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val salesCount: StateFlow<Int> =
        saleDao.todaySalesCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val purchasesTotal: StateFlow<Double> =
        purchaseDao.todayPurchasesTotal().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val netIncome: StateFlow<Double> =
        combine(salesTotal, purchasesTotal) { s, p -> s - p }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val avgTicket: StateFlow<Double> =
        combine(salesTotal, salesCount) { s, c -> if (c <= 0) 0.0 else s / c }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val movementsToday: StateFlow<List<MovimientoInventario>> =
        movementDao.todayMovements()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun resetDay(onDone: () -> Unit) = viewModelScope.launch {
        saleDao.deleteTodaySales()
        purchaseDao.deleteTodayPurchases()
        movementDao.deleteTodayMovements()
        onDone()
    }

    fun generateReport(context: Context, onDone: (Uri?) -> Unit) = viewModelScope.launch {
        try {
            val fileName = "reporte_cierre.csv"
            val file = File(context.cacheDir, fileName)

            val csvHeader = "Concepto,Valor\n"

            val csvData = buildString {
                append("Total Ventas,${salesTotal.value}\n")
                append("Total Compras,${purchasesTotal.value}\n")
                append("Ganancia Neta,${netIncome.value}\n")
                append("Cantidad de Ventas,${salesCount.value}\n")
                append("Ticket Promedio,${avgTicket.value}\n")
                append("Ticket Promedio,${movementsToday.value}\n")
            }

            FileOutputStream(file).use { it.write((csvHeader + csvData).toByteArray()) }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            onDone(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            onDone(null)
        }
    }
}
