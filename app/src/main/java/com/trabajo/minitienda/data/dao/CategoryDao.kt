package com.trabajo.minitienda.data.dao


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.trabajo.minitienda.data.model.Category

@Dao
interface CategoryDao {

    // LISTAR (reactivo)
    @Query("SELECT * FROM categoria ORDER BY nombre ASC")
    fun observeAll(): Flow<List<Category>>

    // BUSCAR
    @Query("SELECT * FROM categoria WHERE nombre = :nombre LIMIT 1")
    suspend fun findByNombre(nombre: String): Category?

    // INSERT (IGNORA si ya existe por índice único en nombre)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(c: Category): Long

    // UPDATE por nombre (opcional)
    @Query("UPDATE categoria SET nombre = :nuevo WHERE nombre = :anterior")
    suspend fun updateByName(nuevo: String, anterior: String): Int

    // ELIMINAR (elige el que prefieras)
    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM categoria WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    // UPSERT por nombre: inserta si no existe; si existe, devuelve su id
    @Transaction
    suspend fun upsertByName(nombre: String): Long {
        val trimmed = nombre.trim()
        val insertedId = insertIgnore(Category(nombre = trimmed))
        if (insertedId != -1L) return insertedId
        // ya existía: devuelve su id real
        return findByNombre(trimmed)?.id ?: -1L
    }
}

