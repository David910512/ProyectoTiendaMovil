package com.trabajo.minitienda.data.dao

import androidx.room.*
import com.trabajo.minitienda.data.model.*
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertDetails(details: List<SaleDetail>)

    @Transaction
    @Query("SELECT * FROM venta ORDER BY fecha DESC")
    fun observeSales(): Flow<List<SaleWithDetails>>

    @Query("""
        SELECT 
            DATE(fecha / 1000, 'unixepoch') as saleDate, 
            SUM(total) as total  
        FROM venta                 
        WHERE fecha >= :sevenDaysAgoTimestamp 
        GROUP BY saleDate
        ORDER BY saleDate ASC
    """)
    fun getWeeklySalesSummary(sevenDaysAgoTimestamp: Long): Flow<List<DailySaleSummary>>

}
