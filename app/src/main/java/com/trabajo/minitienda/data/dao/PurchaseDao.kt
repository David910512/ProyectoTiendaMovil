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

    // Total de egresos (compras) de HOY
    @Query("""
        SELECT COALESCE(SUM(total), 0.0)
        FROM compra
        WHERE DATE(fecha / 1000, 'unixepoch', 'localtime') = DATE('now','localtime')
    """)
    fun todayPurchasesTotal(): Flow<Double>

    // (Opcional) número de compras de HOY
    @Query("""
        SELECT COUNT(*)
        FROM compra
        WHERE DATE(fecha / 1000, 'unixepoch', 'localtime') = DATE('now','localtime')
    """)
    fun todayPurchasesCount(): Flow<Int>
}
