package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.dao.SaleDao
import com.trabajo.minitienda.data.model.SaleBrief
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val saleDao: SaleDao
) : ViewModel() {

    // Ventas del día (número de transacciones)
    val todaySalesCount: StateFlow<Int> =
        saleDao.todaySalesCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Unidades vendidas hoy (suma de cantidades)
    val todayUnitsSold: StateFlow<Int> =
        saleDao.todayUnitsSold()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Última venta (id, total, fecha)
    val lastSaleBrief: StateFlow<SaleBrief?> =
        saleDao.lastSaleBrief()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}