package com.trabajo.minitienda.repository

import com.trabajo.minitienda.data.dao.CategoryDao

class CategoryRepository(private val dao: CategoryDao) {
    fun observeAll() = dao.observeAll()
    suspend fun add(nombre: String) = dao.upsertByName(nombre)   // ← usa upsertByName
    suspend fun delete(id: Long) = dao.deleteById(id)            // ← deleteById
}

