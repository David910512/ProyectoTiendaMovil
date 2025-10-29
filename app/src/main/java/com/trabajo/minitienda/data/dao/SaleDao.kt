package com.trabajo.minitienda.data.dao

import androidx.room.*
import com.trabajo.minitienda.data.model.*
import kotlinx.coroutines.flow.Flow
import com.trabajo.minitienda.data.model.SaleBrief


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
        SELECT COUNT(*) FROM venta
        WHERE date(fecha/1000,'unixepoch','localtime') = date('now','localtime')
    """)
    fun todaySalesCount(): kotlinx.coroutines.flow.Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(d.cantidad), 0)
        FROM detalle_venta d
        JOIN venta v ON v.id = d.sale_id
        WHERE date(v.fecha/1000,'unixepoch','localtime') = date('now','localtime')
    """)
    fun todayUnitsSold(): kotlinx.coroutines.flow.Flow<Int>

    @Query("""
        SELECT v.id AS id, v.total AS total, v.fecha AS fecha
        FROM venta v
        ORDER BY v.fecha DESC
        LIMIT 1
    """)
    fun lastSaleBrief(): kotlinx.coroutines.flow.Flow<SaleBrief?>

}
