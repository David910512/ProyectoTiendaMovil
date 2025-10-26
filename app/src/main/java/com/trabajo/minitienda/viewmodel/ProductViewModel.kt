package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.repository.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // Lista reactiva desde Room (Flow -> StateFlow)
    val products: StateFlow<List<Product>> =
        repository.observeAllProducts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // Canal de eventos para la UI (snackbar)
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    // ---- Operaciones ----
    fun upsertProduct(product: Product) = viewModelScope.launch {
        try {
            repository.upsertByCode(product)   // <— usamos UPSERT
            _events.tryEmit("Producto guardado") // o “actualizado”
        } catch (e: Exception) {
            _events.tryEmit("Error al guardar: ${e.message ?: "desconocido"}")
        }
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.deleteProduct(product)
    }

    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.updateProduct(product)
    }
}
