package com.trabajo.minitienda.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.model.Product

@Database(entities = [Product::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}