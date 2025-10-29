
package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.dao.SupplierDao
import com.trabajo.minitienda.data.model.Supplier
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SupplierViewModel(
    private val supplierDao: SupplierDao
) : ViewModel() {

    // Lista reactiva de proveedores
    val suppliers: StateFlow<List<Supplier>> =
        supplierDao.observeAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    /** Crear proveedor (si ya existe por RUC o nombre, insertIgnore devuelve -1) */
    fun addSupplier(ruc: String, nombre: String, telefono: String?) = viewModelScope.launch {
        val r = ruc.trim()
        val n = nombre.trim()
        val t = telefono?.trim().takeUnless { it.isNullOrBlank() }

        if (r.isBlank() || n.isBlank()) {
            _events.tryEmit("RUC y nombre son obligatorios")
            return@launch
        }

        val id = supplierDao.insertIgnore(Supplier(ruc = r, nombre = n, telefono = t))
        if (id == -1L) _events.tryEmit("El proveedor ya existe (RUC o nombre)")
        else _events.tryEmit("Proveedor agregado")
    }

    /** Renombrar por id (ya tienes el método rename en el DAO) */
    fun renameSupplier(id: Int, nuevoNombre: String) = viewModelScope.launch {
        val nuevo = nuevoNombre.trim()
        if (nuevo.isBlank()) return@launch
        supplierDao.rename(id, nuevo)
        _events.tryEmit("Proveedor actualizado")
    }

    /** Eliminar */
    fun deleteSupplier(supplier: Supplier) = viewModelScope.launch {
        supplierDao.delete(supplier)
        _events.tryEmit("Proveedor eliminado")
    }
}
