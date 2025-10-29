package com.trabajo.minitienda.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trabajo.minitienda.data.dao.CategoryDao
import com.trabajo.minitienda.data.dao.ProductDao
import com.trabajo.minitienda.data.dao.PurchaseDao
import com.trabajo.minitienda.data.dao.SaleDao
import com.trabajo.minitienda.data.dao.SupplierDao
import com.trabajo.minitienda.data.model.Category
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.data.model.Purchase
import com.trabajo.minitienda.data.model.PurchaseDetail
import com.trabajo.minitienda.data.model.Sale
import com.trabajo.minitienda.data.model.SaleDetail
import com.trabajo.minitienda.data.model.Supplier

@Database(entities = [Product::class, Category::class, Sale::class, SaleDetail::class, Supplier::class, Purchase::class, PurchaseDetail::class], version = 7,exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun saleDao(): SaleDao

    abstract fun supplierDao(): SupplierDao

    abstract fun purchaseDao(): PurchaseDao

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