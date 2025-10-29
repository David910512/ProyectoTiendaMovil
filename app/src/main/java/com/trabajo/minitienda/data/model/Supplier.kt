package com.trabajo.minitienda.data.model

import androidx.room.*

@Entity(
    tableName = "proveedor",
    indices = [
        Index(value = ["ruc"], unique = true),
        Index(value = ["nombre"], unique = true)
    ]
)
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ruc: String,
    val nombre: String,
    val telefono: String? = null
)