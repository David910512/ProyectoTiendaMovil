package com.trabajo.minitienda.repository

import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.data.dao.ProductDao

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
}