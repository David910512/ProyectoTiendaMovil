package com.trabajo.minitienda.data.model

import androidx.room.*

@Entity(tableName = "venta")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: Long = System.currentTimeMillis(),
    val total: Double
)

@Entity(
    tableName = "detalle_venta",
    primaryKeys = ["sale_id", "product_id"],
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("product_id")]
)
data class SaleDetail(
    @ColumnInfo(name = "sale_id") val saleId: Long,
    @ColumnInfo(name = "product_id") val productId: Int,
    val cantidad: Int,
    @ColumnInfo(name = "precio_unit") val precioUnit: Double,
    val subtotal: Double
)

data class SaleWithDetails(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "id",
        entityColumn = "sale_id",
        entity = SaleDetail::class
    )
    val items: List<SaleDetail>
)

data class DailySaleSummary(
    val saleDate: String,
    val total: Double
)