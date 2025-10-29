package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.dao.SupplierDao
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.data.model.Supplier
import com.trabajo.minitienda.repository.PurchaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class PurchasesViewModel(
    private val repo: PurchaseRepository,
    private val productDao: ProductDao,
    private val supplierDao: SupplierDao
) : ViewModel() {

    // Proveedores para dropdown
    val suppliers: StateFlow<List<Supplier>> =
        supplierDao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Carrito de compra
    private val _cart = MutableStateFlow<List<PurchaseRepository.CartItem>>(emptyList())
    val cart: StateFlow<List<PurchaseRepository.CartItem>> = _cart.asStateFlow()

    val total: StateFlow<Double> =
        cart.map { it.sumOf { it.subtotal } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun addByCode(code: String, qty: Int, cost: Double) = viewModelScope.launch {
        val p: Product? = productDao.findByCode(code.trim().uppercase())
        if (p == null) {
            _events.tryEmit("Código no encontrado")
            return@launch
        }
        if (qty <= 0 || cost < 0) {
            _events.tryEmit("Cantidad/costo inválidos")
            return@launch
        }
        val current = _cart.value.toMutableList()
        val i = current.indexOfFirst { it.product.id == p.id && it.cost == cost }
        if (i >= 0) {
            val old = current[i]
            current[i] = old.copy(qty = old.qty + qty)
        } else {
            current += PurchaseRepository.CartItem(product = p, qty = qty, cost = cost)
        }
        _cart.value = current
    }

    fun remove(productId: Int, cost: Double) {
        _cart.update { it.filterNot { line -> line.product.id == productId && line.cost == cost } }
    }

    fun clearCart() { _cart.value = emptyList() }

    fun upsertSupplierByRuc(ruc: String, nombre: String, telefono: String?) = viewModelScope.launch {
        val r = ruc.trim()
        val n = nombre.trim()
        val t = telefono?.trim().takeUnless { it.isNullOrBlank() }

        if (r.isBlank() || n.isBlank()) {
            _events.tryEmit("RUC y nombre son obligatorios")
            return@launch
        }

        val id = supplierDao.insertIgnore(Supplier(ruc = r, nombre = n, telefono = t))
        if (id == -1L) {
            _events.tryEmit("El proveedor ya existe")
        } else {
            _events.tryEmit("Proveedor guardado")
        }
    }


    data class DraftLine(
        val nameOrCode: String,
        val qty: Int,
        val cost: Double
    )

    private val _draftCart = MutableStateFlow<List<DraftLine>>(emptyList())
    val draftCart: StateFlow<List<DraftLine>> = _draftCart.asStateFlow()

    val draftTotal: StateFlow<Double> =
        draftCart.map { lines -> lines.sumOf { it.qty * it.cost } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    fun addDraftLine(nameOrCode: String, qty: Int, cost: Double) {
        val n = nameOrCode.trim()
        if (n.isBlank() || qty <= 0 || cost < 0) {
            _events.tryEmit("Datos inválidos")
            return
        }
        val current = _draftCart.value.toMutableList()
        val idx = current.indexOfFirst { it.nameOrCode.equals(n, ignoreCase = true) && it.cost == cost }
        if (idx >= 0) {
            val old = current[idx]
            current[idx] = old.copy(qty = old.qty + qty)
        } else {
            current += DraftLine(n, qty, cost)
        }
        _draftCart.value = current
    }

    fun removeDraftLine(nameOrCode: String, cost: Double) {
        _draftCart.update { list -> list.filterNot { it.nameOrCode.equals(nameOrCode, true) && it.cost == cost } }
    }

    fun clearDraftCart() { _draftCart.value = emptyList() }

    // Materializa productos (si no existen) y registra compra
    fun finalizeDraftPurchase(supplierId: Int) = viewModelScope.launch {
        val drafts = _draftCart.value
        if (drafts.isEmpty()) {
            _events.tryEmit("Agrega al menos un producto")
            return@launch
        }

        try {
            val items = mutableListOf<PurchaseRepository.CartItem>()

            for (d in drafts) {
                val code = d.nameOrCode.trim().uppercase()
                val name = d.nameOrCode.trim()

                var p = productDao.findByCode(code)

                if (p == null) {
                    // Alta mínima (si ya existe por UNIQUE(code), insertará 0 filas y no falla)
                    productDao.insertIgnoreMinimal(
                        code = code,
                        name = name,
                        price = d.cost,
                        stock = 0,
                        desc = ""
                    )
                    // Asegura nombre/precio (idempotente)
                    productDao.updateByCode(
                        name = name,
                        price = d.cost,
                        stock = 0,
                        desc = "",
                        code = code
                    )
                    p = productDao.findByCode(code)
                    if (p == null) {
                        _events.tryEmit("No se pudo crear/leer el producto $name")
                        continue
                    }
                } else {
                    // Opcional: actualizar precio de referencia
                    productDao.updateByCode(
                        name = p.name,
                        price = d.cost,
                        stock = p.stock,
                        desc = p.descripcion,
                        code = p.code
                    )
                }

                items += PurchaseRepository.CartItem(
                    product = p,
                    qty = d.qty,
                    cost = d.cost
                )
            }

            if (items.isEmpty()) {
                _events.tryEmit("No hay ítems válidos para registrar")
                return@launch
            }

            val id = repo.makePurchase(supplierId = supplierId, lines = items)

            clearDraftCart()
            _events.tryEmit("Compra registrada (#$id)")
        } catch (e: Exception) {
            _events.tryEmit(e.message ?: "Error al registrar compra")
        }
    }


}

