package com.trabajo.minitienda.data.dao

import androidx.room.*
import com.trabajo.minitienda.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert
    suspend fun insertPurchase(p: Purchase): Long

    @Insert
    suspend fun insertDetails(details: List<PurchaseDetail>)

    @Transaction
    @Query("SELECT * FROM compra ORDER BY fecha DESC")
    fun observePurchases(): Flow<List<PurchaseWithDetails>>
}
