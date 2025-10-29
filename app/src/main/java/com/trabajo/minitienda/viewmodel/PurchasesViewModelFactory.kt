package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.dao.SupplierDao
import com.trabajo.minitienda.repository.PurchaseRepository

class PurchasesViewModelFactory(
    private val repo: PurchaseRepository,
    private val productDao: ProductDao,
    private val supplierDao: SupplierDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchasesViewModel::class.java)) {
            return PurchasesViewModel(repo, productDao, supplierDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}