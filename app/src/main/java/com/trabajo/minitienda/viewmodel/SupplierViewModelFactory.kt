
package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trabajo.minitienda.data.dao.SupplierDao

class SupplierViewModelFactory(
    private val dao: SupplierDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupplierViewModel::class.java)) {
            return SupplierViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
