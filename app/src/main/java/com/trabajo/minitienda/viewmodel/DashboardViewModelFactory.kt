package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trabajo.minitienda.data.dao.SaleDao

class DashboardViewModelFactory(
    private val saleDao: SaleDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(saleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}