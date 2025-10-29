package com.trabajo.minitienda.data.model

import androidx.room.*

@Entity(tableName = "compra")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "proveedor_id") val proveedorId: Int,
    val fecha: Long = System.currentTimeMillis(),
    val total: Double
)

@Entity(
    tableName = "detalle_compra",
    primaryKeys = ["purchase_id", "product_id"],
    foreignKeys = [
        ForeignKey(
            entity = Purchase::class,
            parentColumns = ["id"],
            childColumns = ["purchase_id"],
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
data class PurchaseDetail(
    @ColumnInfo(name = "purchase_id") val purchaseId: Long,
    @ColumnInfo(name = "product_id") val productId: Int,
    val cantidad: Int,
    @ColumnInfo(name = "costo_unit") val costoUnit: Double,
    val subtotal: Double = cantidad * costoUnit
)

data class PurchaseWithDetails(
    @Embedded val compra: Purchase,
    @Relation(
        parentColumn = "id",
        entityColumn = "purchase_id",
        entity = PurchaseDetail::class
    )
    val detalles: List<PurchaseDetail>
)
