package com.trabajo.minitienda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val code: String,
    val price: Double,
    val stock: Int,
    val descripcion: String
)
