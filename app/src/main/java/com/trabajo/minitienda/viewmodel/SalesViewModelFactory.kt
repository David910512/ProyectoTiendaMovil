package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.repository.SaleRepository

class SalesViewModelFactory(
    private val repo: SaleRepository,
    private val productDao: ProductDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesViewModel(repo, productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
