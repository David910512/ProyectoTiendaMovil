package com.trabajo.minitienda.repository


import androidx.room.withTransaction
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.dao.SaleDao
import com.trabajo.minitienda.data.database.AppDatabase
import com.trabajo.minitienda.data.model.*
import kotlinx.coroutines.flow.Flow

class SaleRepository(
    private val db: AppDatabase,
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) {
    data class CartItem(val product: Product, val qty: Int) {
        val subtotal: Double get() = product.price * qty
    }

    fun getWeeklySalesSummary(timestamp: Long): Flow<List<DailySaleSummary>> {
        return db.saleDao().getWeeklySalesSummary(timestamp)
    }

    fun observeSales() = saleDao.observeSales()

    /** Inserta la venta + items y descuenta stock, todo en una transacción. */
    suspend fun makeSale(cart: List<CartItem>): Long {
        require(cart.isNotEmpty()) { "Carrito vacío" }

        return db.withTransaction {
            // Descontar stock de cada producto (falla si no alcanza)
            for (item in cart) {
                val updated = productDao.decreaseStock(item.product.id, item.qty)
                if (updated == 0) {
                    throw IllegalStateException("Stock insuficiente para ${item.product.name}")
                }
            }

            val total = cart.sumOf { it.subtotal }
            val saleId = saleDao.insertSale(Sale(total = total))
            val details = cart.map {
                SaleDetail(
                    saleId = saleId,
                    productId = it.product.id,
                    cantidad = it.qty,
                    precioUnit = it.product.price,
                    subtotal = it.subtotal
                )
            }
            saleDao.insertDetails(details)
            saleId
        }
    }
}