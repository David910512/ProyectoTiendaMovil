package com.trabajo.minitienda.data.dao


import androidx.room.*
import com.trabajo.minitienda.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // --- LECTURA REACTIVA ---
    @Query("SELECT * FROM producto ORDER BY name ASC")
    fun observeAllProducts(): Flow<List<Product>>

    // --- DIAGNÓSTICO: CUENTA ---
    @Query("SELECT COUNT(*) FROM producto")
    suspend fun countAll(): Int

    // --- CRUD BÁSICO ---
    @Delete suspend fun deleteProduct(product: Product)
    @Update suspend fun updateProduct(product: Product)

    // --- UPSERT POR 'code' ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(product: Product): Long

    @Query("""
        UPDATE producto
        SET name = :name, price = :price, stock = :stock, descripcion = :desc
        WHERE code = :code
    """)
    suspend fun updateByCode(name: String, price: Double, stock: Int, desc: String, code: String): Int

    @Query("SELECT * FROM producto WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): Product?

    @Query("UPDATE producto SET stock = stock - :qty WHERE id = :productId AND stock >= :qty")
    suspend fun decreaseStock(productId: Int, qty: Int): Int


    @Transaction
    suspend fun upsertByCode(p: Product) {
        val id = insertIgnore(p)
        if (id == -1L) {
            updateByCode(p.name, p.price, p.stock, p.descripcion, p.code)
        }
    }
}
