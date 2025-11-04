package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.dao.MovementDao
import com.trabajo.minitienda.data.dao.PurchaseDao
import com.trabajo.minitienda.data.dao.SaleDao
import com.trabajo.minitienda.data.model.MovimientoInventario
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CashClosureViewModel(
    saleDao: SaleDao,
    purchaseDao: PurchaseDao,
    private val movementDao: MovementDao
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
        // Aquí NO borramos ventas/compras reales por seguridad.
        // Si quieres “reiniciar”, crea un endpoint/DAO específico y úsalo aquí.
        onDone()
    }

    fun generateReport(onDone: () -> Unit) = viewModelScope.launch {
        // Genera un PDF/CSV si quieres; por ahora solo callback.
        onDone()
    }
}
