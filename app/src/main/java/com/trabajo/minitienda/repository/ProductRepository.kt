package com.trabajo.minitienda.repository

import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.data.dao.ProductDao

class ProductRepository(private val productDao: ProductDao) {

    fun observeAllProducts() = productDao.observeAllProducts()

    suspend fun upsertByCode(p: Product) = productDao.upsertByCode(p)

    suspend fun deleteProduct(p: Product) = productDao.deleteProduct(p)

    suspend fun updateProduct(p: Product) = productDao.updateProduct(p)

    // (Opcional) utilidad de diagnóstico
    suspend fun countAll(): Int = productDao.countAll()
}