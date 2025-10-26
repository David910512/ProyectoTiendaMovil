package com.trabajo.minitienda.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.trabajo.minitienda.data.model.Product

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM producto")
    suspend fun getAllProducts(): List<Product>

    @Delete
    suspend fun deleteProduct(product: Product)
}