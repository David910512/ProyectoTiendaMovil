package com.trabajo.minitienda.repository

import com.trabajo.minitienda.data.database.AppDatabase
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.dao.PurchaseDao
import com.trabajo.minitienda.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PurchaseRepository(
    private val db: AppDatabase,
    private val purchaseDao: PurchaseDao,
    private val productDao: ProductDao
) {
    data class CartItem(val product: Product, val qty: Int, val cost: Double) {
        val subtotal: Double = qty * cost
    }

    suspend fun makePurchase(supplierId: Int, lines: List<CartItem>): Long = withContext(Dispatchers.IO) {
        require(lines.isNotEmpty()) { "El carrito está vacío" }

        val total = lines.sumOf { it.subtotal }
        val purchaseId = purchaseDao.insertPurchase(
            Purchase(proveedorId = supplierId, total = total)
        )

        val details = lines.map {
            PurchaseDetail(
                purchaseId = purchaseId,
                productId = it.product.id,
                cantidad = it.qty,
                costoUnit = it.cost
            )
        }
        purchaseDao.insertDetails(details)

        // Aumentar stock
        lines.forEach { line ->
            val p = line.product.copy(stock = line.product.stock + line.qty)
            productDao.updateProduct(p)
        }

        purchaseId
    }
}
