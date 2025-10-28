package com.trabajo.minitienda.data.dao

import androidx.room.*
import com.trabajo.minitienda.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertDetails(details: List<SaleDetail>)

    @Transaction
    @Query("SELECT * FROM venta ORDER BY fecha DESC")
    fun observeSales(): Flow<List<SaleWithDetails>>


}
