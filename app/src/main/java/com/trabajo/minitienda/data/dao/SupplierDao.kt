package com.trabajo.minitienda.data.dao

import androidx.room.*
import com.trabajo.minitienda.data.model.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {

    @Query("SELECT * FROM proveedor ORDER BY nombre ASC")
    fun observeAll(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(s: Supplier): Long

    @Query("UPDATE proveedor SET nombre = :nuevo WHERE id = :id")
    suspend fun rename(id: Int, nuevo: String): Int

    @Query("SELECT * FROM proveedor WHERE ruc = :ruc LIMIT 1")
    suspend fun findByRuc(ruc: String): Supplier?

    @Query("UPDATE proveedor SET nombre = :nombre, telefono = :telefono WHERE ruc = :ruc")
    suspend fun updateByRuc(ruc: String, nombre: String, telefono: String?): Int


    @Delete
    suspend fun delete(s: Supplier)
}
