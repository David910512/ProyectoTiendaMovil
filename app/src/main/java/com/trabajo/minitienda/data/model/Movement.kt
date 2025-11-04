package com.trabajo.minitienda.data.model

import androidx.room.*

@Entity(
    tableName = "movimiento_inventario",
    indices = [Index("producto_id")],
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["producto_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovimientoInventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "producto_id") val productoId: Int,
    val fecha: Long = System.currentTimeMillis(),
    val tipo: String,                 // "AJUSTE" | "COMPRA" | "VENTA"
    val cantidad: Int
)
