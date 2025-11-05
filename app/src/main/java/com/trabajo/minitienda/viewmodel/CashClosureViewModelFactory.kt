package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trabajo.minitienda.data.dao.MovementDao
import com.trabajo.minitienda.data.dao.PurchaseDao
import com.trabajo.minitienda.data.dao.SaleDao

class CashClosureViewModelFactory(
    private val saleDao: SaleDao,
    private val purchaseDao: PurchaseDao,
    private val movementDao: MovementDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashClosureViewModel::class.java)) {
            return CashClosureViewModel(saleDao, movementDao, purchaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
