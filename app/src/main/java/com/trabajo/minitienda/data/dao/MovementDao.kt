package com.trabajo.minitienda.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.trabajo.minitienda.data.model.MovimientoInventario
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {
    @Insert
    suspend fun insertAll(items: List<MovimientoInventario>)

    @Insert
    suspend fun insert(item: MovimientoInventario)

    // Listar movimientos del día (útil para un detalle)
    @Query("""
        SELECT * FROM movimiento_inventario
        WHERE date(fecha/1000,'unixepoch') = date('now','unixepoch')
        ORDER BY fecha DESC
    """)
    fun todayMovements(): Flow<List<MovimientoInventario>>
}
