package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.model.DailySaleSummary
import com.trabajo.minitienda.repository.SaleRepository
import com.trabajo.minitienda.repository.SaleRepository.CartItem
import com.trabajo.minitienda.data.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class SalesViewModel(
    private val repo: SaleRepository,
    private val productDao: ProductDao
) : ViewModel() {

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    val total: StateFlow<Double> = cart
        .map { items -> items.sumOf { it.product.price * it.qty } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)


    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events

    fun addByCode(code: String, qty: Int) = viewModelScope.launch {
        val p = productDao.findByCode(code.trim().uppercase())
        if (p == null) {
            _events.tryEmit("Código no encontrado")
            return@launch
        }
        if (qty <= 0) {
            _events.tryEmit("Cantidad inválida")
            return@launch
        }
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == p.id }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(qty = old.qty + qty)
        } else {
            current += CartItem(p, qty)
        }
        _cart.value = current
    }

    fun addByCodeSafe(code: String, qty: Int) = viewModelScope.launch {
        // Validaciones para evitar crash
        if (code.isBlank()) {
            _events.tryEmit("Ingresa un código o selecciona un producto")
            return@launch
        }
        if (qty <= 0) {
            _events.tryEmit("Cantidad inválida (usa un número mayor a 0)")
            return@launch
        }
        try {
            // Reutiliza tu lógica actual
            addByCode(code.trim().uppercase(), qty)
            _events.tryEmit("Producto agregado")
        } catch (e: Exception) {
            _events.tryEmit("No se pudo agregar: ${e.message ?: "error desconocido"}")
        }
    }

    fun changeQty(productId: Int, qty: Int) {
        _cart.update { list -> list.map { if (it.product.id == productId) it.copy(qty = qty.coerceAtLeast(1)) else it } }
    }

    fun remove(productId: Int) {
        _cart.update { list -> list.filterNot { it.product.id == productId } }
    }

    fun clearCart() { _cart.value = emptyList() }

    fun finalizeSale() = viewModelScope.launch {
        try {
            val id = repo.makeSale(_cart.value)
            clearCart()
            _events.tryEmit("Venta realizada (#$id)")
        } catch (e: Exception) {
            _events.tryEmit(e.message ?: "Error al registrar la venta")
        }
    }
    private val _weeklySales = MutableStateFlow<List<DailySaleSummary>>(emptyList())
    val weeklySales: StateFlow<List<DailySaleSummary>> = _weeklySales.asStateFlow()

    init {
        loadWeeklySales()
    }

    private fun loadWeeklySales() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val sevenDaysAgoTimestamp = calendar.timeInMillis

        viewModelScope.launch {
            repo.getWeeklySalesSummary(sevenDaysAgoTimestamp)
                .catch { exception ->
                    _weeklySales.value = emptyList()
                }
                .collect { salesData ->
                    _weeklySales.value = salesData
                }
        }
    }
}
