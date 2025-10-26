package com.trabajo.minitienda.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trabajo.minitienda.data.dao.CategoryDao
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.model.Category
import com.trabajo.minitienda.data.model.Product

@Database(entities = [Product::class, Category::class], version = 3,exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}

object AppDbProvider {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun get(context: android.content.Context): AppDatabase =
        INSTANCE ?: synchronized(this) {
            androidx.room.Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "minitienda.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
}