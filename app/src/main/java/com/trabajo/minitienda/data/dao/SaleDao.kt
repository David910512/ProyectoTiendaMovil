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

    // Resumen semanal (por día, en zona local)
    @Query("""
        SELECT 
            DATE(fecha / 1000, 'unixepoch', 'localtime') AS saleDate, 
            SUM(total) AS total  
        FROM venta                 
        WHERE fecha >= :sevenDaysAgoTimestamp 
        GROUP BY saleDate
        ORDER BY saleDate ASC
    """)
    fun getWeeklySalesSummary(sevenDaysAgoTimestamp: Long): Flow<List<DailySaleSummary>>

    // Transacciones de HOY (zona local)
    @Query("""
        SELECT COUNT(*) 
        FROM venta
        WHERE DATE(fecha / 1000, 'unixepoch', 'localtime') = DATE('now','localtime')
    """)
    fun todaySalesCount(): Flow<Int>

    // Unidades vendidas HOY (zona local)
    @Query("""
        SELECT COALESCE(SUM(d.cantidad), 0)
        FROM detalle_venta d
        INNER JOIN venta v ON v.id = d.sale_id
        WHERE DATE(v.fecha / 1000, 'unixepoch', 'localtime') = DATE('now','localtime')
    """)
    fun todayUnitsSold(): Flow<Int>

    // Última venta
    @Query("""
        SELECT v.id AS id, v.total AS total, v.fecha AS fecha
        FROM venta v
        ORDER BY v.fecha DESC
        LIMIT 1
    """)
    fun lastSaleBrief(): Flow<SaleBrief?>
}

